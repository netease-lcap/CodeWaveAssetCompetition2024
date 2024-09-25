package com.code;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.RSAKeyProvider;
import com.code.utils.JsonSortUtil;
import com.code.utils.PemUtils;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * @author: cfn
 * @date: 2024/4/23 15:12
 * @description:
 */
public class JWTTokenService {

    private static final Base64.Encoder BASE64_ENCODER = Base64.getEncoder();

    private static final Base64.Decoder BASE64_DECODER = Base64.getDecoder();

    private static final Logger logger = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    private static final JSONConfig jsonConfig = new JSONConfig();


    static {
        // 必须创建Bouncy Castle提供者
        Security.addProvider(new BouncyCastleProvider());
        jsonConfig.setIgnoreNullValue(false);
    }

    /**
     * 生成token
     *
     * @param requestPath
     * @param requestArgJson
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws IOException
     */
    @NaslLogic
    public static String generateJwtToken(String requestPath, String requestArgJson) throws NoSuchAlgorithmException,
            InvalidKeySpecException, IOException {
        logger.info("jsonString to md5 requestArgJson is:{}",requestArgJson);
        JSONObject json = JSONObject.parseObject(requestArgJson);
        JSONObject jsonObject = JsonSortUtil.sortJSONObject(json);
        jsonConfig.setIgnoreNullValue(false);
        String jsonString = JSONUtil.toJsonStr(jsonObject, jsonConfig);
        // 使用正则表达式替换，确保每个键和值之间有冒号和空格
        // 替换":"后面直接跟着的引号，添加一个空格
        jsonString = jsonString.replace("\":", "\": ");
        jsonString = jsonString.replace(",\"", ", \"");
        jsonString = jsonString.replace("},", "}, ");
        logger.info("jsonString to md5 jsonString is:{}",jsonString);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] encodedBytes = md.digest(jsonString.getBytes(StandardCharsets.UTF_8));
        // 将字节数组转换为十六进制字符串
        StringBuilder sb = new StringBuilder();
        for (byte b : encodedBytes) {
            sb.append(String.format("%02x", b));
        }
        String md5HexDigest = sb.toString();
        //iat时间
        LocalDateTime nowDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = nowDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date iatTime = Date.from(instant);
        // 生成一个随机UUID
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString().replace("-", "");
//        // 创建负载（Payload）
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("request_path", requestPath);
//        payload.put("iat", nowDateTime.format(formatter));
//        // 将UUID转换为标准字符串表示形式，然后替换分隔符"-"为""
//        payload.put("nonce", uuidStr);
//        payload.put("md5", md5HexDigest);
        //解析私钥
        PrivateKey privateKeys;
        Resource resource = new ClassPathResource("idct_private_key.pem");
        InputStream inputStream = resource.getStream();
        try (PemReader pemReader = new PemReader(new InputStreamReader(inputStream))) {
            PemObject pemObject = pemReader.readPemObject();
            String privateKeyInPKCS1 = BASE64_ENCODER.encodeToString(pemObject.getContent());
            KeySpec keySpec = new PKCS8EncodedKeySpec(BASE64_DECODER.decode(privateKeyInPKCS1));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            privateKeys = keyFactory.generatePrivate(keySpec);
        }

