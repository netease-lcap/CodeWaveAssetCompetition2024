package com.netease.lowcode.freemarker.config;

import com.netease.lowcode.core.annotation.NaslConfiguration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class UploadConfig {
    @NaslConfiguration
    public String uploadPort;

    public static String UPLOAD_PORT;

    @PostConstruct
    public void init() {
        UPLOAD_PORT = uploadPort;
    }

    public static String getUploadPort() {
        return UPLOAD_PORT;
    }
}
