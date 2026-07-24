package com.netease.http.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

/**
 * json工具类
 *
 * @author xujianping
 */
public class JsonUtil {

    private static final Logger log = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        MAPPER.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        MAPPER.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        MAPPER.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        MAPPER.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    private static final ObjectWriter PRETTY_WRITER = MAPPER.writerWithDefaultPrettyPrinter();

    /**
     * convert obj to json string;
     *
     * @param obj object;
     * @return json string;
     */
    public static String toJson(Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }

        StringWriter writer = new StringWriter();
        try {
            MAPPER.writeValue(writer, obj);
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "illegal object when to json, exception : " + e.getMessage());
        }
        return writer.toString();
    }

    public static String toJsonPrettyString(Object obj) {
        try {
            return PRETTY_WRITER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(
                    "illegal object when to json, exception : " + e.getMessage());
        }
    }

    public static byte[] toByte(Object obj) {
        try {
            return MAPPER.writeValueAsBytes(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("illegal object when to json, exception : " + e);
        }
    }

    /**
     * convert json string to object based clazz no throw expcetion;
     *
     * @param json  json string;
     * @param clazz class;
     * @param <T>   parameter T;
     * @return object;
     */
    public static <T> T fromJsonNoThrow(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, clazz);
        } catch (IOException e) {
            log.error("parse json error", e);
            return null;
        }
    }

    /**
     * convert json string to object based clazz;
     *
     * @param json  json string;
     * @param clazz class;
     * @param <T>   parameter T;
     * @return object;
     */
    public static <T> T fromJson(String json, Class<T> clazz) throws IOException {
        return MAPPER.readValue(json, clazz);
    }

    public static <T> HashMap<String, T> fromJsonToHashMap(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json,
                    MAPPER.getTypeFactory().constructParametricType(HashMap.class, String.class, clazz));
        } catch (IOException e) {
            log.error("parse json error", e);
            return null;
        }
    }

    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        try {
            return MAPPER.readValue(json, MAPPER.getTypeFactory().constructParametricType(List.class, clazz));
        } catch (IOException e) {
            log.error("parse json error", e);
            return null;
        }
    }

    public static Map<String, Object> fromObjectToMap(Object o) {
        try {
            return MAPPER.readValue(MAPPER.writeValueAsString(o), new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            log.error("parse json error", e);
            return null;
        }
    }

    public static Map<String, String> fromStringToMap(String str) {
        try {
            return MAPPER.readValue(str, new TypeReference<Map<String, String>>() {
            });
        } catch (IOException e) {
            log.error("parse json error", e);
            return null;
        }
    }

    public static <T> T fromObjectToObject(Object o, Class<T> clazz) {
        try {
            return MAPPER.readValue(MAPPER.writeValueAsString(o), clazz);
        } catch (IOException e) {
            log.error("parse json error", e);
            return null;
        }
    }

    public static <T> ArrayList<T> fromObjectToArrayList(Object o, Class<T> clazz) {
        try {
            return MAPPER.readValue(MAPPER.writeValueAsString(o),
                    MAPPER.getTypeFactory().constructParametricType(ArrayList.class, clazz));
        } catch (IOException e) {
            log.error("parse json error", e);
            return null;
        }
    }
}
