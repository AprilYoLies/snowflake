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

    private long lastTime = -1;

    public SnowflakeIdBuilder(long idType, TimeSupport timeSupport, MachineIdProvider machineIdProvider) {
        this.idType = idType;
        this.timeSupport = timeSupport;
        this.machineIdProvider = machineIdProvider;
        model = IdModel.parseModel(idType);
    }

    @Override
    public String buildId() {
        // 拿到 machine id
        int machineId = machineIdProvider.buildMachineId();
        long curTime = timeSupport.getTime();
        if (curTime == lastTime) {
            serial++;
            serial &= model.getSerialMask();
            if (serial == 0) {
                curTime = timeSupport.waitUntilNextTimeUnit();
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
