package com.walker.socket.server.netty;

import com.walker.socket.model.Header;
import com.walker.socket.model.NettyMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 二次解码器
 *
 * 只匹配拦截处理指定类型
 * 可作为aop拦截日志等业务
 *
 *
 */
@ChannelHandler.Sharable
public class MessageToMessageDecoderLog extends MessageToMessageDecoder<NettyMessage> {
    public Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void decode(ChannelHandlerContext ctx, NettyMessage msg, List<Object> out) throws Exception {

        log.info("aop " + msg.toString());

        out.add(msg);
    }
}