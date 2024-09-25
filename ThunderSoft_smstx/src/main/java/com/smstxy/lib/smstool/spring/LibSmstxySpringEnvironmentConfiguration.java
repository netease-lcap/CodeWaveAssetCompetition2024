package com.smstxy.lib.smstool.spring;

import com.smstxy.lib.smstool.LibraryAutoScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 加入spring环境配置（在spring.factories中指定）
 */
@Configuration
@ComponentScan(basePackageClasses=LibraryAutoScan.class)
public class LibSmstxySpringEnvironmentConfiguration {
    public LibSmstxySpringEnvironmentConfiguration() {
        System.out.println ("LibDemoRedisSpringEnvironmentConfiguration");
    }
}