        // 生成私钥sign
        PrivateKey finalPrivateKeys = privateKeys;
        RSAKeyProvider keyProvider = new RSAKeyProvider() {
            @Override
            public RSAPublicKey getPublicKeyById(String keyId) {
                return null; // 这里仅用作私钥操作，可返回null或实现公钥获取逻辑
            }

            @Override
            public RSAPrivateKey getPrivateKey() {
                return (RSAPrivateKey) finalPrivateKeys;
            }

            @Override
            public String getPrivateKeyId() {
                return null; // 可以自定义私钥ID，也可以返回null
            }
        };
        // 使用HMAC256算法和提供的私钥生成JWT
        return JWT.create()
                .withClaim("request_path", requestPath)
                .withIssuedAt(iatTime)                // 将UUID转换为标准字符串表示形式，然后替换分隔符"-"为""
                .withClaim("nonce", uuidStr)
                .withClaim("md5", md5HexDigest)
                .sign(Algorithm.RSA256(keyProvider));
    }

    /**
     * 生成不同用户的token
     *
     * @param requestPath    请求路径
     * @param requestArgJson 请求参数
     * @param privateKey
     * @return
     */
    @NaslLogic
    public static String generateUserToken(String requestPath, String requestArgJson, String privateKey) throws NoSuchAlgorithmException,
            InvalidKeySpecException {
        // 使用正则表达式替换，确保每个键和值之间有冒号和空格
        // 替换":"后面直接跟着的引号，添加一个空格
        logger.info("jsonString to md5 requestArgJson is:{}",requestArgJson);
        JSONObject json = JSONObject.parseObject(requestArgJson);
        JSONObject jsonObject = JsonSortUtil.sortJSONObject(json);
        jsonConfig.setIgnoreNullValue(false);
        String jsonString = JSONUtil.toJsonStr(jsonObject, jsonConfig);
        // 使用正则表达式替换，确保每个键和值之间有冒号和空格
        // 替换":"后面直接跟着的引号，添加一个空格
        jsonString = jsonString.replace("\":", "\": ");
        jsonString = jsonString.replace(",\"", ", \"");
        jsonString = jsonString.replace("},", "}, ");
        logger.info("jsonString to md5 jsonString is:{}",jsonString);
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] encodedBytes = md.digest(jsonString.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : encodedBytes) {
            sb.append(String.format("%02x", b));
        }
        String md5HexDigest = sb.toString();
        //iat时间
        LocalDateTime nowDateTime = LocalDateTime.now();
        ZonedDateTime zonedDateTime = nowDateTime.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Date iatTime = Date.from(instant);
        // 生成一个随机UUID
        UUID uuid = UUID.randomUUID();
        String uuidStr = uuid.toString().replace("-", "");
