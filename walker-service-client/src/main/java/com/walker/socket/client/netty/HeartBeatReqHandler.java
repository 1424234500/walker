package com.walker.socket.client.netty;

import com.walker.socket.model.Header;
import com.walker.socket.model.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author Lilinfeng
 * @version 1.0
 * @date 2014年3月15日
 */
public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

    public Logger log = LoggerFactory.getLogger(getClass());

    //使用定时任务发送
    private volatile ScheduledFuture<?> heartBeat;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 当握手成功登录ok时，Login响应向下透传，主动发送心跳消息
        if (message.getHeader() != null
                && message.getHeader().getType() == Header.MessageType.LOGIN_RESP
                .getValue()) {
            //NioEventLoop是一个Schedule,因此支持定时器的执行，创建心跳计时器
            heartBeat = ctx.executor().scheduleAtFixedRate(
                    new HeartBeatReqHandler.HeartBeatTask(ctx), 0, 5000,
                    TimeUnit.MILLISECONDS);
        } else if (message.getHeader() != null
                && message.getHeader().getType() == Header.MessageType.HEARTBEAT_RESP
                .getValue()) {
            log.info("Client receive server heart beat message : ---> " + message);
        } else
            ctx.fireChannelRead(msg);
    }

    //Ping消息任务类
    private class HeartBeatTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public HeartBeatTask(final ChannelHandlerContext ctx) {
            this.ctx = ctx;
        }

        @Override
        public void run() {
            NettyMessage heatBeat = buildHeatBeat();
            log.info("Client send heart beat messsage to server : ---> "
                    + heatBeat);
            ctx.writeAndFlush(heatBeat);
        }

        private NettyMessage buildHeatBeat() {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            header.setType(Header.MessageType.HEARTBEAT_REQ.getValue());
            message.setHeader(header);
            return message;
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        cause.printStackTrace();
        if (heartBeat != null) {
            heartBeat.cancel(true);
            heartBeat = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}