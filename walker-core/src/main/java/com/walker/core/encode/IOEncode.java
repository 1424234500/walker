package com.walker.core.encode;

import com.walker.util.FileUtil;
import com.walker.util.FileUtil.FunArgsReturn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;

/**
 * 输入流编码转输出流
 */
public class IOEncode {
 	public static final String encode = "utf-8";

	private static Logger log = LoggerFactory.getLogger(IOEncode.class);



	/**
	 * 移位 交换
	 */
 	public static final FunArgsReturn<byte[], byte[]> encoder = new FunArgsReturn<byte[], byte[]>() {
		@Override
		public byte[] make(byte[] obj) {
			int d = 4;
			if(obj != null && obj.length > 2){
				for(int i = 0; i < obj.length - d; i+=d) {
					int j = i + d;
//					byte a = obj[j-1];
//					while(j-- > i + 1){
//						obj[j] = obj[j - 1];
//					}
//					obj[i] = a;
					for(int k = i; k < j; k++){
						obj[k] = moveRotate(obj[k], -d);
					}
				}
			}

			return obj;
		}
	};
	public static final FileUtil.FunArgsReturn<byte[], byte[]> decoder =  new FunArgsReturn<byte[], byte[]>() {
		@Override
		public byte[] make(byte[] obj) {
			int d = 4;
			if(obj != null && obj.length > 2){
				for(int i = 0; i < obj.length - d; i+=d) {
					int j = i + d;
					byte a = obj[j-1];
					while(j-- > i + 1){
						obj[j] = obj[j - 1];
					}
					obj[i] = a;
					for(int k = i; k < j; k++){
						obj[k] = moveRotate(obj[k], d);
					}
				}
			}

			return obj;
		}
	};

	public static Object encode(String pathFrom, String pathTo) throws IOException {
		return make(pathFrom, pathTo, encoder);
	}
	public static Object decode(String pathFrom, String pathTo) throws IOException {
		return make(pathFrom, pathTo, decoder);
	}


	public static Object make(String pathFrom, String pathTo, FunArgsReturn<byte[], byte[]> make) throws IOException {
		return make(new File(pathFrom), new File(pathTo), encoder);
	}
 	public static Object make(File fileFrom, File fileTo, FunArgsReturn<byte[], byte[]> make) throws IOException {
		if (!fileFrom.isFile()) {
			throw new FileNotFoundException(fileFrom.getAbsolutePath() + " from no exists ?");
		}
		if (fileTo.isDirectory()) {
			throw new FileAlreadyExistsException(fileFrom.getAbsolutePath() + " to dir ?");
		}
		if (!fileTo.isFile()) {
			FileUtil.mkfile(fileTo.getAbsolutePath());
		}else{
			fileTo.delete();
			fileTo.createNewFile();
		}

		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			inputStream = new FileInputStream(fileFrom);
			outputStream = new FileOutputStream(fileTo);
			FileUtil.copyStream(inputStream, outputStream, FileUtil.SIZE_BUFFER, FileUtil.SIZE_FLUSH, make);
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}

				}

			}
		}
		return null;
	}


	/**
	 * 循环移位
	 * @param sourceByte
	 * @param n >0右移  <0左移
	 */
	public static byte moveRotate(byte sourceByte, int n){
		// 去除高位的1
		int temp = sourceByte & 0xFF;
		n = n % 8;
		if(n > 0){
			return (byte) ((temp >>> n) | (temp << (8 - n)));
		}else if(n < 0){
			n = -n;
			return (byte) ((temp << n) | (temp >>> (8 - n)));
		}
		return sourceByte;
	}


}