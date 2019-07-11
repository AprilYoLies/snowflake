package top.aprilyolies.snowflake.idservice.support.idbuilder;

import top.aprilyolies.snowflake.idservice.support.IdModel;
import top.aprilyolies.snowflake.machineid.MachineIdProvider;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public class SnowflakeIdBuilder implements IdBuilder {
    // 时间戳
    private final long timeStamp;
    // 序列号（类型如果是 int，将会导致错误）
    private final long serial;
    // 机器 id 生成器
    private final MachineIdProvider machineIdProvider;
    // id 的类型
    private final long idType;

    public SnowflakeIdBuilder(long idType, long timeStamp, long serial, MachineIdProvider machineIdProvider) {
        this.idType = idType;
        this.timeStamp = timeStamp;
        this.serial = serial;
        this.machineIdProvider = machineIdProvider;
    }

    @Override
    public String buildId() {
        // 获取 IdModel
        IdModel model = IdModel.parseModel(idType);
        // 拿到 machine id
        int machineId = machineIdProvider.buildMachineId();
        long id = 0;
        id |= (timeStamp << model.getTimePos());
        id |= (serial << model.getSerialPos());
        id |= machineId;
        return String.valueOf(id);
    }
}
