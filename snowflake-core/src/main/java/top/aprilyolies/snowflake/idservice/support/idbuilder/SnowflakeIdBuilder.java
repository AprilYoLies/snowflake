package top.aprilyolies.snowflake.idservice.support.idbuilder;

import top.aprilyolies.snowflake.idservice.support.IdModel;
import top.aprilyolies.snowflake.idservice.support.TimeSupport;
import top.aprilyolies.snowflake.machineid.MachineIdProvider;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public class SnowflakeIdBuilder implements IdBuilder {
    // 时间戳
    private final TimeSupport timeSupport;
    // 序列号（类型如果是 int，将会导致错误）
    private long serial = 0L;
    // 机器 id 生成器
    private final MachineIdProvider machineIdProvider;
    // id 的类型
    private final long idType;
    // id 的数据结构模型
    private IdModel model;

    public SnowflakeIdBuilder(long idType, TimeSupport timeSupport, MachineIdProvider machineIdProvider) {
        this.idType = idType;
        this.timeSupport = timeSupport;
        this.machineIdProvider = machineIdProvider;
        model = IdModel.parseModel(idType);
    }

    // 在多线程访问的情况下，lastTime 和 serial 可能导致并发问题，需要进行枷锁
    @Override
    public synchronized String buildId() {
        // 拿到 machine id
        int machineId = machineIdProvider.buildMachineId(); // 拿到 machine id
        long curTime = timeSupport.getTime();   // 获取当前时间
        if (curTime == timeSupport.getLastTime()) {  //  如果时间重复了，需要通过序列号来区分
            serial++;
            serial &= model.getSerialMask();    // 如果序列号超过了上界，那就必须要等待到下一个最小时刻了
            if (serial == 0) {
                curTime = timeSupport.waitUntilNextTimeUnit();  // 线程自旋到下一个最小时间单元
            }
        } else {
            serial = 0;
        }
        timeSupport.setLastTime(curTime);
        long id = 0;
        id |= (curTime << model.getTimePos());
        id |= (serial << model.getSerialPos());
        id |= machineId;
        return String.valueOf(id);
    }
}
