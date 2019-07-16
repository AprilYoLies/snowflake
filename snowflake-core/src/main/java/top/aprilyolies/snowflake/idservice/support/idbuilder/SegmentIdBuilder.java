package top.aprilyolies.snowflake.idservice.support.idbuilder;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.aprilyolies.snowflake.idservice.impl.support.SegmentInfo;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;

/**
 * @Author EvaJohnson
 * @Date 2019-07-13
 * @Email g863821569@gmail.com
 */
public class SegmentIdBuilder implements IdBuilder {
    Logger logger = LoggerFactory.getLogger(SegmentIdBuilder.class);
    // 数据库会话工厂，用于和数据库交互
    private final SqlSessionFactory sessionFactory;
    // idBuilder 所对应的业务名称
    private final String business;
    // 代表本地的两个缓存
    private final Segment[] segments = new Segment[]{new Segment(), new Segment()};
    // 判定 builder 是否初始化过，即第一个 segment 是否初始化完成
    private boolean initialized = false;
    // 当前使用的缓存的索引下标
    private int cur = 0;
    // 下一个将要使用的 segment 的索引
    private int next = 1;
    // 步进值
    private int step = 5000;
    // 用于后台更新 segment 的线程池
    private Executor executor = Executors.newCachedThreadPool(new SegmentTaskThreadFactory());
    // 记录 segment 加载的状态
    private boolean loading = false;

    private Semaphore loaded = new Semaphore(0);

    public SegmentIdBuilder(SqlSessionFactory sessionFactory, String business) {
        this.sessionFactory = sessionFactory;
        this.business = business;
    }

    public boolean init() {
        if (!initialized) {
            Segment segment = segments[cur];
            SegmentInfo segmentInfo = getSegmentInfoFromDB(business, step);
            segment.init(segmentInfo.getBegin(), segmentInfo.getEnd(), segmentInfo.getBusiness());
            return initialized = true;
        }
        return initialized = false;
    }

    // 根据 business 从数据库中获取对应的数据记录
    public SegmentInfo getSegmentInfoFromDB(String business, int step) {
        SqlSession session = sessionFactory.openSession();
        session.update("top.aprilyolies.snowflake.idservice.impl.support.SegmentIdMapper.updateSegmentTable", step);
        SegmentInfo segmentInfo = session.selectOne("top.aprilyolies.snowflake.idservice.impl.support.SegmentIdMapper.getSegmentInfoFromDB", business);
        session.commit();
        session.close();    // 如果关闭会话，会导致线程阻塞
        return segmentInfo;
    }

    @Override
    public synchronized String buildId() {
        if (initialized) {  // 判断 SegmentIdBuilder 是否初始化完成
            Segment segment = segments[cur];    // 获取当前使用的 segment
            if (segment.initialized) {  // 看当前 segment 是否初始化完成
                if (!loading) { // 如果初始化完成的话，看另外一个 segment 是否是在加载中（避免重复提交加载任务）
                    long used = segment.cur - segment.begin;    // 如果不是在加载中
                    if (step * 0.2 < used) {    // 看当前 segment 是否使用超过了 20%
                        executor.execute(new LoadSegmentTask(next));    // 超过了 20% 的话，就提交 segment 后台加载任务
                        loading = true; // 设置 segment 正在加载的状态
                    }
                }
                return segment.getId(); // 从当前使用的 segment 中获取 id
            } else {    // 执行到这里说明当前正在使用的 segment id 已经用光了
                loading = false;
                changeSegment();    // 切换到备用的 segment
                segment = segments[cur];    // 看备用的 segment 是否加载完毕
                if (segment.initialized) {  // 如果加载完毕
                    waitSegmentLoaded();    // 这里是为了防止加载任务提前完成，导致下一个 wait 操作提前释放
                    return segment.getId();
                } else {    // 如果没有加载完毕
                    waitSegmentLoaded();    // 等待加载任务完成
                    return segment.getId();
                }
            }
        } else {
            throw new IllegalStateException("Segment id builder hasn't been initialized");
        }
    }

    // 等待 segment 加载完成
    private void waitSegmentLoaded() {
        try {
            loaded.acquire();   // 等待任务完成
        } catch (InterruptedException e) {
            logger.error("Waiting for segment loaded was interrupted by other thread");
        }
    }

    // 该 Task 用于后台更新 segment 的信息
    private class LoadSegmentTask implements Runnable {
        private final int idx;

        public LoadSegmentTask(int next) {
            this.idx = next;
        }

        @Override
        public void run() {
            Segment segment = segments[idx];
            SegmentInfo segmentInfo = getSegmentInfoFromDB(business, step);
            segment.init(segmentInfo.getBegin(), segmentInfo.getEnd(), segmentInfo.getBusiness());
            segment.initialized = true;
            loaded.release();
        }
    }

    // segment 后台更新线程工厂
    private class SegmentTaskThreadFactory implements ThreadFactory {
        int count = 0;

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("segment-updater-" + count++);
            return thread;
        }
    }

    // 切换正在使用的 segment
    private void changeSegment() {
        int temp = cur;
        cur = next;
        next = temp;
    }

    private class Segment {
        // 业务名称
        private volatile String business;
        // 起始 id 号
        private volatile long begin;
        // 终止 id 号
        private volatile long end;
        // 当前 id
        private long cur;

        private volatile boolean initialized = false;


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
