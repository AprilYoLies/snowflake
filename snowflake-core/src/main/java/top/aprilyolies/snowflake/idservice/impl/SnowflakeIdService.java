package top.aprilyolies.snowflake.idservice.impl;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */

import top.aprilyolies.snowflake.idservice.AbstractIdService;
import top.aprilyolies.snowflake.idservice.support.TimeSupport;
import top.aprilyolies.snowflake.idservice.support.idbuilder.IdBuilder;
import top.aprilyolies.snowflake.idservice.support.idbuilder.SnowflakeIdBuilder;
import top.aprilyolies.snowflake.machineid.MachineIdProvider;

/**
 * 通过 snowflake 算法生成唯一 id 的服务
 * 该方式生成的唯一 id（占用 64 位长度，用一个 long 类型数据存储）的格式类型如下
 * <p>
 * 最大峰值型：
 * 1位（标识颗粒度）      占用 31 位     占用 22 位     10 位（1024 台机器）
 * 生成id的模式     -    时间戳    -    序列号     -      机器id
 * <p>
 * 最小粒度型：
 * 1位（标识颗粒度）      占用 41 位     占用 12 位     10 位（1024 台机器）
 * 生成id的模式     -    时间戳    -    序列号     -      机器id
 */
public class SnowflakeIdService extends AbstractIdService {
    // snowflake 算法的 id 生成器（该实例是单例的）
    private final IdBuilder builder;

    public SnowflakeIdService(long idType, MachineIdProvider machineIdProvider) {
        super(machineIdProvider);
        this.timeSupport = new TimeSupport(idType);
        this.builder = new SnowflakeIdBuilder(idType, timeSupport, machineIdProvider);
    }

    @Override
    public String generateId() {
        return builder.buildId();
    }
}
