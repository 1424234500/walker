package com.walker.socket.encode;
import com.walker.socket.model.Header;
import com.walker.socket.model.NettyMessage;
import com.walker.socket.util.MarshallingCodecUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Lilinfeng
 * @version 1.0
 * @date 2014年3月15日
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {


	public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) throws IOException {
		super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
	}

	@Override
	protected Object decode(ChannelHandlerContext ctx, ByteBuf in)
			throws Exception {
		ByteBuf frame = (ByteBuf) super.decode(ctx, in);
		if (frame == null) {
			return null;
		}

		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setCrcCode(frame.readInt());
		header.setLength(frame.readInt());
		header.setSessionID(frame.readLong());
		header.setType(frame.readByte());
		header.setPriority(frame.readByte());

		int size = frame.readInt();
		if (size > 0) {
			Map<String, Object> attch = new LinkedHashMap<>(size);
			int keySize = 0;
			byte[] keyArray = null;
			String key = null;
			for (int i = 0; i < size; i++) {
				keySize = frame.readInt();
				keyArray = new byte[keySize];
				frame.readBytes(keyArray);
				key = new String(keyArray, "UTF-8");
				attch.put(key, MarshallingCodecUtil.decode(frame));
			}
			keyArray = null;
			key = null;
			header.setAttachment(attch);
		}
		if (frame.readableBytes() > 4) {
			message.setBody(MarshallingCodecUtil.decode(frame));
		}
		message.setHeader(header);
		return message;
	}
}