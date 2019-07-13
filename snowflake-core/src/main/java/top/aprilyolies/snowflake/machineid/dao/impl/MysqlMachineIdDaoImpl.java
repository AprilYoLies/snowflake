package top.aprilyolies.snowflake.machineid.dao.impl;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.aprilyolies.snowflake.machineid.dao.MachineId;
import top.aprilyolies.snowflake.machineid.dao.MysqlMachineIdDao;

/**
 * @Author EvaJohnson
 * @Date 2019-07-13
 * @Email g863821569@gmail.com
 */

public class MysqlMachineIdDaoImpl implements MysqlMachineIdDao {
    Logger logger = LoggerFactory.getLogger(MysqlMachineIdDaoImpl.class);

    private final SqlSessionFactory sessionFactory;

    public MysqlMachineIdDaoImpl(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public MachineId getMachineIdByIpAddress(String ipAddress) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sessionFactory.openSession();
            return sqlSession.selectOne("top.aprilyolies.snowflake.machineid.dao.MysqlMachineIdMapper.getMachineIdByIpAddress", ipAddress);
        } catch (Exception e) {
            logger.error("Can't get machine id by ip address {}, return null", ipAddress);
            return null;
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }

    @Override
    public int updateMachineIdByIpAddress(String ipAddress) {
        SqlSession sqlSession = null;
        try {
            sqlSession = sessionFactory.openSession();
            int count = sqlSession.update("top.aprilyolies.snowflake.machineid.dao.MysqlMachineIdMapper.updateMachineIdByIpAddress", ipAddress);
            sqlSession.commit();
            return count;
        } catch (Exception e) {
            logger.error("Can't get machine id by ip address {}, return null", ipAddress);
            return -1;
        } finally {
            if (sqlSession != null) {
                sqlSession.close();
            }
        }
    }
}
