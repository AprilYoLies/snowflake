package top.aprilyolies.snowflake.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.aprilyolies.snowflake.SnowflakeProperties;
import top.aprilyolies.snowflake.idservice.IdService;
import top.aprilyolies.snowflake.idservice.IdServiceFactory;
import top.aprilyolies.snowflake.idservice.support.MachineIdProviderType;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */

@RestController
public class SegmentController {
    // 从配置文件中读取的配置信息
    private final SnowflakeProperties snowflakeProperties;
    // SnowflakeIdService 实例
    private final IdService snowIdService;
    // SegmentIdService 实例
    private final IdService segIdService;
    // SegmentIdService 服务类工厂
    private IdServiceFactory segIdServiceFactory;
    // SnowflakeIdService 服务类工厂
    private IdServiceFactory snowIdServiceFactory;

    public SegmentController(@Qualifier("segment") IdServiceFactory segServiceFactory,
                             @Qualifier("snowflake") IdServiceFactory snowServiceFactory,
                             @Qualifier("snowflakeProperties") SnowflakeProperties snowflakeProperties) throws Exception {
        this.segIdServiceFactory = segServiceFactory;
        this.snowIdServiceFactory = snowServiceFactory;
        this.snowflakeProperties = snowflakeProperties;
        this.segIdService = buildSegmentIdService();
        this.snowIdService = buildSnowflakeIdService();
    }

    @RequestMapping("/snowflake/seg/{business}")
    public String getSegId(@PathVariable String business) {
        return segIdService.generateId(business);
    }

    @RequestMapping("/snowflake/snow")
    public String getSnowId() {
        return snowIdService.generateId();
    }

    // 根据配置信息构建 SegmentIdService 实例
    private IdService buildSegmentIdService() {
        segIdServiceFactory.setServiceType("segment");
        segIdServiceFactory.setDbUrl(snowflakeProperties.getDbUrl());
        segIdServiceFactory.setUsername(snowflakeProperties.getUsername());
        segIdServiceFactory.setPassword(snowflakeProperties.getPassword());
        try {
            return (IdService) segIdServiceFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 根据配置信息构建 SnowflakeIdService 实例
    private IdService buildSnowflakeIdService() {
        segIdServiceFactory.setServiceType("snowflake");
        segIdServiceFactory.setDbUrl(snowflakeProperties.getDbUrl());
        segIdServiceFactory.setMachineIdProvider(MachineIdProviderType.valueOf(snowflakeProperties.getMachineIdProvider()));
        segIdServiceFactory.setUsername(snowflakeProperties.getUsername());
        segIdServiceFactory.setPassword(snowflakeProperties.getPassword());
        segIdServiceFactory.setZkHost(snowflakeProperties.getZkHost());
        segIdServiceFactory.setIdType(snowflakeProperties.getIdType());
        try {
            return (IdService) segIdServiceFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
