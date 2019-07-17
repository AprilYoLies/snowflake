package top.aprilyolies.snowflake.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.aprilyolies.snowflake.SnowflakeApplication;
import top.aprilyolies.snowflake.idservice.IdService;
import top.aprilyolies.snowflake.idservice.IdServiceFactory;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */

@RestController
public class SegmentController {
    Logger logger = LoggerFactory.getLogger(SegmentController.class);
    // SnowflakeIdService 实例
    private final IdService snowIdService;
    // SegmentIdService 实例
    private final IdService segIdService;

    public SegmentController() throws Exception {
        ClassLoader classLoader = SegmentController.class.getClassLoader();
        this.segIdService = IdServiceFactory.buildIdService(classLoader, "segment", "snowflake.properties");
        this.snowIdService = IdServiceFactory.buildIdService(classLoader, "snowflake", "snowflake.properties");
        logger.info("Snowflake netty server started on port 6707.");
        logger.info("Get segment id by http://localhost:8080/snowflake/seg/business-name");
        logger.info("Get snowflake id by http://localhost:8080/snowflake/snow");
    }

    @RequestMapping("/snowflake/seg/{business}")
    public String getSegId(@PathVariable String business) {
        return segIdService.generateId(business);
    }

    @RequestMapping("/snowflake/snow")
    public String getSnowId() {
        return snowIdService.generateId();
    }
}
