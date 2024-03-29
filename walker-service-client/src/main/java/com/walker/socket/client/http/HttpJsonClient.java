package com.walker.socket.client.http;

import com.walker.socket.encode.HttpJsonRequestEncoder;
import com.walker.socket.encode.HttpJsonResponseDecoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;

import java.net.InetSocketAddress;
import java.util.Map;

/**
 * Created by carl.yu on 2016/12/16.
 */
public class HttpJsonClient {
    public void connect(int port) throws Exception {
        // 配置客户端NIO线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch)
                                throws Exception {
                            ch.pipeline().addLast("http-decoder",
                                    new HttpResponseDecoder());
                            ch.pipeline().addLast("http-aggregator",
                                    new HttpObjectAggregator(65536));
                            // json解码器
                            ch.pipeline().addLast("json-decoder", new HttpJsonResponseDecoder(Map.class, true));
                            ch.pipeline().addLast("http-encoder",
                                    new HttpRequestEncoder());
                            ch.pipeline().addLast("json-encoder",
                                    new HttpJsonRequestEncoder());
                            ch.pipeline().addLast("jsonClientHandler",
                                    new HttpJsonClientHandler());
                        }
                    });

            // 发起异步连接操作
            ChannelFuture f = b.connect(new InetSocketAddress(port)).sync();

            // 当代客户端链路关闭
            f.channel().closeFuture().sync();
        } finally {
            // 优雅退出，释放NIO线程组
            group.shutdownGracefully();
        }
    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

//        new HttpBuilder("http://127.0.0.1:8092", HttpBuilder.Type.POST)
//                .setData(JSON.parseObject(JSON.toJSONString(
//                        new ClassUtil.Model()
//                                .setClassName("com.walker.socket.server.http.HttpJsonServerController")
//                                .setConstructorArgs("cr")
//                                .setMethodName("hello")
//                                .setConstructorArgs("info test")
//                ))).setForm(true)
//                .buildString();


        int port = 8092;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new HttpJsonClient().connect(port);
    }


}
