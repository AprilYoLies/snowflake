package top.aprilyolies.snowflake.machineid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public abstract class AbstractMachineIdProvider implements MachineIdProvider {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

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
            logger.info("Machine id should between 0 and 1023");
            throw new IllegalArgumentException("Machine id should between 0 and 1023");
        }
    }
}
