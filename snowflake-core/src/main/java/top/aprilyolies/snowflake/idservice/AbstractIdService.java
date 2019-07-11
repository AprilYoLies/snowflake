package top.aprilyolies.snowflake.idservice;

import top.aprilyolies.snowflake.machineid.MachineIdProvider;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public abstract class AbstractIdService implements IdService {
    // 用于提供 machine id
    protected MachineIdProvider machineIdProvider;


}
