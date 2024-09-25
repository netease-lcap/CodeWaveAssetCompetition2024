package com.code;

import com.netease.lowcode.core.annotation.NaslLogic;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @author: cfn
 * @date: 2024/4/2 14:43
 * @description:
 */
public class AppIdAndSecretGenerator {

	private static final String ALGORITHM = "SHA-256";

	/**
	 * 生成appid
	 *
	 * @return
	 */
	@NaslLogic
	public static String generateAppId() {
		SecureRandom random = new SecureRandom();
		return new BigInteger(130, random).toString(32);
	}

	/**
	 *
	 * 生成appSecret
	 * @param name 子系统名称
	 * @return
	 */
	@NaslLogic
	public static String generateAppSecret(String name) {
		try {
			// 生成一个随机的 AppSecret
			MessageDigest digest = MessageDigest.getInstance(ALGORITHM);
			byte[] hash = digest.digest(name.getBytes());
			return Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}


}
