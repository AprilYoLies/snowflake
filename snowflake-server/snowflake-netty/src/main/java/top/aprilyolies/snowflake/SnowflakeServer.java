package top.aprilyolies.snowflake;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @Author EvaJohnson
 * @Date 2019-07-16
 * @Email g863821569@gmail.com
 */
public class SnowflakeServer {
    public static void main(String[] args) {
        new SnowflakeServer().start();
    }

    private void start() {
        NioEventLoopGroup bosses = new NioEventLoopGroup();
        NioEventLoopGroup workers = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bosses, workers)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new SnowflakeChannelInitializer());
            ChannelFuture future = bootstrap.bind(6707).sync();
            System.out.println("Snowflake netty server started on port 6707");
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bosses.shutdownGracefully();
            workers.shutdownGracefully();
        }
    }

    private class SnowflakeChannelInitializer extends ChannelInitializer {

        @Override
        protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast("http-codec", new HttpServerCodec());
            pipeline.addLast(new SnowflakeChannelHandler());
        }
    }
}
