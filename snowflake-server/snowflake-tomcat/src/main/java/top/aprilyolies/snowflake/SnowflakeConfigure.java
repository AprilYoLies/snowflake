package top.aprilyolies.snowflake;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import top.aprilyolies.snowflake.common.SnowflakeProperties;
import top.aprilyolies.snowflake.idservice.IdServiceFactory;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */

@Configuration
public class SnowflakeConfigure {
    @Bean("segment")
    public IdServiceFactory segServiceFactory() {
        return new IdServiceFactory();
    }

    @Bean("snowflake")
    public IdServiceFactory snowServiceFactory() {
        return new IdServiceFactory();
    }

    @Bean("snowflakeProperties")
    public SnowflakeProperties snowflakeProperties() {
        return new SnowflakeProperties();
    }

    @Bean("segmentProperties")
    public SnowflakeProperties segmentProperties() {
        return new SnowflakeProperties();
    }

    @Bean
    public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
        PropertyPlaceholderConfigurer configurer = new PropertyPlaceholderConfigurer();
        ClassPathResource snowflake = new ClassPathResource("snowflake.properties");
        configurer.setLocations(snowflake);
        return configurer;
    }
}
