## snowflake 简介

snowflake 是一款分布式的统一 ID 生成器，提供了两种 id 生成的方式，分段 id 和通过 snowflake 算法计算得到 id。在分段 id 的生成过程中，不同的服务器从数据库获取自己专属的 id 号段，再从该号段中来产生唯一 id，在从数据库中获取号段的过程中，通过数据库的事务机制，保证了不同的 id 服务器获取的号段是不重复的，所以能够保证全局的 id 唯一性。而 snowflake 算法计算 id 的方式稍微复杂，首先它并不是通过 id 隔段的方式来保证 id 的唯一性，而是通过各个 id 服务器的 machine id 来保证不同的服务器之间产生的 id 唯一性，此外，在单台 id 服务器之上，machine id 是唯一且不变的，要想产生唯一 id，就还需要其他的项来进行保证，snowflake 算法采用的是加上时间戳的方式，在本项目的实现中，提供了两种时间颗粒度的选择（秒和毫秒），对于计算机世界而言一秒和一毫秒而言，是很漫长的，在这样的时间颗粒度下很有可能会在同一个最小时间单元内生成多个 id，因为时间戳和 machine id 一致，所以在我们生成的 id 中，还需要一个额外的字段来保证在单位时间粒度中生成的 id 的唯一性，这就是序列号。snowflake 算法生成 id 相关的三个字段如上所述，更加详细的 id 模型请看下表：

| 颗粒度 | 时间戳 | 序列号 | machine id | 
| :------ | :------ | :------ | :------ |
| 秒 | 占用 31 位 | 占用 22 位 | 占用 10 位 |
| 毫秒 | 占用 41 位 | 占用 12 位 | 占用 10 位 |

从上表可以看出来两种颗粒度的 id 占用的长度都是 63 位数，刚好可以使用一个长整型数据来进行存储，剩余一位没有使用，方便以后拓展使用。对于秒颗粒度而言，一年 31536000 秒占用 25 位，时间戳剩余 6 位，也就是说这种方式产生的 id 能用 64 年（非精确计算），而序列号占用 22 位，折算后可以知道理论情况下，一秒内单台 id 服务器最多能生成 4194303 个 id。对于毫秒颗粒度而言，一年 31536000000 毫秒占用 35 位，剩余 6 位，同样可以使用 64 年（非精确计算），由于时间戳长度的增加导致序列号的长度被压缩为 12 位，这样理论情况下，单台 id 服务器一毫秒内最多能产生 4096 个 id。

## snowflake 基本使用

### 服务发布模式

