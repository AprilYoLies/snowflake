package top.aprilyolies.snowflake.machineid;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public abstract class AbstractMachineIdProvider implements MachineIdProvider {
    /**
     * 检查 id 是否合法
     *
     * @param id 被检查的 machine id
     * @return 合法的 id
     */
    protected int checkMachineId(int id) {
        if (id >= 0 && id <= 1023) {
            return id;
        } else {
            throw new IllegalArgumentException("Machine id should between 0 to 1023");
        }
    }
}
