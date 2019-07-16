package top.aprilyolies.snowflake;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import top.aprilyolies.snowflake.idservice.IdService;
import top.aprilyolies.snowflake.idservice.IdServiceFactory;

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
    // SnowflakeIdService 实例
    private static final IdService snowIdService = IdServiceFactory.buildIdService(SnowflakeChannelHandler.class.getClassLoader(), "snowflake", "snowflake.properties");
    // SegmentIdService 实例
    private static final IdService segIdService = IdServiceFactory.buildIdService(SnowflakeChannelHandler.class.getClassLoader(), "segment", "snowflake.properties");
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

        if (SNOWFLAKE_SERVICE_PATH.equals(uri)) {
            sb.append(snowIdService.generateId());
        } else if (uri.startsWith(SEGMENT_SERVICE_PATH)) {
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
}
