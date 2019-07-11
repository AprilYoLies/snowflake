package top.aprilyolies.snowflake.common;

import org.junit.Before;
import org.junit.Test;
import top.aprilyolies.snowflake.idservice.IdService;
import top.aprilyolies.snowflake.idservice.IdServiceFactory;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public class IdServiceFactoryTest {
    IdServiceFactory factory;

    @Before
    public void initFactory() {
        factory = new IdServiceFactory();
        factory.setIdType("0");
        factory.setServiceType("snowflake");
        factory.setMachineIdProvider("property");
    }

    @Test
    public void testSnowflakeUseProperty() throws Exception {
        IdService service = (IdService) factory.getObject();
        String id = service.generateId();
        System.out.println(id);
    }
}
