package com.netease.lib.redistemplatetool.spring;

import com.netease.lib.redistemplatetool.LibraryAutoScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 加入spring环境配置（在spring.factories中指定）
 */
@Configuration
@ComponentScan(basePackageClasses = LibraryAutoScan.class)
public class LibDemoRedisSpringEnvironmentConfiguration {
    public LibDemoRedisSpringEnvironmentConfiguration() {
        System.out.println("LibDemoRedisSpringEnvironmentConfiguration");
    }
}
