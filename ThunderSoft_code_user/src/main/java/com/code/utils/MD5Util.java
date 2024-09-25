package com.code.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author: cfn
 * @date: 2024/4/24 11:03
 * @description:
 */
public class MD5Util {

	public static String getMD5Hash(String input) {
		try {
			// 创建MessageDigest实例，指定MD5算法
			MessageDigest md = MessageDigest.getInstance("MD5");
			// 对输入字符串进行MD5散列
			md.update(input.getBytes("UTF-8"));
			// 获取散列值的字节数组
			byte[] digest = md.digest();
			// 将字节数组转换为十六进制字符串
			return bytesToHex(digest);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	private static String bytesToHex(byte[] bytes) {
		// 使用StringBuilder来构建十六进制字符串
		StringBuilder sb = new StringBuilder();
		for (byte b : bytes) {
			sb.append(String.format("%02x", b));
		}
		return sb.toString();
	}
}
