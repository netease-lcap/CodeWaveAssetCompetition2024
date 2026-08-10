package com.fdddf.wechat.email;

import com.fdddf.wechat.implement.IWeixinRequest;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailUnreadRequest implements IWeixinRequest {
    public String userid;

    public EmailUnreadRequest() {}

    public EmailUnreadRequest(String userid) {
        this.userid = userid;
    }
}
