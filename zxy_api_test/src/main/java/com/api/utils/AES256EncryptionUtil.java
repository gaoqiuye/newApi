package com.api.utils;

import java.math.BigInteger;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class AES256EncryptionUtil {

	public static final String TAG = AES256EncryptionUtil.class.getSimpleName();
	public static final String ALGORITHMSTR = "AES/ECB/PKCS5Padding";
	private static String stKey = "d8cg8gVakEq9Agup";

	/**
	 * 一次性设置password，后面无需再次设置
	 * 
	 * @param secretKey
	 */
	public static void setSecretKey(String secretKey) {
		stKey = secretKey;
	}

	/**
	 * aes加密
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String aesEncrypt(String content) {
		try {
			return aesEncrypt(content, stKey);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * AES加密为base 64 code
	 * 
	 * @param content    待加密的内容
	 * @param encryptKey 加密密钥
	 * @return 加密后的base 64 code
	 * @throws Exception
	 */
	private static String aesEncrypt(String content, String encryptKey) throws Exception {
		return base64Encode(aesEncryptToBytes(content, encryptKey));
	}

	/**
	 * base 64 encode
	 * 
	 * @param bytes 待编码的byte[]
	 * @return 编码后的base64 code
	 */
	public static String base64Encode(byte[] bytes) {
		return Base64.encodeBase64String(bytes);
	}

	/**
	 * AES加密
	 * 
	 * @param content    待加密的内容
	 * @param encryptKey 加密密钥
	 * @return 加密后的byte[]
	 * @throws Exception
	 */
	private static byte[] aesEncryptToBytes(String content, String encryptKey) throws Exception {
		// KeyGenerator 密钥生成器；getInstance获取实例
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		// 128是密钥长度
		// new SecureRandom(aesKey.getBytes())是此密钥生成器的随机源
		// kgen.init(128, new SecureRandom(aesKey.getBytes()));
		kgen.init(128);
		// Cipher创建密码器
		Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
		// 初始化
		cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(encryptKey.getBytes(), "AES"));
		// 加密并返回byte[]结果
		return cipher.doFinal(content.getBytes("utf-8"));
	}

	// *******************************************************************//

	/**
	 * aes解密
	 * 
	 * @param content
	 * @return
	 * @throws Exception
	 */
	public static String aesDecrypt(String encryptStr) {
		try {
			return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), stKey);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	/**
	 * base 64 decode
	 * 
	 * @param base64Code 待解码的base 64 code
	 * @return 解码后的byte[]
	 * @throws Exception
	 */
	public static byte[] base64Decode(String base64Code) throws Exception {
		return StringUtils.isEmpty(base64Code) ? null : new BASE64Decoder().decodeBuffer(base64Code);
	}

	/**
	 * AES解密
	 * 
	 * @param encryptBytes 待解密的byte[]
	 * @param decryptKey   解密密钥
	 * @return 解密后的String
	 * @throws Exception
	 */
	public static String aesDecryptByBytes(byte[] encryptBytes, String decryptKey) throws Exception {
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		kgen.init(128);
		Cipher cipher = Cipher.getInstance(ALGORITHMSTR);
		cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(decryptKey.getBytes(), "AES"));
		byte[] decryptBytes = cipher.doFinal(encryptBytes);
		return new String(decryptBytes);
	}

	/**
	 * 将byte[]转为各种进制的字符串
	 * 
	 * @param bytes byte[]
	 * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
	 * @return 转换后的字符串
	 */
	public static String binary(byte[] bytes, int radix) {
		return new BigInteger(1, bytes).toString(radix);// 这里的1代表正数
	}

	/**
	 * 将base 64 code AES解密
	 * 
	 * @param encryptStr 待解密的base 64 code
	 * @param decryptKey 解密密钥
	 * @return 解密后的string
	 * @throws Exception
	 */
	public static String aesDecrypt(String encryptStr, String decryptKey) throws Exception {
		return StringUtils.isEmpty(encryptStr) ? null : aesDecryptByBytes(base64Decode(encryptStr), decryptKey);
	}

	public static void main(String[] args) throws Exception {
		String enc = "8959477d770c7179dbd578638f77c56a";
		String encA = AES256EncryptionUtil.aesEncrypt(enc);
		String encAB = AES256EncryptionUtil.base64Encode(encA.getBytes());
		System.out.println("加密后的数据：" + encA);
		System.out.println("加密后的数据再次转base64：" + encAB);
		encA = "yRR0rXmkPIK9TFrMr0DLDQ==";
		System.out.println("解密后的数据：" + AES256EncryptionUtil.aesDecrypt(encA));
		String base64Str="vIVmspr0ZsESlZuaKIQyw7oFYOP9";
		
//		String text = "base64 in java8 lib";
//		// 编码
//		String encode = java.util.Base64.getEncoder().encodeToString(text.getBytes(StandardCharsets.UTF_8));
//		System.out.println("64编码后：" + encode);
//		// 解码
//		String ss = "bzVUTtGkdzTZQrEKmntSVA==";
//		String aa = "0GrD6MYSpX0BOZW15fev2w==";
//		String decode = new String(java.util.Base64.getDecoder().decode(ss), StandardCharsets.UTF_8);
//		System.out.println("64解码后：" + decode);

	}
}
