package com.walker.socket.encode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.msgpack.MessagePack;

import java.util.List;

public class MessagePackDecoder extends MessageToMessageDecoder<ByteBuf> {

	/**
	 * 包头长度
	 */
	public final static int BASE_LENGTH = 5;
	public final static char START_WITH = '^';
	public final static String ENCODE = "UTF-8";

	/**
	 * 将int数值转换为占四个字节的byte数组，本方法适用于(高位在前，低位在后)的顺序。 和bytesToInt2（）配套使用
	 */
	public static byte[] int2bytes(int value) {
		byte[] b = new byte[4];
		for (int i = 0; i < 4; i++) {
			b[i] = (byte) (value >> (24 - i * 8));
		}
		return b;
	}

	public static int bytes2int(byte[] b) {
		return (((int) b[0] << 24) + (((int) b[1]) << 16) + (((int) b[2]) << 8) + b[3]);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
		int len = buffer.readableBytes();
		byte[] array = new byte[len];
		buffer.getBytes(buffer.readerIndex(), array, 0, len);
		MessagePack messagePack = new MessagePack();
		out.add(messagePack.read(array));
	}

}
