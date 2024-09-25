package com.hkgapi.hktool.param;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class AppPrivilege {
    public String key;

    public String option;

    public String value;

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getOption() {
        return this.option;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}