package top.aprilyolies.snowflake;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import top.aprilyolies.snowflake.idservice.IdService;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */
public class EmbedClient {
    public static void main(String[] args) throws Exception {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("embed-client.xml");
        context.start();
        IdService idService = (IdService) context.getBean("idService");
        for (int i = 0; i < 20000; i++) {
            System.out.println(idService.generateId());
            Thread.sleep(500);
        }
    }
}
