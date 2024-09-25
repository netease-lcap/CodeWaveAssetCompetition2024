package com.code.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.XML;
import com.netease.lowcode.core.annotation.NaslLogic;

/**
 * @author zhouzz
 */
public class XmlToJsonConverter {

    @NaslLogic
    public static String convertXmlToJson(String xml) {
        // 将XML字符串转换为JSONObject
        JSONObject jsonObject = XML.toJSONObject(xml);
        // 将JSONObject转换为JSON字符串
        return jsonObject.toString();
    }
}
