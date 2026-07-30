package com.yourcompany.xml.generator.utils.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * JSON转XML工具库
 */
@Service
public class JsonToXmlGeneratorService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();



    /**
     * 将JSON字符串转换为XML字符串
     *
     * @param jsonStr     JSON字符串
     * @param rootTagName 根节点名称
     * @return XML字符串
     */
    @NaslLogic
    public String jsonToXml(String jsonStr, String rootTagName) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            LCAP_LOGGER.error("Input JSON string is empty");
            return null;
        }
        try {
            // Parse JSON to generic map first
            Map<String, Object> data = OBJECT_MAPPER.readValue(jsonStr, Map.class);
            // Convert to XML using a helper method that handles the generic object structure
            // We cannot call mapToXml directly if it expects Map<String, String> or similar concrete types
            // But since we want to expose a NASL logic, we need to handle the conversion internally
            return convertMapToXml(data, rootTagName);
        } catch (Exception e) {
            LCAP_LOGGER.error("Failed to parse JSON string: " + e.getMessage(), e);
            throw new RuntimeException("JSON parsing error", e);
        }
    }

    private String convertMapToXml(Map<String, Object> data, String rootTagName) {
        if (data == null) return null;
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        appendTag(xml, rootTagName, data, "");
        return xml.toString();
    }

    private void appendTag(StringBuilder sb, String tagName, Object value, String indent) {
        // Extract pure tag name for closing tag (handle attributes in tagName like "Root attr='val'")
        String pureTagName = tagName.split("\\s+")[0];

        if (value == null) {
            sb.append(indent).append("<").append(tagName).append("/>\n");
            return;
        }

        if (value instanceof Map) {
            sb.append(indent).append("<").append(tagName).append(">\n");
            Map<?, ?> map = (Map<?, ?>) value;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (entry.getKey() != null) {
                    appendTag(sb, entry.getKey().toString(), entry.getValue(), indent + "    ");
                }
            }
            sb.append(indent).append("</").append(pureTagName).append(">\n");
        } else if (value instanceof List) {
            List<?> list = (List<?>) value;
            for (Object item : list) {
                appendTag(sb, tagName, item, indent);
            }
        } else {
            sb.append(indent).append("<").append(tagName).append(">");
            String valStr = value.toString();
            valStr = escapeXml(valStr);
            sb.append(valStr);
            sb.append("</").append(pureTagName).append(">\n");
        }
    }

    private String escapeXml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&apos;");
    }
}
