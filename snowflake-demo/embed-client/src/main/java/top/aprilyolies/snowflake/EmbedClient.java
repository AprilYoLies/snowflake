package top.aprilyolies.snowflake;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import top.aprilyolies.snowflake.idservice.IdService;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */
public class EmbedClient {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("embed-client.xml");
        context.start();
        IdService idService = (IdService) context.getBean("idService");
        for (int i = 0; i < 100; i++) {
            System.out.println(idService.generateId());
        }
    }
}
