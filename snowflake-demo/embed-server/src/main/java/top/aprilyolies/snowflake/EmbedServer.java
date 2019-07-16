package top.aprilyolies.snowflake;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */
public class EmbedServer {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("embed-server.xml");
        context.start();
        System.out.println("Embed-server started.");
        System.in.read();
    }
}
