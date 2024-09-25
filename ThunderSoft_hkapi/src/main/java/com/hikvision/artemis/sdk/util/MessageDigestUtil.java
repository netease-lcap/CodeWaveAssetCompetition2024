//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hikvision.artemis.sdk.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

public class MessageDigestUtil {
    public MessageDigestUtil() {
    }

    public static String base64AndMD5(String str) {
        if (str == null) {
            throw new IllegalArgumentException("inStr can not be null");
        } else {
            return base64AndMD5(toBytes(str)).trim();
        }
    }

    public static String base64AndMD5(byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException("bytes can not be null");
        } else {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.reset();
                md.update(bytes);
                Base64 base64 = new Base64();
                byte[] enbytes = base64.encode(md.digest());
                return new String(enbytes);
            } catch (NoSuchAlgorithmException var4) {
                throw new IllegalArgumentException("unknown algorithm MD5");
            }
        }
    }

    public static String utf8ToIso88591(String str) {
        if (str == null) {
            return str;
        } else {
            try {
                return new String(str.getBytes("UTF-8"), "ISO-8859-1");
            } catch (UnsupportedEncodingException var2) {
                throw new RuntimeException(var2.getMessage(), var2);
            }
        }
    }

    public static String iso88591ToUtf8(String str) {
        if (str == null) {
            return str;
        } else {
            try {
                return new String(str.getBytes("ISO-8859-1"), "UTF-8");
            } catch (UnsupportedEncodingException var2) {
                throw new RuntimeException(var2.getMessage(), var2);
            }
        }
    }

    private static byte[] toBytes(String str) {
        if (str == null) {
            return null;
        } else {
            try {
                return str.getBytes("UTF-8");
            } catch (UnsupportedEncodingException var2) {
                throw new RuntimeException(var2.getMessage(), var2);
            }
        }
    }
}
