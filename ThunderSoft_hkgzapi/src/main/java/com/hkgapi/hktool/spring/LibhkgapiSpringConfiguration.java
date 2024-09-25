package com.hkgapi.hktool.spring;

import com.hkgapi.hktool.LibraryAutoScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 加入spring环境配置（在spring.factories中指定）
 */
@Configuration
@ComponentScan(basePackageClasses= LibraryAutoScan.class)
public class LibhkgapiSpringConfiguration {
    public LibhkgapiSpringConfiguration() {
        System.out.println ("LibhkgapiSpringConfiguration");
    }
}

