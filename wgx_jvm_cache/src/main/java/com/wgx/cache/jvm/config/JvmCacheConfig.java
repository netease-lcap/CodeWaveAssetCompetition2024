package com.wgx.cache.jvm.config;

import com.netease.lowcode.core.EnvironmentType;
import com.netease.lowcode.core.annotation.Environment;
import com.netease.lowcode.core.annotation.NaslConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JvmCacheConfig {

    /**
     * 初始的缓存空间大小 单位为M
     */
    @Value("${initialCapacity}")
    @NaslConfiguration(defaultValue = {
            @Environment(type = EnvironmentType.DEV, value = "10"),
            @Environment(type = EnvironmentType.ONLINE, value = "10")
    })
    public Long initialCapacity;

    /**
     * 缓存的最大条数
     */
    @Value("${maximumSize}")
    @NaslConfiguration(defaultValue = {
            @Environment(type = EnvironmentType.DEV, value = "10000"),
            @Environment(type = EnvironmentType.ONLINE, value = "10000")
    })
    public Long maximumSize;

}
