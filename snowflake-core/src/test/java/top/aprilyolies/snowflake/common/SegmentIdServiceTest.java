package top.aprilyolies.snowflake.common;

import org.junit.Before;
import org.junit.Test;
import top.aprilyolies.snowflake.idservice.IdService;
import top.aprilyolies.snowflake.idservice.IdServiceFactory;
import top.aprilyolies.snowflake.idservice.support.MachineIdProviderType;

import java.util.concurrent.CountDownLatch;

/**
 * @Author EvaJohnson
 * @Date 2019-07-14
 * @Email g863821569@gmail.com
 */
public class SegmentIdServiceTest {
    private IdServiceFactory factory;

    @Before
    public void initFactory() {
        factory = new IdServiceFactory();
        factory.setIdType("0");
        factory.setServiceType("segment");
        factory.setMachineIdProvider(MachineIdProviderType.MYSQL);
        factory.setDbUrl("jdbc:mysql://localhost:3306/snowflake");
        factory.setZkHost("119.23.247.86");
        factory.setUsername("root");
        factory.setPassword("kuaile1..");
    }

    @Test
    public void testSnowflakeUseProperty() throws Exception {
        IdService service = (IdService) factory.getObject();
        String id = service.generateId("order");
        System.out.println(id);
    }

    @Test
    public void testMultiThreadSnowflakeUseProperty() throws Exception {
        IdService service = (IdService) factory.getObject();
        CountDownLatch latch = new CountDownLatch(10);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        String id = service.generateId("order");
                        System.out.println(id);
                    }
                } finally {
                    latch.countDown();
                }
            }).start();
        }
        latch.await();
        System.out.println(System.currentTimeMillis() - start);
    }
}
