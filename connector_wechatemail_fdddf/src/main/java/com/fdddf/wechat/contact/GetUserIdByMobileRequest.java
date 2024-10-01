package com.fdddf.wechat.contact;

import com.fdddf.wechat.implement.IWeixinRequest;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class GetUserIdByMobileRequest implements IWeixinRequest {
    public String mobile;

    public GetUserIdByMobileRequest(String mobile) {
        this.mobile = mobile;
    }

    public GetUserIdByMobileRequest() {}
}
