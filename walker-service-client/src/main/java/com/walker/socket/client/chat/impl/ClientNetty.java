package com.walker.socket.client.chat.impl;

import com.walker.socket.encode.StringJsonDecoder;
import com.walker.socket.encode.StringJsonEncoder;
import com.walker.socket.util.SocketUtil;
import com.walker.socket.client.chat.frame.Client;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.util.concurrent.TimeUnit;

public class ClientNetty<DATA> extends Client<ChannelHandlerContext, DATA> {
    EventLoopGroup eventLoopGroup;

    public ClientNetty(String ip, int port, String name) throws Exception {
        super(ip, port, name);
    }

    @Override
    public void eventLoopKeeper() throws Exception {
        // Configure the client.
        eventLoopGroup = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class) // (3)
                .option(ChannelOption.SO_KEEPALIVE, true) // (4)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws Exception {
                        ChannelPipeline p = ch.pipeline();
                        p.addLast("ping", new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));    //5s心跳包
                        //							p.addLast(new LoggingHandler(LogLevel.INFO));
                        //							p.addLast( new ObjectEncoder(),  new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                        p.addLast(new StringJsonEncoder(), new StringJsonDecoder());
                        p.addLast(new HandlerNetty());
                    }
                });
        // Start the client.
        ChannelFuture f = bootstrap.connect(ip, port).sync();
        f.addListener(new GenericFutureListener<Future<Object>>() {
            @Override
            public void operationComplete(Future<Object> arg0) throws Exception {
                log.info("Netty operationComplete" + ClientNetty.this);
            }
        });
//        f.get();
//      Wait until the connection is closed.
        f.channel().closeFuture().sync();
    }

    /**
     * 停止监听
     */
    @Override
    public void stop(long timemillWait) throws Exception {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
        super.stop(timemillWait);
    }

    @Override
    public String readString(ChannelHandlerContext socket) throws Exception {
        return null;
    }

    @Override
    public void writeString(ChannelHandlerContext socket, String data) throws Exception {
        socket.writeAndFlush(data);
    }

    @Override
    public String makeSocketKey(ChannelHandlerContext socket) {
        return SocketUtil.makeKey(socket);
    }


    /**
     * netty的handler
     * 负责处理新旧连接
     * 监控新消息读取
     */
    public class HandlerNetty extends ChannelInboundHandlerAdapter {


        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            super.handlerAdded(ctx);
            onNewConnection(makeUserSession(ctx));

        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            super.handlerRemoved(ctx);
//			out("handlerRemoved", ctx);
            onDisConnection();
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//			out("channelRead", msg);
            onReceive(decode(String.valueOf(msg)));
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
//			out("channelReadComplete", ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            cause.printStackTrace();
//			group.shutdownGracefully();
//			ctx.close();
//			out("exceptionCaught", ctx, cause);
            log.info("异常", cause);
        }

    }

}
