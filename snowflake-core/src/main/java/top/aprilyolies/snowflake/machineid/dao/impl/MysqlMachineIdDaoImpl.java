package top.aprilyolies.snowflake.machineid.dao.impl;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import top.aprilyolies.snowflake.machineid.dao.MachineId;
import top.aprilyolies.snowflake.machineid.dao.MysqlMachineIdDao;

/**
 * @Author EvaJohnson
 * @Date 2019-07-13
 * @Email g863821569@gmail.com
 */

public class MysqlMachineIdDaoImpl implements MysqlMachineIdDao {
    private final SqlSessionFactory sessionFactory;

    public MysqlMachineIdDaoImpl(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public MachineId getMachineIdByIpAddress(String ipAddress) {
        SqlSession sqlSession = sessionFactory.openSession();
        return sqlSession.selectOne("top.aprilyolies.snowflake.machineid.dao.MysqlMachineIdMapper.getMachineIdByIpAddress", ipAddress);
    }
}
