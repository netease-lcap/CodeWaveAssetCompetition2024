package com.fdddf.wechat.contact;

import com.fdddf.wechat.implement.IWeixinResponse;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class GetUserIdResponse implements IWeixinResponse {
    public Integer errcode;
    public String errmsg;
    public String userid;

    public GetUserIdResponse() {}
}
