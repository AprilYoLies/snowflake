package top.aprilyolies.snowflake.machineid.dao;

/**
 * @Author EvaJohnson
 * @Date 2019-07-13
 * @Email g863821569@gmail.com
 */
public interface MysqlMachineIdDao {
    // 通过 ip address 获取 machine id
    MachineId getMachineIdByIpAddress(String ipAddress);
}
