package com.walker.socket.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MessagePackEncoder extends MessageToByteEncoder<Object> {
	@Override
	protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
		MessagePack messagePack = new MessagePack();
		byte[] raw = messagePack.write(o);
		byteBuf.writeBytes(raw);
	}
}