服务发布模式就是将 snowflake 利用 RPC 框架作为服务发布，这样其他的系统就能通过服务调用的方式来获取 id，常用的 RPC 框架有 dubbo、gRPC、spring Cloud 等，本项目中给出的测试用例是使用的本人写的一个 RPC 框架 [beehive](https://github.com/AprilYoLies/beehive)，至于其他的 rpc 框架的使用方式，参考实例程序 snowflake-demo/embed-server 即可。

``` java
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:beehive="https://www.aprilyolies.top/schema/beehive"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd https://www.aprilyolies.top/schema/beehive https://www.aprilyolies.top/schema/beehive.xsd">

    <bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="locations" value="classpath:snowflake.properties"/>
    </bean>

    <bean id="serviceFactory" class="top.aprilyolies.snowflake.idservice.IdServiceFactory">
        <property name="machineIdProvider" value="ZOOKEEPER"/>
        <property name="idType" value="1"/>
        <property name="zkHost" value="119.23.247.86"/>
        <property name="dbUrl" value="jdbc:mysql://localhost:3306/snowflake"/>
        <property name="username" value="root"/>
        <property name="password" value="kuaile1.."/>
        <property name="serviceType" value="snowflake"/>
    </bean>

    <beehive:service id="idService" service="top.aprilyolies.snowflake.idservice.IdService"
                     ref="serviceFactory" proxy-factory="jdk" serializer="hessian" server-port="7442"/>

    <beehive:registry id="registry" address="zookeeper://119.23.247.86:2181"/>
</beans>
```

用于产生 id 的服务接口是 `top.aprilyolies.snowflake.idservice.IdService`，具体的实现是通过 `top.aprilyolies.snowflake.idservice.IdServiceFactory` 来进行构建的，相关的配置也就是设置在了 IdServiceFactory 实例中，具体的配置说明如下：

``` java
<property name="machineIdProvider" value="ZOOKEEPER"/>  // machine id 的生成方式，支持 MYSQL,ZOOKEEPER,PROPERTY 三种方式
<property name="idType" value="1"/> // 生成的 id 颗粒度，0 代表秒级颗粒度，1 代表毫秒级颗粒度
<property name="zkHost" value="119.23.247.86"/> // zookeeper 的地址
<property name="dbUrl" value="jdbc:mysql://localhost:3306/snowflake"/>  // 数据库地址
<property name="username" value="root"/>    // 数据库用户名
<property name="password" value="123456"/>   // 数据库密码
<property name="serviceType" value="snowflake"/>    // 生成 id 的方式，支持 segment 和 snowflake 两种
```

### 独立 Server 启动模式

独立 Server 启动模式提供了 Tomcat 和 Netty 两种 Server 的支持，任意启动其中一个即可，获取 id 的方式符合 restful 风格，如果你需要根据自己的需求进行定制，那么也很简单，只需要在 classpath 下创建一个 snowflake 的配置文件 `snowflake.properties`，里边的内容如下：

``` java
# 在 snowflake.machine.id.provider 配置为 PROPERTY 模式时，将会使用该值（其他情况下，可以忽视）
snowflake.machine.id=1023
# machine id 的生成方式，支持 MYSQL,ZOOKEEPER,PROPERTY 三种方式
snowflake.machine.id.provider=MYSQL
# 生成的 id 颗粒度，0 代表秒级颗粒度，1 代表毫秒级颗粒度
snowflake.id.type=0
# zookeeper 的地址
snowflake.zookeeper.host=119.23.247.86
# 数据库地址
snowflake.database.url=jdbc:mysql://localhost:3306/snowflake
# 数据库用户名
snowflake.database.username=root
# 数据库密码
snowflake.database.password=123456
```

接下来就是获取 IdService 接口的实例，只需要通过下边的代码获取，拿到实现类后进行获取 id 的方法调用，就可以得到唯一 id 了。
``` java
// serviceType 为 segment 或者 snowflake，confPath 为 snowflake.properties 的路径
top.aprilyolies.snowflake.idservice.IdServiceFactory#buildIdService(java.lang.ClassLoader classLoader, java.lang.String seviceType, java.lang.String confPath)");
```

## TODO-LIST
* 在通过 snowflake 算法生成 id 时，由于需要通过时间戳来生成 id，这就有可能会因为某一个机器的时间发生回拨，导致这台机器产生出重复的 id，有两种解决办法，一个是在每次生成 id 时，都检查一次当前时间戳是否比上一次生成 id 的时间戳小，如果条件成立，就拒绝 id 生成，这种方式的好处是，在服务不重启的情况下也能检查出这种问题，缺陷是获取当前时间戳和时间比对是一个耗时操作，会导致吞吐量下降。另外一种方式是每隔一小段时间向数据库中写入当前机器生成 id 的时间戳，在服务重启时需要先检查当前时间戳是否比上一次生成 id 的时间戳小，如果成立，则拒绝启动服务，这种做法能够避免服务重启时的时间回拨问题，但是如果服务是在运行中，发生时间回拨，这种方式将会产生错误。

* 在 segment 方式生成 id 的过程中，代码是使用的硬编码方式将 step 值写死，对于不同的应用场景，可能该步进值并不合适，最好是系统能够根据两次获取 id 段的时间差来自动调整步进值，这样能够动态的缓解 id 段服务数据库的压力。

* 可以再为系统增加一个可视化管理界面，能够直观的看到不同业务的 id 段使用情况，以及 id 段缓存的使用情况。