//        // 创建负载（Payload）
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("request_path", requestPath);
//        payload.put("iat", nowDateTime.format(formatter));
//        // 将UUID转换为标准字符串表示形式，然后替换分隔符"-"为""
//        payload.put("nonce", uuidStr);
//        payload.put("md5", md5HexDigest);
        //解析私钥
        // 生成私钥sign
        String key = replaceSpaceInKey(privateKey);
        PrivateKey finalPrivateKeys = PemUtils.fromPrivateKeyPem(key);
        RSAKeyProvider keyProvider = new RSAKeyProvider() {
            @Override
            public RSAPublicKey getPublicKeyById(String keyId) {
                return null; // 这里仅用作私钥操作，可返回null或实现公钥获取逻辑
            }

            @Override
            public RSAPrivateKey getPrivateKey() {
                return (RSAPrivateKey) finalPrivateKeys;
            }

            @Override
            public String getPrivateKeyId() {
                return null; // 可以自定义私钥ID，也可以返回null
            }
        };
        // 使用HMAC256算法和提供的私钥生成JWT
        return JWT.create()
                .withClaim("request_path", requestPath)
                .withIssuedAt(iatTime)
                // 将UUID转换为标准字符串表示形式，然后替换分隔符"-"为""
                .withClaim("nonce", uuidStr)
                .withClaim("md5", md5HexDigest)
                .sign(Algorithm.RSA256(keyProvider));
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException, IOException {
        String key = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIErAIBAAKCAQEA6xfwlzuF+BV2YOhqdpaWhzA35OT8qqIBJewKqVVkyQhcSQem\n" +
                "Uhx0JqQP6mXuqWnqVMvZML/N9NOpQJjJunKcCENdz6T7wkmju3+CdGo6m4G1pOmF\n" +
                "P0rBRZRMwdlLS4NM/BzaVnfc6SgaICbBhEThummtc4uc7BC4NTTwJNT8jagpxvj/\n" +
                "jbCAYIWsrw6Ccp3Ws256GoDyYUDDlsaJHcsclFLBzQmM+P9evtJoKqGg8imj+xXF\n" +
                "rVFJlC+GOyET5kcqyOXhmRwEZf/0U8vCZF4KqkVJvfcO2GVOjwKnJYRdDxn9nLx3\n" +
                "mt3KnSdR/6LQi5hDOENPkv5c2vXacqvQi3zpgQIDAQABAoIBAQDLeAfJzEGRynmP\n" +
                "O/hQtNqJHKlzmOA5ikc2HAnKLtcwyuScbFt9u5SnkN0ZgKlDxSCDzX4nnAnT+JEH\n" +
                "EHWfwevblvqNaXxf1j44XA57VSluR5//oACsG7YU01pvd/Kb44LwvMQXjFwNC6E4\n" +
                "7HIWWUuIfEgbtdzVz03VsenTykIr7GhFpRbGoL/4QD/gjT/uQCi/82LfyJ2nA/1E\n" +
                "jyab9TFrBjomeycFE17W3R/v0O+LyBXLS57eoxM3NDF1DEoRySo/sqTGZLruHUoD\n" +
                "wSkUHqgPzdcnO2Avmii0OgVpf0r3cch+tO8u79Y6oRF0u74VmLnmvqCveSlRVoEn\n" +
                "WGaJXs8xAoGJAPsJJujq0nhn0y84PKtru9vi0gQLHL15oh72acTIPi4REIw8Pvoc\n" +
                "7MX/ROAy6HnfHi9vIu3HpQyKSYpdJy1COzUejPJnu1UUDpr3oKqTWPTMZWyu1Wpo\n" +
                "5fuvVtxWxnn0mw5FixjlfRfAqDkZICsyyZCEYltCWlE9vmHEu5dQGlRsIkIbRE84\n" +
                "oe0CeQDvvhTgixNbSAuf4ZADaTHbRHZds6a5vTiH2KHyk+eTc1aiCJjVYh2rqJPk\n" +
                "P6f6uEPV6fubJr1jdOOrQ8sFUlT4N2zo2OU3L5WGEjLuYRj0xMBE+cSvI4b7YnLT\n" +
                "9Ht06UVmLpJ8MB77LWK63EuKQhAb+K07yu5UQ2UCgYkA5jWqXQabJJfx7uefm4Qc\n" +
                "rrT+635VSNz2c3Z94FvRyeUUS8Yj6rID4D8MJp4I21plEBLW8T+YeDuI3T434yVN\n" +
                "rpTppJLEFG1kR3C3AjZF6ZhXmqdqRz8TSQ+ulDlvof9TAm9788TgRpwCqPoFqhfV\n" +
                "CJaerBnZQY9hu77yWPy4aJsRf5YJuqywsQJ5AJFUePNlW2aKVgGliteGfWKeLeK/\n" +
                "dOJDgnFXMVULs+X+lRXEgGFaCQJ2PrCupfWmnfiezpdO9J/Fy/2WKwwezMNrVeoR\n" +
                "EWJSxiFwBs/arHY0jxlBD6nbr4fUNiemnPBSOG+o68O8qAs7iA4eXOyC/6D1Lo6A\n" +
                "hdkuwQKBiQDBoN1Dy+SUym6gdDd1fvWh7f3NNIg5dta3vnGkv623ZqxrqF2ysgMZ\n" +
                "S7fNOKdmnpHatlwawlesEFU+KSu2UBU8JGEyjyh0QGmoP3zlmHHeyqN3gluB1tCr\n" +
                "sC99P0KmUKuKWNRHnOwVEYZxjkSDuUR5so1MaGyvC5LNRVN0wy25MRrNsD0tNMEt\n" +
                "-----END RSA PRIVATE KEY-----";
        String s = generateUserToken("/auth/v1/rbac/user_client",
                "{\"password\":\"WSxiangyannan\",\"gender\":\"false\",\"name\":\"项燕楠\",\"id\":\"19025\",\"age\":\"38\"}", key);
        System.out.println(s);
    }

    public static String replaceSpaceInKey(String keyString) {
        if (Objects.isNull(keyString)) {
            return "";
        }
        String[] lines = keyString.trim().split("\n");
        StringBuilder keyContent = new StringBuilder();
        for (String line : lines) {
            if (line.contains("BEGIN")) {
                keyContent.append(line).append("\n");
            } else if (line.contains("END")) {
                keyContent.append(line).append("\n");
            } else {
                keyContent.append(line.replace(" ", "+")).append("\n");
                ;
            }
        }
        return keyContent.toString();
    }
}
