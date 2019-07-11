package top.aprilyolies.snowflake.common;

import org.junit.Test;

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
}
