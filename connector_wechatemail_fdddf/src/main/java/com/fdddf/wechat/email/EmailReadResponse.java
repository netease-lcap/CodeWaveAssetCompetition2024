package com.fdddf.wechat.email;

import com.fdddf.wechat.implement.IWeixinResponse;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailReadResponse implements IWeixinResponse{
    public Integer errcode;
    public String errmsg;
    /**
     * 邮件eml内容
     */
    public String mail_data;
}
