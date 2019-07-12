package top.aprilyolies.snowflake.common;

import org.junit.Test;
import top.aprilyolies.snowflake.idservice.support.IdModel;
import top.aprilyolies.snowflake.idservice.support.TimeSupport;
import top.aprilyolies.snowflake.idservice.support.idbuilder.SnowflakeIdBuilder;
import top.aprilyolies.snowflake.machineid.impl.PropertyMachineIdProvider;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

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
        TimeSupport timeSupport1 = new TimeSupport(0);

        SnowflakeIdBuilder builder1 = new SnowflakeIdBuilder(0, timeSupport1, provider);
        String sid1 = builder1.buildId();

        TimeSupport timeSupport2 = new TimeSupport(0);
        SnowflakeIdBuilder builder2 = new SnowflakeIdBuilder(1, timeSupport2, provider);
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

    @Test
    public void testGenerateMask() {
        System.out.println(~(-1L << (2)));
        System.out.println(1 << 2);
    }

    @Test
    public void testGenMaxTime() {
        System.out.println((1L << (41)) - 1);
    }

    @Test
    public void testGetTempFolder() {
        String property = System.getProperty("java.io.tmpdir");
        System.out.println(property);
    }

    @Test
    public void testGetIpAddress() {
        String ip = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()    // 查看当前 ip 是否是本地回环地址
                            && !inetAddress.isLinkLocalAddress()    // 链路本地地址
                            && inetAddress.isSiteLocalAddress()) {  // 这个算是公网地址？？
                        ip = inetAddress.getHostAddress();
                    }
                }
            }
            System.out.println(ip);
        } catch (SocketException e) {
            System.out.println(ip);
        }
    }
}
