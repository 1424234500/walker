package com.walker.socket.base.encode;
import com.walker.core.encode.JsonFastUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.Charset;

/**
 * Created by carl.yu on 2016/12/16.
 */
public abstract class AbstractHttpJsonEncoder<T> extends MessageToMessageEncoder<T> {
	final static Charset UTF_8 = Charset.forName("utf-8");

	protected ByteBuf encode0(ChannelHandlerContext ctx, Object body) {
		String jsonStr = JsonFastUtil.toString(body);
		ByteBuf encodeBuf = Unpooled.copiedBuffer(jsonStr, UTF_8);
		return encodeBuf;
	}

}