package top.aprilyolies.snowflake.common;

import org.junit.Test;
import top.aprilyolies.snowflake.idservice.support.IdModle;
import top.aprilyolies.snowflake.machineid.impl.PropertyMachineIdProvider;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public class CommonTest {
    @Test
    public void testPowerInOneMS() {
        long current = System.currentTimeMillis();
        int count = 0;
        while (current == System.currentTimeMillis()) {
            count++;
        }
        System.out.println(count);
    }

    @Test
    public void testIdModel() {
        System.out.println(IdModle.parseModel(0));
        System.out.println(IdModle.parseModel(1));
        try {
            System.out.println(IdModle.parseModel(2));
        } catch (Exception e) {
            System.out.println("Parse failed");
        }
    }

    @Test
    public void testPropertyMachineIdProvider() {
        PropertyMachineIdProvider provider = new PropertyMachineIdProvider();
        int id = provider.buildMachineId();
        System.out.println(id);
    }

}
