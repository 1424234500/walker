package com.walker.socket.server.http;

import com.walker.socket.encode.HttpJsonRequestDecoder;
import com.walker.socket.encode.HttpJsonResponseEncoder;
import com.walker.util.ClassUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Created by carl.yu on 2016/12/16.
 */
public class HttpJsonServer {

	public static void main(String[] args) throws Exception {
		int port = 8092;
		if (args.length > 0) {
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		new HttpJsonServer().run(port);
	}


	public Logger log = LoggerFactory.getLogger(getClass());

	public void run(final int port) throws Exception {
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			log.warn("HTTP接口调用尝试启动 " + "http://localhost:" + port);

			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch)
								throws Exception {
							//接收HttpJsonRequest，需要对应解码器
							//ByteBuf->FullHttpRequest-> HttpJsonRequestDecoder
							//输出HttpJsonResponse，需要对应编码器
							//HttpResponseEncoder->FullHttpResponse-> HttpJsonResponseEncoder
							ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
							ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
							ch.pipeline().addLast("json-decoder", new HttpJsonRequestDecoder(ClassUtil.Model.class, true));
							ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
							ch.pipeline().addLast("json-encoder", new HttpJsonResponseEncoder());
							ch.pipeline().addLast("jsonServerHandler", new HttpJsonServerHandler());
						}
					});
			ChannelFuture future = b.bind(new InetSocketAddress(port)).sync();
			log.warn("HTTP接口调用启动成功 " + "http://localhost:" + port);
			future.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

}