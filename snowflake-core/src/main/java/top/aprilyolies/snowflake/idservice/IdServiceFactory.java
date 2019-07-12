package top.aprilyolies.snowflake.idservice;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;
import top.aprilyolies.snowflake.idservice.impl.SnowflakeIdService;
import top.aprilyolies.snowflake.idservice.support.MachineIdProviderType;
import top.aprilyolies.snowflake.machineid.MachineIdProvider;
import top.aprilyolies.snowflake.machineid.impl.PropertyMachineIdProvider;
import top.aprilyolies.snowflake.machineid.impl.ZookeeperMachineIdProvider;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public class IdServiceFactory implements FactoryBean {
    // 服务的类型
    private String serviceType;
    // id 的类型
    private String idType;
    // machine id 的生成器
    private MachineIdProviderType machineIdProvider;
    // zookeeper 的地址
    private String zkHost;
    // 数据库用户名
    private String username;
    // 数据库密码
    private String passwd;
    // 采用 snowflake 算法生成 id 的服务
    private static final String SNOWFLAKE_SERVICE = "snowflake";
    // 采用分段的方式生成 id 的服务
    private static final String SEGMENT_SERVICE = "segment";

    @Override
    public Object getObject() throws Exception {
        if (StringUtils.isEmpty(serviceType)) {
            throw new IllegalArgumentException("Service type should not be null");
        } else {
            MachineIdProvider machineIdProvider = parseIdProvider(this.machineIdProvider);
            long idType = Long.parseLong(this.idType);
            if (SNOWFLAKE_SERVICE.equals(serviceType)) {
                return new SnowflakeIdService(idType, machineIdProvider);
            } else if (SEGMENT_SERVICE.equals(serviceType)) {
                return null;
            } else {
                throw new IllegalStateException("Unsupported service type");
            }
        }
    }

    /**
     * 根据 machineIdProvider 解析出 MachineIdProvider 实例
     *
     * @param machineIdProvider 字符串类型的 machineIdProvider
     * @return 匹配出来的 MachineIdProvider 实例
     */
    private MachineIdProvider parseIdProvider(MachineIdProviderType machineIdProvider) {
        if (StringUtils.isEmpty(machineIdProvider))
            throw new IllegalArgumentException("Machine id type should not be null");
        switch (machineIdProvider) {
            case PROPERTY:
                return new PropertyMachineIdProvider();
            case ZOOKEEPER:
                ZookeeperMachineIdProvider provider = new ZookeeperMachineIdProvider(this.zkHost);
                provider.init();
                return provider;
            default:
                throw new IllegalArgumentException("None of machine id provider could be found");
        }
    }

    @Override
    public Class<?> getObjectType() {
        return IdService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public void setMachineIdProvider(MachineIdProviderType machineIdProvider) {
        this.machineIdProvider = machineIdProvider;
    }

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
