package com.walker.core.encode;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.SecureRandom;
/**
 DES加密介绍
 DES是一种对称加密算法，所谓对称加密算法即：加密和解密使用相同密钥的算法。DES加密算法出自IBM的研究，
 后来被美国政府正式采用，之后开始广泛流传，但是近些年使用越来越少，因为DES使用56位密钥，以现代计算能力，
 24小时内即可被破解。虽然如此，在某些简单应用中，我们还是可以使用DES加密算法，本文简单讲解DES的JAVA实现
 。
 注意：DES加密和解密过程中，密钥长度都必须是8的倍数
 */
public class Des {
 	public static final String encode = "utf-8";



	/**
	 * 加密
	 * @param str 需加密的串
	 * @param password 密码
	 */
	public static  String encrypt(String str, String password) throws  Exception {
		String res = "";
		SecureRandom random = new SecureRandom();
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		//创建一个密匙工厂，然后用它把DESKeySpec转换成
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		SecretKey securekey = keyFactory.generateSecret(desKey);
		//Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance("DES");
		//用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, random);
		//现在，获取数据并加密
		//正式执行加密操作
		byte[] bs = str.getBytes(encode);
		bs = cipher.doFinal(bs);
		bs = Base64.encode(bs);
		res = new String(bs, encode);
		return res;
	}
	/**
	 * 解密
	 * @param str 需解密的串
	 * @param password 密码
	 */
	public static String decrypt(String str, String password) throws Exception {
		// DES算法要求有一个可信任的随机数源
		SecureRandom random = new SecureRandom();
		// 创建一个DESKeySpec对象
		DESKeySpec desKey = new DESKeySpec(password.getBytes());
		// 创建一个密匙工厂
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
		// 将DESKeySpec对象转换成SecretKey对象
		SecretKey securekey = keyFactory.generateSecret(desKey);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance("DES");
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, random);
		// 真正开始解密操作

		byte[] bs =  str.getBytes(encode);
		bs = Base64.decode(bs);
		bs = cipher.doFinal(bs);
		return new String(bs, encode);
	}


}