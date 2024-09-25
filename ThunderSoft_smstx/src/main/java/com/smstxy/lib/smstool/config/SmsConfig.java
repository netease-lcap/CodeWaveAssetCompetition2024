package com.smstxy.lib.smstool.config;

import com.netease.lowcode.core.EnvironmentType;
import com.netease.lowcode.core.annotation.Environment;
import com.netease.lowcode.core.annotation.NaslConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
@Component
public class SmsConfig {
    /**
     * 地域
     */
    @Value("${region}")
    @NaslConfiguration
    public String region;

    /**
     * 认证的用户名
     */
    @Value("${secretId}")
    @NaslConfiguration
    public String secretId;
    /**
     * 认证的密码
     */
    @Value("${secretKey}")
    @NaslConfiguration
    public String secretKey;

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
