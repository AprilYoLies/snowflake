package top.aprilyolies.snowflake.idservice.support;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public class TimeSupport {
    // id 的类型
    private final long idType;
    // 秒元时间
    private final long SECONDS_EPOCH = 1560000000L;
    // 毫秒元时间
    private final long MILLISECONDS_EPOCH = 1560000000000L;
    // id 数据结构模型
    private final IdModel model;
    // 秒级 id 的时间戳最大值
    private final long maxTimes;
    // 上一个获取的时刻
    private long lastTime;

    public TimeSupport(long idType) {
        this.idType = idType;
        this.model = IdModel.parseModel(idType);
        this.maxTimes = (1 << (model.getTypePos() - model.getTimePos())) - 1;
    }

    // 根据 id 的类型获取时间戳
    public long getTime() {
        long curTime;
        if (idType == 0) {
            curTime = System.currentTimeMillis() / 1000;
            checkTime(curTime);
            return curTime - SECONDS_EPOCH;
        } else {
            curTime = System.currentTimeMillis();
            checkTime(curTime);
            return curTime - MILLISECONDS_EPOCH;
        }
    }

    // 检查当前时间是否已经超过了 maxTimes
    private void checkTime(long curSeconds) {
        if (curSeconds > maxTimes) {
            throw new IllegalStateException("Current time is greater than maxTimes");
        }
    }

    // 线程自旋到下一个最小时间单元
    public long waitUntilNextTimeUnit() {
        long curTime = getTime();
        while (curTime == lastTime) {
            curTime = getTime();
        }
        checkTime(curTime);
        return curTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }
}
