package com.netease.cache.jvm.api;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * @author system
 */
@Configuration
@ComponentScan(basePackageClasses = LibraryAutoScan.class)
public class JvmCacheSpringEnvironmentConfiguration {
}
