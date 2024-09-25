package com.hkgapi.hktool.reslut;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class ResponseEntity {
    public String code;

    public String msg;

    public String data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
