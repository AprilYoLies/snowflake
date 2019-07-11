package top.aprilyolies.snowflake.idservice.impl;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */

import top.aprilyolies.snowflake.idservice.AbstractIdService;
import top.aprilyolies.snowflake.idservice.support.idbuilder.SnowflakeIdBuilder;

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
    // id 的类型（最大峰值型或最小粒度型）
    private int idType;
    // 时间戳
    private long timeStamp;
    // 序列号
    private int serial;

    @Override
    public String generateId() {
        SnowflakeIdBuilder builder = new SnowflakeIdBuilder(idType, timeStamp, serial, machineIdProvider);
        return null;
    }
}
