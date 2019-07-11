package top.aprilyolies.snowflake.machineid;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */

/**
 * machine id 生成器的接口，实现类实现对应的额 machine id 生成逻辑，注意生成的 machine id 的值应该限制在 0 - 1023 之间
 */
public interface MachineIdProvider {
    // 生成 machine id
    int buildMachineId();
}
