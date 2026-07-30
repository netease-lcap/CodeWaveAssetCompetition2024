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
public class XmlToBase64ConverterService {

    //参数使用LCAP_EXTENSION_LOGGER后日志会显示在平台日志功能中
    private static final Logger log = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    /**
     * XML字符串转Base64
     *
     * @param xmlContent XML字符串内容
     * @return Base64编码后的字符串
     */
    @NaslLogic
    public String xmlToBase64(String xmlContent) {
        if (xmlContent == null) {
            return null;
        }
        try {
            return Base64.getEncoder().encodeToString(xmlContent.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("Base64 to XML conversion failed: " + e.getMessage(), e);
            throw new RuntimeException("XML to Base64 conversion failed: " + e.getMessage(), e);
        }
    }
}
