package com.hkgapi.hktool.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class QueryInfo {
    public String key;

    public String value;

    @JSONField(name = "operator")
    public String operatorT;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }


    public String getOperatorT() {
        return operatorT;
    }

    public void setOperatorT(String operatorT) {
        this.operatorT = operatorT;
    }
}