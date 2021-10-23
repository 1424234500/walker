package com.walker.core.encode;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

//MD5加密工具类  
public class MD5 {
    /**
     * 编码 把value编码成N进制 进制基数编码为start->stop 并填充位数len
     *
     * @param value
     * @param start
     * @param stop
     */
    public static String encode(BigInteger value, int len, char start, char stop) {
        assert (stop >= start + 1); //'0' '1' 2进制
        BigInteger cLen = BigInteger.valueOf(stop - start + 1); //23进制 '0' -> '0' + 23
        StringBuilder sb = new StringBuilder();

        while (value.compareTo(BigInteger.ZERO) > 0) {
//			Tools.out(value, sb.toString());
            sb.append((char) (start + value.mod(cLen).intValue()));//22 -> '0'+22
            value = value.divide(cLen);
        }
        for (int i = 0; i < (len - sb.length()); i++) {
            sb.append("0");
        }
        sb.reverse();

        return sb.toString();
    }
//	1、新建一个值为123的大整数对象
//	BigInteger a=new BigInteger(“123”); //第一种，参数是字符串
//	BigInteger a=BigInteger.valueOf(123); //第二种，参数可以是int、long
//
//	2、大整数的四则运算
//	a. add(b); //a,b均为BigInteger类型，加法
//	a.subtract(b); //减 法
//	a.divide(b); //除法
//	a.multiply(b); //乘法
//
//	3、大整数比较大小
//	a.equals(b); //如果a、b相等返回true否则返回false
//	a.comareTo(b); //a小于b返回-1，等于返回0，大于返回1
//
//	4、常用方法
//	a.mod(b); //求余
//	a.gcd(b); //求最大公约数
//	a.max(b); //求最大值
//	a.min(b); //求最小值


    /**
     * 加密
     */
    public final static String makeStr(String str) {
        if (null == str) {
            str = "";
        }
        String MD5Str = "";
        try {
            // JDK 6 支持以下6种消息摘要算法，不区分大小写
            // md5,sha(sha-1),md2,sha-256,sha-384,sha-512
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(str.getBytes());
            byte[] b = md.digest();

            int i;
            StringBuilder builder = new StringBuilder(32);
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    builder.append("0");
                builder.append(Integer.toHexString(i));
            }
            MD5Str = builder.toString();
            // LogUtil.println("result: " + buf.toString());// 32位的加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return MD5Str;
    }
}