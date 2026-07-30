package com.yourcompany.xml.generator.utils.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用XML生成工具库
 */
@Service
public class XmlGeneratorUtilsService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将Map数据转换为XML字符串
     *
     * @param data        包含数据的Map
     * @param keyOrder    Key 顺序列表
     * @param keyOrderByTag 嵌套节点的子节点顺序配置（可为空）
     * @param rootTagName 根节点名称 (如: Certificate)
     * @return XML字符串
     */
    @NaslLogic
    public String mapToXml(Map<String, String> data, List<String> keyOrder, Map<String, List<String>> keyOrderByTag, String rootTagName) {
        if (data == null) {
            LCAP_LOGGER.error("输入数据 Map 为空");
            return null;
        }
        if (rootTagName == null || rootTagName.isEmpty()) {
            LCAP_LOGGER.error("根节点名称不能为空");
            return null;
        }

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

        if (keyOrder != null && !keyOrder.isEmpty()) {
            LCAP_LOGGER.info("正在按照 Key 顺序生成 XML: " + keyOrder);

            String pureTagName = rootTagName.trim().split("\\s+")[0];
            xml.append("<").append(rootTagName).append(">\n");

            Map<String, String> lowerKeyToOriginalKey = new HashMap<>();
            for (String key : data.keySet()) {
                if (key == null) {
                    continue;
                }
                lowerKeyToOriginalKey.putIfAbsent(key.toLowerCase(), key);
            }

            for (String key : keyOrder) {
                if (key == null) {
                    continue;
                }
                String trimmedKey = key.trim();

                String matchedKey = data.containsKey(trimmedKey)
                        ? trimmedKey
                        : lowerKeyToOriginalKey.get(trimmedKey.toLowerCase());
                if (matchedKey == null) {
                    continue;
                }
                String value = data.get(matchedKey);
                appendTag(xml, trimmedKey, value, "    ", keyOrderByTag, pureTagName + "/" + trimmedKey);
            }

            xml.append("</").append(pureTagName).append(">\n");
        } else {
            LCAP_LOGGER.warn("Key 顺序列表为空，回退到默认 Map 遍历顺序。");
            String pureTagName = rootTagName.trim().split("\\s+")[0];
            appendTag(xml, rootTagName, data, "", keyOrderByTag, pureTagName);
        }

        return xml.toString();
    }


    private void appendTag(StringBuilder sb, String tagName, Object value, String indent, Map<String, List<String>> keyOrderByTag, String path) {
        String pureTagName = tagName.split("\\s+")[0];

        if (value == null) {
            sb.append(indent).append("<").append(tagName).append("/>\n");
            return;
        }

        Object processingValue = value;

        // 尝试解析字符串是否为 JSON 对象或数组
        if (value instanceof String) {
            String strVal = ((String) value).trim();
            if ((strVal.startsWith("{") && strVal.endsWith("}")) || (strVal.startsWith("[") && strVal.endsWith("]"))) {
                try {
                    // 尝试反序列化为对象 (Map 或 List)
                    Object parsed = OBJECT_MAPPER.readValue(strVal, Object.class);
                    processingValue = parsed;
                } catch (Exception e) {
                    // 解析失败，视为普通字符串
                    // LCAP_LOGGER.warn("值看起来像 JSON 但解析失败: " + strVal);
                }
            }
        }

        if (processingValue instanceof Map) {
            sb.append(indent).append("<").append(tagName).append(">\n");
            Map<?, ?> map = (Map<?, ?>) processingValue;

            List<String> childKeyOrder = null;
            if (keyOrderByTag != null && !keyOrderByTag.isEmpty()) {
                childKeyOrder = keyOrderByTag.get(pureTagName);
                if ((childKeyOrder == null || childKeyOrder.isEmpty()) && path != null && !path.isEmpty()) {
                    childKeyOrder = keyOrderByTag.get(path);
                }
            }

            if (childKeyOrder != null && !childKeyOrder.isEmpty()) {
                Map<String, String> lowerKeyToOriginalKey = new HashMap<>();
                for (Object keyObj : map.keySet()) {
                    if (keyObj == null) {
                        continue;
                    }
                    String keyStr = keyObj.toString();
                    lowerKeyToOriginalKey.putIfAbsent(keyStr.toLowerCase(), keyStr);
                }

                for (String key : childKeyOrder) {
                    if (key == null) {
                        continue;
                    }
                    String trimmedKey = key.trim();
                    if (trimmedKey.isEmpty()) {
                        continue;
                    }

                    Object matchedKeyObj = map.containsKey(trimmedKey) ? trimmedKey : lowerKeyToOriginalKey.get(trimmedKey.toLowerCase());
                    if (matchedKeyObj == null) {
                        continue;
                    }
                    Object childValue = map.get(matchedKeyObj);
                    appendTag(sb, trimmedKey, childValue, indent + "    ", keyOrderByTag, pathJoin(path, trimmedKey));
                }
            } else {
                for (Map.Entry<?, ?> entry : map.entrySet()) {
                    if (entry.getKey() != null) {
                        appendTag(sb, entry.getKey().toString(), entry.getValue(), indent + "    ", keyOrderByTag, pathJoin(path, entry.getKey().toString()));
                    }
                }
            }
            sb.append(indent).append("</").append(pureTagName).append(">\n");
        } else if (processingValue instanceof List) {
            List<?> list = (List<?>) processingValue;
            for (Object item : list) {
                appendTag(sb, tagName, item, indent, keyOrderByTag, path);
            }
        } else {
            sb.append(indent).append("<").append(tagName).append(">");
            String valStr = processingValue.toString();
            valStr = escapeXml(valStr);
            sb.append(valStr);
            sb.append("</").append(pureTagName).append(">\n");
        }
    }

    private String pathJoin(String base, String child) {
        if (base == null || base.trim().isEmpty()) {
            return child;
        }
        if (child == null || child.trim().isEmpty()) {
            return base;
        }
        return base + "/" + child.trim();
    }

    private String escapeXml(String str) {
        if (str == null) return "";
        return str.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    public static void main(String[] args) {
        XmlGeneratorUtilsService service = new XmlGeneratorUtilsService();

        Map<String, String> inputMap = new java.util.HashMap<>();
        inputMap.put("AplPromise", "{\"AplPromiseCode\":\"1\"}");
        inputMap.put("ModCertificate", null);
        inputMap.put("CertificateList", "{\"Goods\":[{\"HSCode\":\"290219\",\"GoodsItemFlag\":\"N\",\"GoodsName\":\"甲基环己烷\"}]}");
        inputMap.put("CertificateHead", "{\"EntMgrNo\":\"333333053\",\"ApplyType\":\"0\",\"CertStatus\":\"0\",\"CertType\":\"C\",\"CertNo\":\"C251429459072741\"}");

        List<String> keyOrder = java.util.Arrays.asList("AplPromise", "ModCertificate", "CertificateHead", "CertificateList");

        Map<String, List<String>> keyOrderByTag = new java.util.HashMap<>();
        keyOrderByTag.put("CertificateHead", java.util.Arrays.asList("CertNo", "ApplyType", "CertStatus", "CertType", "EntMgrNo"));
        keyOrderByTag.put("CertificateList", java.util.Arrays.asList("Goods"));
        keyOrderByTag.put("Goods", java.util.Arrays.asList("GoodsItemFlag", "HSCode", "GoodsName"));

        String rootTagName = "Certificate xmlns=\"http://www.w3.org/2000/09/xmldsig#\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.w3.org/2000/09/xmldsig#Certificate.xsd\"";

        System.out.println("=== Input Map ===");
        System.out.println(inputMap);

        System.out.println("\n=== Generated XML ===");
        String xml = service.mapToXml(inputMap, keyOrder, keyOrderByTag, rootTagName);
        System.out.println(xml);
    }
}
