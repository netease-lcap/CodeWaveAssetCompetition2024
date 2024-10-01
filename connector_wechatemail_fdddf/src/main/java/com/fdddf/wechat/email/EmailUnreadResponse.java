package com.fdddf.wechat.email;

import com.fdddf.wechat.implement.IWeixinResponse;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailUnreadResponse implements IWeixinResponse {
    public Integer errcode;
    public String errmsg;
    /**
     * 成员邮箱中邮件未读数
     */
    public Long count;

    public String toString() {
        return "EmailUnreadResponse{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                ", count=" + count +
                '}';
    }
}
