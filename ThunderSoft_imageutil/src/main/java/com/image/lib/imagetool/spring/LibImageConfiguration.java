package com.image.lib.imagetool.spring;

import com.image.lib.imagetool.LibraryAutoScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 加入spring环境配置（在spring.factories中指定）
 */
@Configuration
@ComponentScan(basePackageClasses=LibraryAutoScan.class)
public class LibImageConfiguration {
    public LibImageConfiguration() {
        System.out.println ("LibImageConfiguration");
    }
}

