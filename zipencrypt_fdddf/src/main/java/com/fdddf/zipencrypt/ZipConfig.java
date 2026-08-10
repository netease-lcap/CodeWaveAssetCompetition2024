package com.fdddf.zipencrypt;

import com.netease.lowcode.core.EnvironmentType;
import com.netease.lowcode.core.annotation.Environment;
import com.netease.lowcode.core.annotation.NaslConfiguration;
import com.netease.lowcode.core.annotation.NaslStructure;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NaslStructure
public class ZipConfig {

    /**
     * 应用https地址，用来上传文件
     */
    @Value("${appUrl}")
    @NaslConfiguration(defaultValue = @Environment(type = EnvironmentType.DEV, value = "https://"))
    public String appUrl;

    /**
     * 默认加密密码
     */
    @Value("${defaultPassword}")
    @NaslConfiguration(defaultValue = @Environment(type = EnvironmentType.DEV, value = "123456"))
    public String defaultPassword = "123456";

    /**
     * 加密方式, 默认AES 0：ZipStandard，1：AES
     */
    @Value("${encryptionMethod}")
    @NaslConfiguration(defaultValue = @Environment(type = EnvironmentType.DEV, value = "1"))
    public Integer encryptionMethod = 1;

    /**
     * 分割大小, 默认为10M
     */
    @Value("${splitSize}")
    @NaslConfiguration(defaultValue = @Environment(type = EnvironmentType.DEV, value = "10485760"))
    public Long splitSize = 10485760L;

    /**
     * 压缩方式，默认为DEFLATE, 0：DEFLATE，1：STORE
     */
    @Value("${compressMethod}")
    @NaslConfiguration(defaultValue = @Environment(type = EnvironmentType.DEV, value = "0"))
    public Integer compressMethod = 0;

    /**
     * 默认压缩级别为5, 1-9, 0 为不压缩
     */
    @Value("${compressLevel}")
    @NaslConfiguration(defaultValue = @Environment(type = EnvironmentType.DEV, value = "5"))
    public Integer compressLevel = 5;

    public EncryptionMethod getEncryptionMethod() {
        if (encryptionMethod == 0) {
            return EncryptionMethod.ZIP_STANDARD;
        } else {
            return EncryptionMethod.AES;
        }
    }

    public CompressionLevel getCompressLevel() {
        // level range
        if (compressLevel < 0 || compressLevel > 9) {
            compressLevel = 5;
        }
        return CompressionLevel.values()[compressLevel];
    }

    public CompressionMethod getCompressMethod() {
        if (compressMethod == 0) {
            return CompressionMethod.DEFLATE;
        } else {
            return CompressionMethod.STORE;
        }
    }
}