package top.aprilyolies.snowflake.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */

/**
 * 用于封装 snowflake 的配置信息，可以通过 PropertyUtils#loadPropertyBean() 方法来进行构建
 */
@PropertySource("snowflake.properties")
public class SnowflakeProperties {
    @Value("${snowflake.machine.id}")
    private String machineId;

    @Value("${snowflake.machine.id.provider}")
    private String machineIdProvider;

    @Value("${snowflake.id.type}")
    private String idType;

    @Value("${snowflake.zookeeper.host}")
    private String zkHost;

    @Value("${snowflake.database.url}")
    private String dbUrl;

    @Value("${snowflake.database.username}")
    private String username;

    @Value("${snowflake.database.password}")
    private String password;


    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineIdProvider() {
        return machineIdProvider;
    }

    public void setMachineIdProvider(String machineIdProvider) {
        this.machineIdProvider = machineIdProvider;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getZkHost() {
        return zkHost;
    }

    public void setZkHost(String zkHost) {
        this.zkHost = zkHost;
    }

    public String getDbUrl() {
        return dbUrl;
    }

    public void setDbUrl(String dbUrl) {
        this.dbUrl = dbUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
