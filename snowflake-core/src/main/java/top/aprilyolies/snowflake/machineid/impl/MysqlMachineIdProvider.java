package top.aprilyolies.snowflake.machineid.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import top.aprilyolies.snowflake.machineid.AbstractMachineIdProvider;

/**
 * @Author EvaJohnson
 * @Date 2019-07-12
 * @Email g863821569@gmail.com
 */
public class MysqlMachineIdProvider extends AbstractMachineIdProvider {
    private final SqlSessionFactory sessionFactory;

    public MysqlMachineIdProvider(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public int buildMachineId() {
        return 0;
    }

    @Override
    public void init() {
        // empty
    }
}
