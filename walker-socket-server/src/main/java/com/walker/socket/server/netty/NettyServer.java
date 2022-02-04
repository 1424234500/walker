package com.walker.socket.server.netty;
import com.walker.socket.encode.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Lilinfeng
 * @version 1.0
 * @date 2014年3月15日
 */
public class NettyServer {

	public Logger log = LoggerFactory.getLogger(getClass());

	public void bind(int port) throws Exception {

		log.info("Netty server try start " + ("127.0.0.1:" + port));

		// 配置服务端的NIO线程组
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b = new ServerBootstrap();
		b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
				.option(ChannelOption.SO_BACKLOG, 100)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch)
							throws IOException {
						ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
						ch.pipeline().addLast(new NettyMessageEncoder());
						ch.pipeline().addLast("readTimeoutHandler",new ReadTimeoutHandler(3));
						ch.pipeline().addLast(new LoginAuthRespHandler());
						ch.pipeline().addLast("HeartBeatHandler",new HeartBeatRespHandler());
					}
				});

		// 绑定端口，同步等待成功
		b.bind(port).sync();
		log.info("Netty server start ok " + ("127.0.0.1:" + port));
	}

	public static void main(String[] args) throws Exception {
		new NettyServer().bind(8093);
	}
}