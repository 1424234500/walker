package com.walker.socket.server.netty;

import com.walker.socket.model.Header;
import com.walker.socket.model.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Lilinfeng
 * @version 1.0
 * @date 2014年3月15日
 */
public class HeartBeatRespHandler extends ChannelInboundHandlerAdapter {

    public Logger log = LoggerFactory.getLogger(getClass());


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg)
            throws Exception {
        NettyMessage message = (NettyMessage) msg;
        // 返回心跳应答消息
        if (message.getHeader() != null
                && message.getHeader().getType() == Header.MessageType.HEARTBEAT_REQ.getValue()) {
            log.info("Receive client heart beat message : ---> "
                    + message);
            NettyMessage heartBeat = buildHeatBeat();
            log.info("Send heart beat response message to client : ---> "
                    + heartBeat);
            ctx.writeAndFlush(heartBeat);
        } else
            ctx.fireChannelRead(msg);
    }

    private NettyMessage buildHeatBeat() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(Header.MessageType.HEARTBEAT_RESP.getValue());
        message.setHeader(header);
        return message;
    }

}