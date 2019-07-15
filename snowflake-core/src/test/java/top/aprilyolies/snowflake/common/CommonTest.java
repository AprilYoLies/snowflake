package top.aprilyolies.snowflake.common;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import top.aprilyolies.snowflake.idservice.support.IdModel;
import top.aprilyolies.snowflake.idservice.support.TimeSupport;
import top.aprilyolies.snowflake.idservice.support.idbuilder.SnowflakeIdBuilder;
import top.aprilyolies.snowflake.machineid.impl.PropertyMachineIdProvider;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public class CommonTest {
    volatile boolean flag = true;

    @Test
    public void testPowerInOneMS() throws InterruptedException {
        int count = 0;
        CountDownLatch latch = new CountDownLatch(1);
        new Thread(() -> {
            long current = System.currentTimeMillis();
            latch.countDown();
            while (current == System.currentTimeMillis()) {

            }
            flag = false;
        }).start();
        latch.await();
        while (flag) {
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

    @Test
    public void testStringIntern1() {
        String str1 = new String("str") + new String("01");
        str1.intern();
        String str2 = "str01";
        System.out.println(str1 == str2);
    }

    @Test
    public void testStringIntern2() {
        String s2 = "str01";
        String s1 = new String("str") + new String("01");
        s1.intern();
        System.out.println(s1 == s2);
    }

    @Test
    public void testStringIntern3() {
        String s1 = new String("str01");
        s1.intern();
        String s2 = "str01";
        System.out.println(s1 == s2);
    }

    @Test
    public void testFetchMachineIdFromLocalCache() {
        File file = new File(System.getProperty("java.io.tmpdir") + "machine_id");
        if (file.exists()) {
            try {
                List<String> lines = FileUtils.readLines(file);
                if (lines.size() > 0) {
                    String line = lines.get(0);
                }
            } catch (Exception e) {
            }
        }
    }


    public void testBatchInsert() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/db";
            String username = "";
            String password = "";
            Connection connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(false);
            String tableName = "";
            String fileName = "";
            for (int i = 0; i < 92; i++) {
                tableName = i < 100 ? "0" + i : i + "";
                fileName = "0" + tableName;
                String sql = "load data local infile '/mobikedata/" + fileName + ".txt' into table mobike" + tableName;
                PreparedStatement ps = connection.prepareStatement(sql);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSegmentExchange() {
        int cur = 0;
        for (int i = 0; i < 100; i++) {
            cur = ++cur & 1;
            System.out.println(cur);
        }
    }

    @Test
    public void testLoadBegin() {
        System.out.println(1000 * 0.2);
    }

    @Test
    public void testSemaphore() throws IOException {
        Semaphore semaphore = new Semaphore(0);
        semaphore.release();
        new Thread(() -> {
            try {
                System.out.println("begin");
                semaphore.acquire();
                System.out.println("end");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        System.in.read();
    }
}
