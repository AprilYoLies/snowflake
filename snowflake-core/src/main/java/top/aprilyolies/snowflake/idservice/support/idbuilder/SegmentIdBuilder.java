package top.aprilyolies.snowflake.idservice.support.idbuilder;

import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @Author EvaJohnson
 * @Date 2019-07-13
 * @Email g863821569@gmail.com
 */
public class SegmentIdBuilder implements IdBuilder {
    // 数据库会话工厂，用于和数据库交互
    private final SqlSessionFactory sessionFactory;

    public SegmentIdBuilder(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public String buildId() {
        return null;
    }
}
