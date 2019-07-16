package top.aprilyolies.snowflake.controller;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.aprilyolies.snowflake.SnowflakeProperties;
import top.aprilyolies.snowflake.idservice.IdService;
import top.aprilyolies.snowflake.idservice.IdServiceFactory;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */

@RestController
public class SegmentController {
    private final SnowflakeProperties snowflakeProperties;

    private final IdService segIdService;

    private IdServiceFactory segIdServiceFactory;

    private IdServiceFactory snowIdServiceFactory;

    public SegmentController(@Qualifier("segment") IdServiceFactory segServiceFactory,
                             @Qualifier("snowflake") IdServiceFactory snowServiceFactory,
                             @Qualifier("snowflakeProperties") SnowflakeProperties snowflakeProperties) throws Exception {
        this.segIdServiceFactory = segServiceFactory;
        this.snowIdServiceFactory = snowServiceFactory;
        this.snowflakeProperties = snowflakeProperties;
        this.segIdService = buildSegmentIdService();
    }

    @RequestMapping("/snowflake/seg/{business}")
    public String getId(@PathVariable String business) {
        return segIdService.generateId(business);
    }

    private IdService buildSegmentIdService() {
        segIdServiceFactory.setDbUrl(snowflakeProperties.getDbUrl());
        segIdServiceFactory.setUsername(snowflakeProperties.getUsername());
        segIdServiceFactory.setPassword(snowflakeProperties.getPassword());
        segIdServiceFactory.setServiceType("segment");
        try {
            return (IdService) segIdServiceFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
