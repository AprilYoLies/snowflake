package top.aprilyolies.snowflake.idservice.impl.support;

/**
 * @Author EvaJohnson
 * @Date 2019-07-14
 * @Email g863821569@gmail.com
 */
public class SegmentInfo {
    // 业务名称
    private String business;
    // 起始 id 号
    private long begin;
    // 终止 id 号
    private long end;

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
}
