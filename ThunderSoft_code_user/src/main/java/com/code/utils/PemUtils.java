package com.code.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author: cfn
 * @date: 2024/4/29 14:15
 * @description:
 */
public class PemUtils {

	private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
	private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";
	private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
	private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static KeyPair fromPem(String publicKeyStr, String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		PublicKey publicKey = fromPublicKeyPem(publicKeyStr);
		PrivateKey privateKey = fromPrivateKeyPem(privateKeyStr);
		return new KeyPair(publicKey, privateKey);
	}

	private static PublicKey fromPublicKeyPem(String publicKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		try (PemReader pemReader = new PemReader(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(publicKeyStr.getBytes()))))) {
			PemObject pemObject = pemReader.readPemObject();
			byte[] content = pemObject.getContent();
			X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(content);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PublicKey publicKey = keyFactory.generatePublic(pubKeySpec);
			return publicKey;
		} catch (IOException e) {
			throw new RuntimeException("Failed to read public key PEM", e);
		}
	}

	public static PrivateKey fromPrivateKeyPem(String privateKeyStr) throws NoSuchAlgorithmException, InvalidKeySpecException {
		try (PemReader pemReader = new PemReader(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(privateKeyStr.getBytes()))))) {
			PemObject pemObject = pemReader.readPemObject();
			String privateKeyInPKCS1 = Base64.getEncoder().encodeToString(pemObject.getContent());
			KeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyInPKCS1));
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
			return privateKey;
		} catch (IOException e) {
			throw new RuntimeException("Failed to read private key PEM", e);
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {
		String publicKeyPem = PUBLIC_KEY_HEADER + "\n" +
				"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDGB8WOhZZOYUrGeGKj0tHv/Zpw\n" +
				"ozjF2QkfE+dfF8rFH0j5Y5zJYx+5DaX8WGY3qfK+cB619FoR5Y5nr5z5DnIve8bC\n" +
				"qF3fJGvC8XpWpkME/nnvj32pJjKvJs8nLWt2cr67OtHOCMxAKvfy8XSTQRsQF1zO\n" +
				"Ozg7VbWO0AVHPh7V/QIDAQAB\n" +
				PUBLIC_KEY_FOOTER;
		String privateKeyPem = PRIVATE_KEY_HEADER + "\n" +
				"MIICYAIBAAKBgQCEym8QvYWm/8bQrJlTN+leGjKObnKCxjrcbll8t5ZwfeobuYbI\n" +
				"/vX1tYTuSlSeBB5WmAoCWuCeuC/1rduZZEGaufxKh2JEzMB5NRgMhUbk+msOjNfM\n" +
				"hw/vuAgkLKFTbQ+Q9q0+hTbsoE2uVYaVraJ7i/FCnfTL3NSVfXouPx5FWwIDAQAB\n" +
				"AoGALcabAupSaH9Dyiu1rzpW+FmPlAn+75XmNJbVwmiHM8A0M9qA3EkIAOvSJ1L4\n" +
				"WICS9Mb0rFsnk49q+QY1mkRBbKlJGUuZ3NPzsGD0NAMkf33LYLOgcaB987y5O4d7\n" +
				"ab3t6f9mUGdI5dJ/3GHNsb9KJfKL403QYAtndUerd/3sUAECRQDxNkEM03vmNnrz\n" +
				"1Wv9bc4MCgINVJUAa2SBRF3bC41c1UR/sXF9TRffD5kscHft9v3ci97KDKArNMgA\n" +
				"i5f8JY/qZmLbIQI9AIzuixYQ8DYu/kEvvDHPxg+Eiq2AEMZJ3xR5KCGc8HgvcPpt\n" +
				"7WnjhfGoTgyEklUx89SGG3xo8gOmu0/s+wJEbWldolK/bZIivT1+iWroPn+/pIpM\n" +
				"N+9+nEAFjpkz2vsF4iH7x4lTnaN2qDivvan+Rq7PNLj7Yf3EBrNLEl17ZShiCOEC\n" +
				"PE9vtZ3+didUXaPIkD9TacQdZT1eThY2VTNpByHLIA/eTcQaahOhV0JbJ4MOxpyW\n" +
				"x2UnIh8B2EgOls36OQJFAKV4i7tGvnqH/uZmlP824YpL6dT9O7AyagheU0zvbJaN\n" +
				"/JKninwoklvtgMcYbpofoHvRpyrogerSvkq4dScmoDRtJTKn\n"+
				PRIVATE_KEY_FOOTER;
		KeyPair keyPair = fromPem(publicKeyPem, privateKeyPem);
		System.out.println("Public Key: " + keyPair.getPublic());
		System.out.println("Private Key: " + keyPair.getPrivate());
	}
}
