package com.yourcompany.xml.generator.utils.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * XML解析服务
 * 提供将XML字符串转换为Java对象的功能
 */
@Service
public class XmlParseService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");
    
    private static final XmlMapper XML_MAPPER = new XmlMapper();

    static {
        // 配置忽略未知属性，增强容错性
        XML_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 配置属性名称不区分大小写
        XML_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    /**
     * 通用XML解析 (反序列化)
     * 将 XML 字符串转换回指定类型的 Java Bean
     * 
     * @param xmlContent XML字符串内容
     * @param clazz      目标类型的Class对象
     * @param <T>        泛型类型
     * @return 转换后的Java对象
     */
    @NaslLogic
    public <T> T parseXml(String xmlContent, Class<T> clazz) {
        if (xmlContent == null) {
            LCAP_LOGGER.error("XML解析失败: 输入内容为空");
            throw new RuntimeException("XML解析失败: 输入内容为空");
        }
        
        try {
            return XML_MAPPER.readValue(xmlContent, clazz);
        } catch (JsonProcessingException e) {
            String errorMsg = String.format("XML解析失败: 无法将内容转换为类型 [%s]. 错误信息: %s", clazz.getName(), e.getMessage());
            LCAP_LOGGER.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        } catch (Exception e) {
            String errorMsg = String.format("XML解析失败: 发生未知错误. 错误信息: %s", e.getMessage());
            LCAP_LOGGER.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
    }
}
