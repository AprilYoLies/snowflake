package top.aprilyolies.snowflake.idservice;

import top.aprilyolies.snowflake.idservice.support.TimeSupport;
import top.aprilyolies.snowflake.machineid.MachineIdProvider;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public abstract class AbstractIdService implements IdService {
    // 用于提供 machine id
    protected MachineIdProvider machineIdProvider;
    // 时间戳生成器
    protected TimeSupport timeSupport;

    public AbstractIdService(MachineIdProvider machineIdProvider) {
        this.machineIdProvider = machineIdProvider;
    }
}
