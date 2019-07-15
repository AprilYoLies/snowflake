package top.aprilyolies.snowflake.idservice.impl;

import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.aprilyolies.snowflake.idservice.AbstractIdService;
import top.aprilyolies.snowflake.idservice.support.idbuilder.IdBuilder;
import top.aprilyolies.snowflake.idservice.support.idbuilder.SegmentIdBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author EvaJohnson
 * @Date 2019-07-13
 * @Email g863821569@gmail.com
 */
public class SegmentIdService extends AbstractIdService {
    Logger logger = LoggerFactory.getLogger(SegmentIdService.class);
    // 会话工厂，用于和数据库进行交互
    private final SqlSessionFactory sessionFactory;
    // 用于缓存 SegmentIdBuilder
    Map<String, IdBuilder> builders = new HashMap<>();

    public SegmentIdService(SqlSessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public String generateId() {
        throw new UnsupportedOperationException("Only snowflake id provider could call this method, segment id provider should call " +
                "top.aprilyolies.snowflake.idservice.impl.SegmentIdService.generateId(java.lang.String business)");
    }

    /**
     * 生成 id，优先从缓存中获取 business 对应的 idBuilder，如果没有的话就直接构建一个
     *
     * @param business 业务对应的名称
     * @return 生成的 id
     */
    @Override
    public String generateId(String business) {
        if (builders.containsKey(business)) {
            return builders.get(business).buildId();
        } else {    // 缓存中没有对应的 idBuilder，所以直接构建一个，完成初始化后，用它来返回 id
            synchronized (this) {
                if (!builders.containsKey(business)) {
                    return builders.computeIfAbsent(business, (t) -> {
                        SegmentIdBuilder builder = new SegmentIdBuilder(sessionFactory, business);
                        if (builder.init()) {
                            return builder;
                        } else {
                            logger.warn("Segment id builder was build successfully, but initialize failed");
                            return builder;
                        }
                    }).buildId();
                }
            }
            return builders.get(business).buildId();
        }
    }
}
