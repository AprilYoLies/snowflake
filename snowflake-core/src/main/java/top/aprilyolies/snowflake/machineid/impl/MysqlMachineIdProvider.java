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
        MachineId machineIdObj;
        machineIdObj = dao.getMachineIdByIpAddress(ipAddress);
        if (machineIdObj == null) {
            int res = dao.updateMachineIdByIpAddress(ipAddress);
            if (res > 0) {
                machineIdObj = dao.getMachineIdByIpAddress(ipAddress);
                if (machineIdObj != null) {
                    machineId = machineIdObj.getMachineId();
                } else {
                    logger.warn("Can't get machine id from database, try to fetch machine id from local cache");
                    machineId = fetchMachineIdFromLocalCache();
                }
            } else {
                logger.warn("Can't get machine id from database, try to fetch machine id from local cache");
                machineId = fetchMachineIdFromLocalCache();
                checkMachineId(machineId);
                return;
            }
        } else {
            machineId = machineIdObj.getMachineId();
            checkMachineId(machineId);
            return;
        }
        checkMachineId(machineId);
        if (storeOrUpdateMachineId(machineId)) {
            logger.info("Store machine id in {} successfully", LOCAL_MACHINE_ID_FILE);
        } else {
            logger.warn("Can't store machine id info in {}", LOCAL_MACHINE_ID_FILE);
        }
    }
}
