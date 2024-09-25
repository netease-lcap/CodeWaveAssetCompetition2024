package com.code.utils;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.eac.RSAPublicKey;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.pkcs.RSAPrivateKey;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

/**
 * @author: cfn
 * @date: 2024/4/24 9:26
 * @description:
 */
public class AsymmetricKeyUtils {
	private static final String ALGORITHM_NAME = "RSA";
	private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();
	private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

	static {
		// 必须创建Bouncy Castle提供者
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * 格式化密钥为标准Pem格式
	 *
	 * @param keyFormat     密钥格式，参考{@link KeyFormat}
	 * @param asymmetricKey 非对称密钥
	 * @return .pem格式密钥字符串
	 * @throws IOException
	 */
	public static String formatKeyAsPemString(KeyFormat keyFormat, String asymmetricKey) throws IOException {
		byte[] keyInBytes = BASE64_DECODER.decode(asymmetricKey);
		PemObject pemObject = new PemObject(keyFormat.getName(), keyInBytes);
		try (StringWriter stringWriter = new StringWriter()) {
			PemWriter pemWriter = new PemWriter(stringWriter);
			pemWriter.writeObject(pemObject);
			pemWriter.flush();
			return stringWriter.toString();
		}
	}

	/**
	 * 从标准Pem格式中提取密钥
	 *
	 * @param asymmetricKeyAsPem
	 * @return 无---BEGIN---和---END---前后缀的密钥字符串
	 * @throws IOException
	 */
	public static String extractKeyFromPemString(String asymmetricKeyAsPem) throws IOException {
		try (PemReader pemReader = new PemReader(new StringReader(asymmetricKeyAsPem))) {
			PemObject pemObject = pemReader.readPemObject();
			return BASE64_ENCODER.encodeToString(pemObject.getContent());
		}
	}

	/**
	 * 从文件中提取密钥
	 *
	 * @param pemFilePath
	 * @return 无---BEGIN---和---END---前后缀的密钥字符串
	 * @throws Exception
	 */
	public static String readKeyFromPath(String pemFilePath) throws Exception {
		try (PemReader pemReader = new PemReader(new InputStreamReader(Files.newInputStream(Paths.get(pemFilePath))))) {
			PemObject pemObject = pemReader.readPemObject();
			return BASE64_ENCODER.encodeToString(pemObject.getContent());
		}
	}

	/**
	 * 将PKCS1公钥转换为PKCS8公钥
	 *
	 * @param pubKeyInPKCS1 PKCS1形式公钥
	 * @return PKCS8形式公钥
	 * @throws Exception
	 */
	public static String transformPubKeyFromPkcs1ToPkcs8(String pubKeyInPKCS1) throws Exception {
		RSAPublicKey rsaPublicKey = (RSAPublicKey) RSAPublicKey.getInstance(BASE64_DECODER.decode(pubKeyInPKCS1));
		KeySpec keySpec = new RSAPublicKeySpec(rsaPublicKey.getModulus(), rsaPublicKey.getPublicExponent());

		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
		PublicKey publicKey = keyFactory.generatePublic(keySpec);
		return BASE64_ENCODER.encodeToString(publicKey.getEncoded());
	}

	/**
	 * 将PKCS8公钥转换为PKCS1公钥
	 *
	 * @param pubKeyInPKCS8 PKCS8公钥
	 * @return PKCS1公钥
	 */
	public static String transformPubKeyFromPkcs8ToPkcs1(String pubKeyInPKCS8) {
		ASN1Sequence publicKeyASN1Object = ASN1Sequence.getInstance(BASE64_DECODER.decode(pubKeyInPKCS8));
		DERBitString derBitString = (DERBitString) publicKeyASN1Object.getObjectAt(1);
		return BASE64_ENCODER.encodeToString(derBitString.getBytes());
	}

	/**
	 * 将PKCS1私钥转换为PKCS8私钥
	 *
	 * @param privateKeyInPKCS1 PKCS1公钥
	 * @return PKCS8公钥
	 * @throws Exception
	 */
	public static String transformPrivateKeyFromPkcs1ToPkcs8(String privateKeyInPKCS1) throws Exception {
		KeySpec keySpec = new PKCS8EncodedKeySpec(BASE64_DECODER.decode(privateKeyInPKCS1));
		KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
		PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
		return BASE64_ENCODER.encodeToString(privateKey.getEncoded());
	}

	/**
	 * 将PKCS1私钥转换为PKCS8私钥
	 *
	 * @param privateKeyInPKCS8 PKCS8私钥
	 * @return PKCS1私钥
	 * @throws Exception
	 */
	public static String transformPrivateKeyFromPkcs8ToPkcs1(String privateKeyInPKCS8) throws Exception {
		PrivateKeyInfo privateKeyInfo = PrivateKeyInfo.getInstance(BASE64_DECODER.decode(privateKeyInPKCS8));
		RSAPrivateKey rsaPrivateKey = RSAPrivateKey.getInstance(privateKeyInfo.parsePrivateKey());
		return BASE64_ENCODER.encodeToString(rsaPrivateKey.getEncoded());
	}

	public enum KeyFormat {
		/**
		 * PKCS1格式RSA私钥
		 */
		RSA_PRIVATE_KEY_PKCS1("RSA PRIVATE KEY"),
		/**
		 * PKCS8格式RSA私钥
		 */
		RSA_PRIVATE_KEY_PKCS8("PRIVATE KEY"),
		/**
		 * PKCS1格式RSA公钥
		 */
		RSA_PUBLIC_KEY_PKCS1("RSA PUBLIC KEY"),
		/**
		 * PKCS8格式RSA公钥
		 */
		RSA_PUBLIC_KEY_PKCS8("PUBLIC KEY");

		private String name;

		KeyFormat(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public static void main(String[] args) throws Exception {
		System.out.println();

		String privateKeyInPkcs1 = AsymmetricKeyUtils.readKeyFromPath("src/main/resources/idct_private_key.pem");
		System.out.println("读取到的PKCS1格式私钥为：\n" + privateKeyInPkcs1);
		String privateKeyInPkcs8 = AsymmetricKeyUtils.transformPrivateKeyFromPkcs1ToPkcs8(privateKeyInPkcs1);
		System.out.println("转换后的PKCS8格式私钥为：\n" + privateKeyInPkcs8);
		privateKeyInPkcs1 = AsymmetricKeyUtils.transformPrivateKeyFromPkcs8ToPkcs1(privateKeyInPkcs8);
		System.out.println("转换后的PKCS1格式私钥为：\n" + privateKeyInPkcs1);
	}
}
