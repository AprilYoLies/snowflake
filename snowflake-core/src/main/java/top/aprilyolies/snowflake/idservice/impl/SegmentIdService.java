package top.aprilyolies.snowflake.idservice.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import top.aprilyolies.snowflake.idservice.AbstractIdService;
import top.aprilyolies.snowflake.idservice.support.idbuilder.IdBuilder;
import top.aprilyolies.snowflake.idservice.support.idbuilder.SegmentIdBuilder;

/**
 * @Author EvaJohnson
 * @Date 2019-07-13
 * @Email g863821569@gmail.com
 */
public class SegmentIdService extends AbstractIdService {
    private final IdBuilder builder;

    public SegmentIdService(SqlSessionFactory sessionFactory) {
        this.builder = new SegmentIdBuilder(sessionFactory);
    }

    @Override
    public String generateId() {
        return builder.buildId();
    }
}
