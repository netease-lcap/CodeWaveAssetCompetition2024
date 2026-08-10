package com.fdddf.wechat;

import com.fdddf.wechat.implement.IWeixinResponse;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class WeChatResponse implements IWeixinResponse {

    public Integer errcode;
    public String errmsg;

    public WeChatResponse() {}

    public WeChatResponse(Integer errcode, String errmsg) {
        this.errcode = errcode;
        this.errmsg = errmsg;
    }

    @Override
    public String toString() {
        return "WeChatResponse{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                '}';
    }
}