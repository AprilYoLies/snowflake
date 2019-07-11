package top.aprilyolies.snowflake.idservice.support;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */

/**
 * 通过 snowflake 算法生成唯一 id 的模型
 * <p>
 * 最大峰值型：
 * 1位（标识颗粒度）      占用 31 位     占用 22 位     10 位（1024 台机器）
 * 生成id的模式     -    时间戳    -    序列号     -      机器id
 * <p>
 * 最小粒度型：
 * 1位（标识颗粒度）      占用 41 位     占用 12 位     10 位（1024 台机器）
 * 生成id的模式     -    时间戳    -    序列号     -      机器id
 */
public class IdModel {
    // 序列号掩码
    private final long serialMask;

    // type 字段在 id 数据结构中的起始位置
    private int typePos;

    // time 字段在 id 数据结构中的起始位置
    private int timePos;

    // serial 字段在 id 数据结构中的起始位置
    private int serialPos;

    // machine id 字段在 id 数据结构中的起始位置
    private int machinePos;

    private IdModel(int typePos, int timePos, int serialPos, int machinePos) {
        this.typePos = typePos;
        this.timePos = timePos;
        this.serialPos = serialPos;
        this.machinePos = machinePos;
        this.serialMask = ~(-1L << (timePos - serialPos));
    }

    /**
     * 根据 type 构建对应的 IdModel
     *
     * @param type 生成 id 的类型，0 代表最大峰值型，1 代表最小颗粒度型
     * @return 对应 id 的模型
     */
    public static IdModel parseModel(long type) {
        if (type == 0) {
            return new IdModel(63, 32, 10, 0);
        } else if (type == 1) {
            return new IdModel(63, 22, 10, 0);
        } else {
            throw new IllegalArgumentException("Can't parse id model for type " + type + " , please use 0 for maximum peak type or 1 for minimum granularity");
        }
    }

    public int getTypePos() {
        return typePos;
    }

    public void setTypePos(int typePos) {
        this.typePos = typePos;
    }

    public int getTimePos() {
        return timePos;
    }

    public void setTimePos(int timePos) {
        this.timePos = timePos;
    }

    public int getSerialPos() {
        return serialPos;
    }

    public void setSerialPos(int serialPos) {
        this.serialPos = serialPos;
    }

    public int getMachinePos() {
        return machinePos;
    }

    public void setMachinePos(int machinePos) {
        this.machinePos = machinePos;
    }

    public long getSerialMask() {
        return serialMask;
    }

    @Override
    public String toString() {
        return "IdModel{" +
                "typePos=" + typePos +
                ", timePos=" + timePos +
                ", serialPos=" + serialPos +
                ", machinePos=" + machinePos +
                '}';
    }
}
