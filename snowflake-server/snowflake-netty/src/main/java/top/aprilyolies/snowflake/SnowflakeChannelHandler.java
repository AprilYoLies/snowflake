package top.aprilyolies.snowflake;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import top.aprilyolies.snowflake.common.SnowflakeProperties;
import top.aprilyolies.snowflake.idservice.IdService;
import top.aprilyolies.snowflake.idservice.IdServiceFactory;
import top.aprilyolies.snowflake.idservice.support.MachineIdProviderType;
import top.aprilyolies.snowflake.utils.PropertyUtils;

import java.nio.charset.Charset;

import static io.netty.handler.codec.http.HttpHeaders.Names.*;
import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */
public class SnowflakeChannelHandler extends ChannelInboundHandlerAdapter {
    // 从配置文件中读取的配置信息
    private static final SnowflakeProperties snowflakeProperties = PropertyUtils.
            loadPropertyBean(SnowflakeChannelHandler.class.getClassLoader(), "snowflake.properties");
    // SegmentIdService 服务类工厂
    private static final IdServiceFactory segIdServiceFactory = new IdServiceFactory();
    // SnowflakeIdService 服务类工厂
    private static final IdServiceFactory snowIdServiceFactory = new IdServiceFactory();
    // SnowflakeIdService 实例
    private static final IdService snowIdService = buildSnowflakeIdService();
    // SegmentIdService 实例
    private static final IdService segIdService = buildSegmentIdService();
    // snowflake 前缀
    private static final String SNOWFLAKE_PREFIX = "/snowflake";
    // snowflake service 的请求路径
    private static final String SNOWFLAKE_SERVICE_PATH = "/snowflake/snow";
    // segment service 的请求路径
    private static final String SEGMENT_SERVICE_PATH = "/snowflake/seg/";
    // segment service 的请求路径前缀长度
    private static final int len = SEGMENT_SERVICE_PATH.length();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof HttpRequest) || (msg instanceof LastHttpContent))
            return;

        HttpRequest req = (HttpRequest) msg;

        if (is100ContinueExpected(req)) {   // 处理头信息有 Expect: 100-continue 的情况
            ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE));
        }

        String uri = req.uri();

        StringBuffer sb = new StringBuffer();

        if ("/snowflake/snow".equals(uri)) {
            sb.append(snowIdService.generateId());
        } else if (uri.startsWith("/snowflake/seg/")) {
            String business = uri.substring(len);
            sb.append(segIdService.generateId(business));
        } else {
            sb.append("Unsupported operation.");
        }

        // 构建响应内容
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(sb.toString().getBytes(Charset.forName("UTF-8"))));
        // 设置必要的响应头
        response.headers().set(CONTENT_TYPE, "text/plain");
        response.headers().set(CONTENT_LENGTH,
                response.content().readableBytes());

        boolean keepAlive = isKeepAlive(req);

        if (!keepAlive) {
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);   // 如果不是长连接类型，操作完成后直接关闭 channel
        } else {
            response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);  // 否则设置长连接头信息，将响应写回
            ctx.writeAndFlush(response);
        }
    }

    // 根据配置信息构建 SegmentIdService 实例
    private static IdService buildSegmentIdService() {
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
    private static IdService buildSnowflakeIdService() {
        snowIdServiceFactory.setServiceType("snowflake");
        snowIdServiceFactory.setDbUrl(snowflakeProperties.getDbUrl());
        snowIdServiceFactory.setMachineIdProvider(MachineIdProviderType.valueOf(snowflakeProperties.getMachineIdProvider()));
        snowIdServiceFactory.setUsername(snowflakeProperties.getUsername());
        snowIdServiceFactory.setPassword(snowflakeProperties.getPassword());
        snowIdServiceFactory.setZkHost(snowflakeProperties.getZkHost());
        snowIdServiceFactory.setIdType(snowflakeProperties.getIdType());
        try {
            return (IdService) snowIdServiceFactory.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
