package top.aprilyolies.snowflake.utils;

import top.aprilyolies.snowflake.common.SnowflakeProperties;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public class PropertyUtils {
    /**
     * 加载 property 实例
     *
     * @param classLoader 类加载器
     * @param path        属性配置文件位置
     * @return property 实例
     */
    public static Properties loadProperty(ClassLoader classLoader, String path) {
        try {
            if (classLoader == null || path == null) {
                throw new IllegalArgumentException("Can't load properties for the given classloader " + classLoader +
                        " and the given path " + path);
            }
            InputStream resource = classLoader.getResourceAsStream(path);
            Properties properties = new Properties();
            properties.load(resource);
            return properties;
        } catch (IOException e) {
            throw new IllegalArgumentException("Can't load properties for the given classloader " + classLoader +
                    " and the given path " + path);
        }
    }

    /**
     * 获取配置文件中 key 所对应的属性值
     *
     * @param classLoader 类加载器
     * @param path        属性配置文件位置
     * @return 配置文件中 key 所对应的属性值
     */
    public static String loadProperty(ClassLoader classLoader, String path, String key) {
        Properties properties = loadProperty(classLoader, path);
        return (String) properties.get(key);
    }

    /**
     * 将制定的配置文件解析为 SnowflakeProperties 实例
     *
     * @param classLoader 类加载器
     * @param path        配置文件所在路径
     * @return
     */
    public static SnowflakeProperties loadPropertyBean(ClassLoader classLoader, String path) {
        Properties properties = loadProperty(classLoader, path);
        SnowflakeProperties snowflakeProperties = new SnowflakeProperties();
        snowflakeProperties.setDbUrl(properties.getProperty("snowflake.database.url"));
        snowflakeProperties.setMachineId(properties.getProperty("snowflake.machine.id"));
        snowflakeProperties.setMachineIdProvider(properties.getProperty("snowflake.machine.id.provider"));
        snowflakeProperties.setIdType(properties.getProperty("snowflake.id.type"));
        snowflakeProperties.setZkHost(properties.getProperty("snowflake.zookeeper.host"));
        snowflakeProperties.setUsername(properties.getProperty("snowflake.database.username"));
        snowflakeProperties.setPassword(properties.getProperty("snowflake.database.password"));
        return snowflakeProperties;
    }
}
