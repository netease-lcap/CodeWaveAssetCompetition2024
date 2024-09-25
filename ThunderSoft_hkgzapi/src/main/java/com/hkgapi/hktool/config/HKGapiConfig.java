package com.hkgapi.hktool.config;

import com.netease.lowcode.core.EnvironmentType;
import com.netease.lowcode.core.annotation.Environment;
import com.netease.lowcode.core.annotation.NaslConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


@Configuration
@Component
public class HKGapiConfig {
    /**
     * 服务地址
     */
    @Value("${host}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "10.2.160.201:443"),
            @Environment(type = EnvironmentType.ONLINE, value = "10.2.160.201:443")})
    public String host;
    /**
     * 服务上下文
     */
    @Value("${artemisPath}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "/artemis"),
            @Environment(type = EnvironmentType.ONLINE, value = "/artemis")})
    public String artemisPath;

    /**
     * 认证的用户名
     */
    @Value("${appKey}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "25231762"),
            @Environment(type = EnvironmentType.ONLINE, value = "25231762")})
    public String appKey;
    /**
     * 认证的密码
     */
    @Value("${appSecret}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "llr0pp87wZese52mZeGJ"),
            @Environment(type = EnvironmentType.ONLINE, value = "llr0pp87wZese52mZeGJ")})
    public String appSecret;


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getArtemisPath() {
        return artemisPath;
    }

    public void setArtemisPath(String artemisPath) {
        this.artemisPath = artemisPath;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }
}
