package com.walker.socket.server.chat.impl;

import com.walker.socket.encode.StringJsonDecoder;
import com.walker.socket.encode.StringJsonEncoder;
import com.walker.socket.frame.ServerSessionEncode;
import com.walker.socket.frame.Session;
import com.walker.socket.util.SocketUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * 初始化一个socketServer
 * 绑定一个handler
 * 即可在handler处理各种连接和事件
 */


public class ServerSocketNetty<DATA> extends ServerSessionEncode<ChannelHandlerContext, DATA> {


    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public ServerSocketNetty(int port) throws Exception {
        super(port);
    }


    @Override
    public String makeSocketKey(ChannelHandlerContext channelHandlerContext) {
        return SocketUtil.makeKey(channelHandlerContext);
    }


    @Override
    public void eventLoopKeeper() throws Exception {
        // Configure the server.
        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                public void initChannel(SocketChannel ch) throws Exception {
                                    ChannelPipeline p = ch.pipeline();
                                    p.addLast(new IdleStateHandler(10, 0, 0, TimeUnit.SECONDS));    //5s心跳包
//						p.addLast(new LoggingHandler(LogLevel.INFO));
//						p.addLast( new ObjectEncoder(),  new ObjectDecoder(ClassResolvers.cacheDisabled(null)))
                                    p.addLast(new StringJsonEncoder(), new StringJsonDecoder());
//						p.addLast(new HeartBeatClientHandler());
                                    p.addLast(new HandlerNetty());

                                }
                            });
            log.info(this + " bind成功 等待连接 端口监听");

            // Start the server.
            ChannelFuture f = serverBootstrap.bind(port).sync();

            // Wait until the server socket is closed.
            f.channel().closeFuture().sync();
        } finally {
            // Shut down all event loops to terminate all threads.
            if (bossGroup != null) {
                bossGroup.shutdownGracefully();
                bossGroup = null;
            }
            if (workerGroup != null) {
                workerGroup.shutdownGracefully();
                workerGroup = null;
            }
        }
    }

    @Override
    public String readString(ChannelHandlerContext socket) throws Exception {
        return null;
    }

    @Override
    public void writeString(ChannelHandlerContext socket, String data) throws Exception {
        ByteBuf resp = Unpooled.copiedBuffer(data.getBytes(StandardCharsets.UTF_8));
        socket.write(resp);
//        socket.writeAndFlush(data);
    }

    /**
     * 停止监听
     */
    @Override
    public void stop(long timemillWait) throws Exception {
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
            bossGroup = null;
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
            workerGroup = null;
        }
        super.stop(timemillWait);
    }

    /**
     * netty的handler
     * 负责处理新旧连接
     * 监控新消息读取
     *
     * @author ThinkPad
     */
    class HandlerNetty extends ChannelInboundHandlerAdapter {

        @Override
        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
            super.userEventTriggered(ctx, evt);
            log.info("userEventTriggered" + " " + ctx + " " + evt);


        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            super.channelActive(ctx);
//			out("channelActive", ctx);
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            super.channelInactive(ctx);
            log.info("channelInactive" + " " + ctx + " ");
        }

        @Override
        public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
            super.handlerAdded(ctx);
            Session<ChannelHandlerContext, DATA> session = makeUserSession(ctx);
            onNewConnection(session);
        }

        @Override
        public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
            super.handlerRemoved(ctx);
            Session<ChannelHandlerContext, DATA> session = makeUserSession(ctx);
            onDisConnection(session);
//			out("handlerRemoved", ctx);
        }

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            Session<ChannelHandlerContext, DATA> session = makeUserSession(ctx);
            String readLine = (String) msg;
            DATA data = decode(readLine);
            onReceive(session, data);

//			out("channelRead", msg);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) {
            ctx.flush();
//			out("channelReadComplete", ctx);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            ctx.close();
            log.error("channelInactive" + " " + ctx + " " + cause.getMessage(), cause);
        }

    }

}
