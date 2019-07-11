package top.aprilyolies.snowflake.machineid.impl;

import top.aprilyolies.snowflake.common.SnowflakeConstants;
import top.aprilyolies.snowflake.machineid.AbstractMachineIdProvider;
import top.aprilyolies.snowflake.utils.PropertyUtils;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */

/**
 * 从配置文件中获取 machine id
 */
public class PropertyMachineIdProvider extends AbstractMachineIdProvider {
    // 从配置文件中获取 machine id
    @Override
    public int buildMachineId() {
        try {
            ClassLoader cl = PropertyMachineIdProvider.class.getClassLoader();
            String machineId = PropertyUtils.loadProperty(cl, SnowflakeConstants.DEFAULT_SNOWFLAKE_PROPERTY,
                    SnowflakeConstants.SNOWFLAKE_MACHINE_ID);
            return checkMachineId(Integer.parseInt(machineId));
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Can't build machine id, please check the snowflake config file is existed, default is " +
                    "snowflake.properties, and make sure that machine id has specified via snowflake.machine.id=xxx, xxx is a number " +
                    "between 0 and 1023 and different from other machine");
        }
    }
}
