package com.yourcompany.xml.generator.utils.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 实体(JSON)转XML生成服务
 *
 * <p>将入参的实体JSON按指定类型反序列化后，基于实体字段值生成XML字符串。
 * 生成规则与 {@link XmlGeneratorUtilsService#mapToXml(Map, List, String)} 保持一致：
 * 支持XML声明、缩进、List重复标签、Map嵌套标签、以及在标签名包含属性时的闭合标签处理。</p>
 *
 * <p>说明：由于平台侧入参/返回值不允许使用Object类型，且实体无法添加XML注解，本服务采用：
 * JSON字符串 + Class&lt;T&gt; 的方式还原实体，并开启字段名大小写不敏感以增强兼容性。</p>
 */
@Service
public class XmlEntityToXmlGeneratorService {

    private static final Logger LCAP_LOGGER = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    }

    /**
     * 将实体JSON转换为XML字符串
     *
     * <p>入参作用：</p>
     * <ul>
     *   <li>entityJson：实体数据（JSON字符串）。用于在不暴露Object类型入参的前提下传递复杂结构。</li>
     *   <li>clazz：实体类型。用于将JSON反序列化为目标实体，并支持字段名大小写不敏感匹配。</li>
     *   <li>keyOrder：子节点顺序列表（可为空）。不为空时仅输出列表命中的字段，并按该顺序生成(只控制 根节点下一级 子标签顺序)。</li>
     *   <li>keyOrderByTag：嵌套节点的子节点顺序配置（可为空）。Key为父节点标签名，Value为其子节点顺序。</li>
     *   <li>rootTagName：根节点名称（必填）。可包含属性，例如 {@code Certificate xmlns="..."}。</li>
     * </ul>
     *
     * <p>大小写处理：</p>
     * <ul>
     *   <li>反序列化实体时：字段名大小写不敏感。</li>
     *   <li>生成XML且指定keyOrder时：会对实体字段名做大小写不敏感匹配，但输出标签名以keyOrder中的key为准。</li>
     *   <li>生成XML且指定keyOrderByTag时：会对各层节点的字段名做大小写不敏感匹配，但输出标签名以配置中的key为准。</li>
     * </ul>
     *
     * @param entityJson 实体JSON字符串
     * @param clazz      目标实体类型
     * @param keyOrder   XML子节点顺序列表（可为空）
     * @param keyOrderByTag 嵌套节点的子节点顺序配置（可为空）
     * @param rootTagName XML根节点名称（必填）
     * @param <T>        目标实体泛型类型
     * @return 生成的XML字符串；当必填入参为空时返回null
     */
    @NaslLogic
    public <T> String entityJsonToXml(String entityJson, Class<T> clazz, List<String> keyOrder, Map<String, List<String>> keyOrderByTag, String rootTagName) {
        if (entityJson == null || entityJson.trim().isEmpty()) {
            LCAP_LOGGER.error("输入实体 JSON 为空");
            return null;
        }
        if (clazz == null) {
            LCAP_LOGGER.error("实体类型 clazz 为空");
            return null;
        }
        if (rootTagName == null || rootTagName.trim().isEmpty()) {
            LCAP_LOGGER.error("根节点名称不能为空");
            return null;
        }

        try {
            T entity = OBJECT_MAPPER.readValue(entityJson, clazz);
            Map<String, Object> dataMap = OBJECT_MAPPER.convertValue(entity, new TypeReference<Map<String, Object>>() {});

            StringBuilder xml = new StringBuilder();
            xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");

            if (keyOrder != null && !keyOrder.isEmpty()) {
                String pureRootTagName = rootTagName.trim().split("\\s+")[0];
                xml.append("<").append(rootTagName).append(">\n");

                Map<String, String> lowerKeyToOriginalKey = new HashMap<>();
                for (String key : dataMap.keySet()) {
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
                    if (trimmedKey.isEmpty()) {
                        continue;
                    }

                    String matchedKey = dataMap.containsKey(trimmedKey)
                            ? trimmedKey
                            : lowerKeyToOriginalKey.get(trimmedKey.toLowerCase());
                    if (matchedKey == null) {
                        continue;
                    }

                    Object value = dataMap.get(matchedKey);
                    appendTag(xml, trimmedKey, value, "    ", keyOrderByTag, pureRootTagName + "/" + trimmedKey);
                }

                xml.append("</").append(pureRootTagName).append(">\n");
            } else {
                String pureRootTagName = rootTagName.trim().split("\\s+")[0];
                appendTag(xml, rootTagName, dataMap, "", keyOrderByTag, pureRootTagName);
            }

            return xml.toString();
        } catch (Exception e) {
            LCAP_LOGGER.error("实体 JSON 转 XML 失败: " + e.getMessage(), e);
            throw new RuntimeException("实体 JSON 转 XML 失败", e);
        }
    }

    private void appendTag(StringBuilder sb, String tagName, Object value, String indent, Map<String, List<String>> keyOrderByTag, String path) {
        String pureTagName = tagName.split("\\s+")[0];

        if (value == null) {
            sb.append(indent).append("<").append(tagName).append("/>\n");
            return;
        }

        Object processingValue = value;

        if (value instanceof String) {
            String strVal = ((String) value).trim();
            if ((strVal.startsWith("{") && strVal.endsWith("}")) || (strVal.startsWith("[") && strVal.endsWith("]"))) {
                try {
                    processingValue = OBJECT_MAPPER.readValue(strVal, Object.class);
                } catch (Exception ignored) {
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
            String valStr = escapeXml(processingValue.toString());
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
}
