package com.walker.socket.util;

import io.netty.buffer.ByteBuf;
import org.jboss.marshalling.*;

import java.io.IOException;
/**
 * @author Administrator
 * @version 1.0
 * @date 2014年3月15日
 */
public final class MarshallingCodecUtil {

	/**
	 * 创建Jboss Marshaller
	 *
	 * @return
	 * @throws IOException
	 */
	protected static Marshaller buildMarshalling() throws IOException {
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		Marshaller marshaller = marshallerFactory.createMarshaller(configuration);
		return marshaller;
	}

	/**
	 * 创建Jboss Unmarshaller
	 *
	 * @return
	 * @throws IOException
	 */
	protected static Unmarshaller buildUnMarshalling() throws IOException {
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		final Unmarshaller unmarshaller = marshallerFactory.createUnmarshaller(configuration);
		return unmarshaller;
	}



	private static final byte[] LENGTH_PLACEHOLDER = new byte[4];

	// 使用marshall对Object进行编码，并且写入bytebuf...
	public static void encode(Object msg, ByteBuf out) throws Exception {
		Marshaller marshaller = MarshallingCodecUtil.buildMarshalling();
		try {
			//1. 获取写入位置
			int lengthPos = out.writerIndex();
			//2. 先写入4个bytes，用于记录Object对象编码后长度
			out.writeBytes(LENGTH_PLACEHOLDER);
			//3. 使用代理对象，防止marshaller写完之后关闭byte buf
			ChannelBufferByteOutput output = new ChannelBufferByteOutput(out);
			//4. 开始使用marshaller往bytebuf中编码
			marshaller.start(output);
			marshaller.writeObject(msg);
			//5. 结束编码
			marshaller.finish();
			//6. 设置对象长度
			out.setInt(lengthPos, out.writerIndex() - lengthPos - 4);
		} finally {
			marshaller.close();
		}
	}


	public static Object decode(ByteBuf in) throws Exception {
		Unmarshaller unmarshaller = MarshallingCodecUtil.buildUnMarshalling();

		//1. 读取第一个4bytes，里面放置的是object对象的byte长度
		int objectSize = in.readInt();
		ByteBuf buf = in.slice(in.readerIndex(), objectSize);
		//2 . 使用bytebuf的代理类
		ByteInput input = new ChannelBufferByteInput(buf);
		try {
			//3. 开始解码
			unmarshaller.start(input);
			Object obj = unmarshaller.readObject();
			unmarshaller.finish();
			//4. 读完之后设置读取的位置
			in.readerIndex(in.readerIndex() + objectSize);
			return obj;
		} finally {
			unmarshaller.close();
		}
	}

	/**
	 * {@link ByteOutput} implementation which writes the data to a {@link ByteBuf}
	 *
	 *
	 */
	static class ChannelBufferByteOutput implements ByteOutput {

		private final ByteBuf buffer;

		/**
		 * Create a new instance which use the given {@link ByteBuf}
		 */
		public ChannelBufferByteOutput(ByteBuf buffer) {
			this.buffer = buffer;
		}

		@Override
		public void close() throws IOException {
			// Nothing to do
		}

		@Override
		public void flush() throws IOException {
			// nothing to do
		}

		@Override
		public void write(int b) throws IOException {
			buffer.writeByte(b);
		}

		@Override
		public void write(byte[] bytes) throws IOException {
			buffer.writeBytes(bytes);
		}

		@Override
		public void write(byte[] bytes, int srcIndex, int length) throws IOException {
			buffer.writeBytes(bytes, srcIndex, length);
		}

		/**
		 * Return the {@link ByteBuf} which contains the written content
		 *
		 */
		ByteBuf getBuffer() {
			return buffer;
		}
	}

	/**
	 * {@link ByteInput} implementation which reads its data from a {@link ByteBuf}
	 */
	static class ChannelBufferByteInput implements ByteInput {

		private final ByteBuf buffer;

		public ChannelBufferByteInput(ByteBuf buffer) {
			this.buffer = buffer;
		}

		@Override
		public void close() throws IOException {
			// nothing to do
		}

		@Override
		public int available() throws IOException {
			return buffer.readableBytes();
		}

		@Override
		public int read() throws IOException {
			if (buffer.isReadable()) {
				return buffer.readByte() & 0xff;
			}
			return -1;
		}

		@Override
		public int read(byte[] array) throws IOException {
			return read(array, 0, array.length);
		}

		@Override
		public int read(byte[] dst, int dstIndex, int length) throws IOException {
			int available = available();
			if (available == 0) {
				return -1;
			}

			length = Math.min(available, length);
			buffer.readBytes(dst, dstIndex, length);
			return length;
		}

		@Override
		public long skip(long bytes) throws IOException {
			int readable = buffer.readableBytes();
			if (readable < bytes) {
				bytes = readable;
			}
			buffer.readerIndex((int) (buffer.readerIndex() + bytes));
			return bytes;
		}

	}
}