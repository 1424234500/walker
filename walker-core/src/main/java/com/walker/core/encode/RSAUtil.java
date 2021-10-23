package com.walker.core.encode;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.security.*;

/**
 * RSA 工具类。提供加密，解密，生成密钥对等方法。
 *
 * <dependency>
 * <groupId>org.bouncycastle</groupId>
 * <artifactId>bcpkix-jdk15on</artifactId>
 * <version>1.60</version>
 * </dependency>
 */
public class RSAUtil {
    static KeyPairGenerator keyPairGen;

    static {
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA",
                    new BouncyCastleProvider());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        // 没什么好说的了，这个值关系到块加密的大小，可以更改，但是不要太大，否则效率会低
        final int KEY_SIZE = 256;//1024;
        keyPairGen.initialize(KEY_SIZE, new SecureRandom());
    }

    /**
     * 生成密钥对 公钥 私钥
     */
    public static KeyPair generateKeyPair() throws Exception {
        try {
            return keyPairGen.generateKeyPair();
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    public static String encrypt(PublicKey pk, String str) throws Exception {
        return new String(encrypt(pk, str.getBytes()));
    }

    /**
     * 加密
     */
    public static byte[] encrypt(PublicKey pk, byte[] data) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance("RSA",
                    new BouncyCastleProvider());
            cipher.init(Cipher.ENCRYPT_MODE, pk);
            // 获得加密块大小，如：加密前数据为128个byte，而key_size=1024
            int blockSize = cipher.getBlockSize();
            // 加密块大小为127
            // byte,加密后为128个byte;因此共有2个加密块，第一个127
            // byte第二个为1个byte
            // 获得加密块加密后块大小
            byte[] raw = new byte[0];
            if (blockSize > 0) {
                int outputSize = cipher.getOutputSize(data.length);
                int leavedSize = data.length % blockSize;
                int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
                        : data.length / blockSize;
                raw = new byte[outputSize * blocksSize];
                int i = 0;
                while (data.length - i * blockSize > 0) {
                    if (data.length - i * blockSize > blockSize) {
                        cipher.doFinal(data, i * blockSize, blockSize, raw, i
                                * outputSize);
                    } else {
                        cipher.doFinal(data, i * blockSize, data.length - i
                                * blockSize, raw, i * outputSize);
                    }
                    // 这里面doUpdate方法不可用，查看源代码后发现每次doUpdate后并没有什么实际动作除了把byte[]放到
                    // ByteArrayOutputStream中，而最后doFinal的时候才将所有的byte[]进行加密，可是到了此时加密块大小很可能已经超出了
                    // OutputSize所以只好用dofinal方法。
                    i++;
                }
            } else {
                LoggerFactory.getLogger(RSAUtil.class).info("block size is zero, please check.");
            }
            return raw;
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }

    /**
     * 解密
     */
    public static byte[] decryptshort(PrivateKey pk, byte[] raw) throws Exception {
        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("RSA/None/PKCS1Padding", "BC");
            cipher.init(Cipher.DECRYPT_MODE, pk);
            return cipher.doFinal(raw);
        } catch (Exception e) {
            throw new Exception(e.getMessage(), e);
        }
    }


}