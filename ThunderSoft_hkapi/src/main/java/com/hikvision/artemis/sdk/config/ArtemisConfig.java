//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.hikvision.artemis.sdk.config;

public class ArtemisConfig {
    public String host;
    public String appKey;
    public String appSecret;

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAppKey() {
        return this.appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return this.appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public ArtemisConfig() {
    }

    public ArtemisConfig(String host, String appKey, String appSecret) {
        this.host = host;
        this.appKey = appKey;
        this.appSecret = appSecret;
    }
}
