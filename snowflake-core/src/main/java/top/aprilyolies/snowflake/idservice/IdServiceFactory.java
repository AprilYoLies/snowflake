package top.aprilyolies.snowflake.idservice;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;
import top.aprilyolies.snowflake.idservice.impl.SnowflakeIdService;
import top.aprilyolies.snowflake.machineid.MachineIdProvider;
import top.aprilyolies.snowflake.machineid.impl.PropertyMachineIdProvider;

/**
 * @Author EvaJohnson
 * @Date 2019-07-11
 * @Email g863821569@gmail.com
 */
public class IdServiceFactory implements FactoryBean {
    // 服务的类型
    private String serviceType;
    // id 的类型
    private String idType;
    // machine id 的生成器
    private String machineIdProvider;
    // 采用 snowflake 算法生成 id 的服务
    private final String SNOWFLAKE_SERVICE = "snowflake";
    // 采用分段的方式生成 id 的服务
    private final String SEGMENT_SERVICE = "segment";

    private final String PROPERTY_MACHINE_ID_PROVIDER = "property";

    @Override
    public Object getObject() throws Exception {
        if (StringUtils.isEmpty(serviceType)) {
            throw new IllegalArgumentException("Service type should not be null");
        } else {
            MachineIdProvider machineIdProvider = parseIdProvider(this.machineIdProvider);
            long idType = Long.parseLong(this.idType);
            if (SNOWFLAKE_SERVICE.equals(serviceType)) {
                return new SnowflakeIdService(idType, machineIdProvider);
            } else {
                return null;
            }
        }
    }

    /**
     * 根据 machineIdProvider 解析出 MachineIdProvider 实例
     *
     * @param machineIdProvider 字符串类型的 machineIdProvider
     * @return 匹配出来的 MachineIdProvider 实例
     */
    private MachineIdProvider parseIdProvider(String machineIdProvider) {
        if (StringUtils.isEmpty(machineIdProvider))
            throw new IllegalArgumentException("Machine id type should not be null");
        switch (machineIdProvider) {
            case PROPERTY_MACHINE_ID_PROVIDER:
                return new PropertyMachineIdProvider();
            default:
                throw new IllegalArgumentException("None of machine id provider could be found");
        }
    }

    @Override
    public Class<?> getObjectType() {
        return IdService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public void setMachineIdProvider(String machineIdProvider) {
        this.machineIdProvider = machineIdProvider;
    }
}