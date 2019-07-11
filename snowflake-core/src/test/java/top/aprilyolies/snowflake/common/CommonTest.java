package top.aprilyolies.snowflake.common;

import org.junit.Test;
import top.aprilyolies.snowflake.idservice.support.IdModel;
import top.aprilyolies.snowflake.idservice.support.idbuilder.SnowflakeIdBuilder;
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
        System.out.println(IdModel.parseModel(0));
        System.out.println(IdModel.parseModel(1));
        try {
            System.out.println(IdModel.parseModel(2));
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

    @Test
    public void testSnowflakeIdBuilder() {
        PropertyMachineIdProvider provider = new PropertyMachineIdProvider();

        SnowflakeIdBuilder builder1 = new SnowflakeIdBuilder(0, 1562841139L, 2097152, provider);
        String sid1 = builder1.buildId();

        SnowflakeIdBuilder builder2 = new SnowflakeIdBuilder(1, 1562841139707L, 2097152, provider);
        String sid2 = builder2.buildId();

        System.out.println(sid1 + " : " + sid2);
    }

    @Test
    public void testGetCurrentTime() {
        System.out.println(System.currentTimeMillis());
    }

    @Test
    public void testGetMaxTime() {
        IdModel model = IdModel.parseModel(0);
        long maxTimes = (1 << (model.getTypePos() - model.getTimePos())) - 1;
        System.out.println(maxTimes);
    }

}
