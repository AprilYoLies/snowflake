package top.aprilyolies.snowflake.idservice.support.idbuilder;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import top.aprilyolies.snowflake.idservice.impl.support.SegmentInfo;

/**
 * @Author EvaJohnson
 * @Date 2019-07-13
 * @Email g863821569@gmail.com
 */
public class SegmentIdBuilder implements IdBuilder {
    // 数据库会话工厂，用于和数据库交互
    private final SqlSessionFactory sessionFactory;
    // idBuilder 所对应的业务名称
    private final String business;
    // 代表本地的两个缓存
    private final Segment[] segments = new Segment[]{new Segment(), new Segment()};

    private boolean initialized = false;
    // 当前使用的缓存的索引下标
    private int cur = 0;

    public SegmentIdBuilder(SqlSessionFactory sessionFactory, String business) {
        this.sessionFactory = sessionFactory;
        this.business = business;
    }

    public boolean init() {
        if (!initialized) {
            SegmentInfo segmentInfo = getSegmentInfoFromDB(business);
            Segment segment = segments[cur];
            segment.init(segmentInfo.getBegin(), segmentInfo.getEnd(), segmentInfo.getBusiness());
            return initialized = true;
        }
        return initialized = false;
    }

    // 根据 business 从数据库中获取对应的数据记录
    public SegmentInfo getSegmentInfoFromDB(String business) {
        SqlSession session = sessionFactory.openSession();
        session.update("top.aprilyolies.snowflake.idservice.impl.support.SegmentIdMapper.updateSegmentTable");
        return session.selectOne("top.aprilyolies.snowflake.idservice.impl.support.SegmentIdMapper.getSegmentInfoFromDB", business);
    }

    @Override
    public synchronized String buildId() {
        Segment segment = segments[cur];
        if (segment.initialized) {
            return segment.getId();
        } else {
            cur = ++cur & 1;
            segment = segments[cur];
            return segment.getId();
        }
    }

    private class Segment {
        // 业务名称
        private String business;
        // 起始 id 号
        private long begin;
        // 终止 id 号
        private long end;
        // 当前 id
        private long cur;

        private boolean initialized = false;

        public String getBusiness() {
            return business;
        }

        public void setBusiness(String business) {
            this.business = business;
        }

        public long getBegin() {
            return begin;
        }

        public void setBegin(long begin) {
            this.begin = begin;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        public void init(long begin, long end, String business) {
            this.begin = begin;
            this.end = end;
            this.business = business;
            this.cur = begin;
            initialized = true;
        }

        public String getId() {
            if (cur <= end) {
                String id = String.valueOf(cur++);
                if (cur > end) {
                    initialized = false;
                }
                return id;
            } else {
                throw new IllegalStateException("Current segment hasn't been initialized");
            }
        }
    }
}
