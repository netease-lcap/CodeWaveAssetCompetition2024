package com.netease.lib.hktool.structure;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

/**
 * 查询表达式
 */
@NaslStructure
public class ExpressionParam {
    public String key;

    public Integer operatord;

    public List<String> values;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getOperatord() {
        return operatord;
    }

    public void setOperatord(Integer operatord) {
        this.operatord = operatord;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}