package com.fdddf.wechat.contact;

import com.fdddf.wechat.implement.IWeixinRequest;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class GetUserIdByEmailRequest implements IWeixinRequest {
    public String email;
    /**
     * 1-企业邮箱（默认）；2-个人邮箱
     */
    public Integer email_type;

    public GetUserIdByEmailRequest() {}

    public GetUserIdByEmailRequest(String email, Integer email_type) {
        this.email = email;
        this.email_type = email_type;
    }
}
