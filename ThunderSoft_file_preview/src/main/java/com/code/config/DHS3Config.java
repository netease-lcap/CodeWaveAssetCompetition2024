
package com.code.config;

import com.netease.lowcode.core.EnvironmentType;
import com.netease.lowcode.core.annotation.Environment;
import com.netease.lowcode.core.annotation.NaslConfiguration;
import com.netease.lowcode.core.annotation.NaslStructure;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhouzz
 */


@Configuration
@NaslStructure
public class DHS3Config {


    /**
     * s3 AccessKey
     */

    @Value("${amazonS3AccessKey}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "abcccccc"),
            @Environment(type = EnvironmentType.ONLINE, value = "abcccccc")})
    public String amazonS3AccessKey;

    /**
     * s3 SecretKey
     */

    @Value("${amazonS3SecretKey}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "abcccccc"),
            @Environment(type = EnvironmentType.ONLINE, value = "abcccccc")})
    public String amazonS3SecretKey;


    /**
     * S3Address
     */

    @Value("${amazonS3Address}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "http://127.0.0.1:9020"),
            @Environment(type = EnvironmentType.ONLINE, value = "http://127.0.0.1:9020")})
    public String amazonS3Address;

    /**
     * S3Type
     */

    @Value("${amazonS3Type}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "s3"),
            @Environment(type = EnvironmentType.ONLINE, value = "s3")})
    public String amazonS3Type;


    /**
     * 桶
     */
    @Value("${amazonS3Bucket}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "protal"),
            @Environment(type = EnvironmentType.ONLINE, value = "protal")})
    public String amazonS3Bucket;

    /**
     * 文件预览doMain
     */
    @Value("${previewDomainUrl}")
    @NaslConfiguration(defaultValue = {@Environment(type = EnvironmentType.DEV, value = "https://test.com"),
            @Environment(type = EnvironmentType.ONLINE, value = "https://test.com")})
    public String previewDomainUrl;


}

