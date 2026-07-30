package com.yourcompany.xml.base64.converter.api;

import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * XML Base64转换器
 */
@Service
public class Base64ToXmlConverterService {

    //参数使用LCAP_EXTENSION_LOGGER后日志会显示在平台日志功能中
    private static final Logger log = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    /**
     * Base64字符串转XML
     *
     * @param base64Content Base64编码字符串
     * @return 解码后的XML字符串
     */
    @NaslLogic
    public String base64ToXml(String base64Content) {
        if (base64Content == null) {
            return null;
        }
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Content);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Base64 to XML conversion failed: " + e.getMessage(), e);
            throw new RuntimeException("Base64 to XML conversion failed: " + e.getMessage(), e);
        }
    }
}
