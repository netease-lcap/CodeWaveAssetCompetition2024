package com.fdddf.wechat.email;

import com.fdddf.wechat.implement.IWeixinRequest;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailReadRequest implements IWeixinRequest{

    /**
     * 邮件id
     */
    public String mail_id;

    public EmailReadRequest() {}

    public EmailReadRequest(String mail_id)
    {
        this.mail_id = mail_id;
    }
}
