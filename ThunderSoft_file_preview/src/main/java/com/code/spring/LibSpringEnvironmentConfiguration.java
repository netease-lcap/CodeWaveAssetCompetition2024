package com.code.spring;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 加入spring环境配置（在spring.factories中指定）
 * @author ts
 */
@Configuration
@ComponentScan( basePackages = "com.code")
public class LibSpringEnvironmentConfiguration {

}
