package com.netease.lib.tasks.util;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsyncLogicAnnotationConfigUtil {
    private final static Logger log = LoggerFactory.getLogger("LCAP_EXTENSION_LOGGER");

    private static final String ANNOTATION_METADATA_LOGIC = "annotation/annotation_metadata_logic_";
    private static Map<String, List<String>> useAnnoLogicNames = new HashMap<>();

    public static JSONArray readAnnotationFile(String annoName) {
        try {
            ClassPathResource resource = new ClassPathResource(ANNOTATION_METADATA_LOGIC + annoName + ".json");
            InputStream inputStream = resource.getInputStream();
            byte[] bdata = FileCopyUtils.copyToByteArray(inputStream);
            JSONArray array = JSONArray.parseArray(new String(bdata, StandardCharsets.UTF_8));
            return array;
        } catch (Exception e) {
            log.warn("readAnnotationFile error", e);
            // 处理异常
            return null;
        }
    }

    public static List<String> listAlluseAnnoLogicNames(String annoName) {
        if (useAnnoLogicNames != null) {
            List<String> result = useAnnoLogicNames.get(annoName);
            if (result != null) {
                return result;
            }
        }
        JSONArray annoArray = readAnnotationFile(annoName);
        if (annoArray == null) {
            useAnnoLogicNames.put(annoName, new ArrayList<>());
            return new ArrayList<>();
        }
        List<String> result = new ArrayList<>();
        annoArray.forEach(anno -> {
            JSONObject annoObj = (JSONObject) anno;
            String logicName = annoObj.getString("logicName");
            JSONObject annotationProperties = annoObj.getJSONObject("annotationProperties");
            if ("true".equals(annotationProperties.getString("useAnno")) && !StringUtils.isEmpty(logicName)) {
                result.add(logicName);
                result.add(NamingUtils.toLowerCamelCase(logicName));
            }
        });
        useAnnoLogicNames.put(annoName, result);
        return result;
    }
}
