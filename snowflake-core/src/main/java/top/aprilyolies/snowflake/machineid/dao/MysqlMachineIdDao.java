package top.aprilyolies.snowflake.machineid.dao;

/**
 * @Author EvaJohnson
 * @Date 2019-07-13
 * @Email g863821569@gmail.com
 */
public interface MysqlMachineIdDao {
    // 通过 ip address 获取 machine id（已存在）
    MachineId getMachineIdByIpAddress(String ipAddress);

    // 通过 ip address 新建 MachineId （不存在）
    int updateMachineIdByIpAddress(String ipAddress);
}
