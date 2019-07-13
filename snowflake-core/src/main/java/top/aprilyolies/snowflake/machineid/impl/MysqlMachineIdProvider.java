package top.aprilyolies.snowflake.machineid.impl;

import top.aprilyolies.snowflake.machineid.AbstractMachineIdProvider;
import top.aprilyolies.snowflake.machineid.dao.MachineId;
import top.aprilyolies.snowflake.machineid.dao.MysqlMachineIdDao;

/**
 * @Author EvaJohnson
 * @Date 2019-07-12
 * @Email g863821569@gmail.com
 */
public class MysqlMachineIdProvider extends AbstractMachineIdProvider {
    // 构建 SqlSessionFactory，用于和数据库进行交互
    private final MysqlMachineIdDao dao;
    // 本机 ip
    private final String ipAddress;
    // machine id
    private int machineId = -1;

    public MysqlMachineIdProvider(MysqlMachineIdDao dao) {
        this.dao = dao;
        this.ipAddress = getIpAddress();
    }

    @Override
    public int buildMachineId() {
        return machineId;
    }

    @Override
    public void init() {
        MachineId machineIdByIpAddress = dao.getMachineIdByIpAddress(ipAddress);
        System.out.println(machineIdByIpAddress);
    }
}
