package com.fdddf.wechat.email;

import com.fdddf.wechat.implement.IWeixinResponse;
import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class EmailListResponse implements IWeixinResponse {
    public Integer errcode;
    public String errmsg;
    /**
     * 应用邮箱账号中邮件未读数
     */
    public String next_cursor;
    /**
     * 是否还有更多数据。0-没有 1-有
     */
    public Integer has_more;
    public List<EmailItem> mail_list;

    public EmailListResponse() {}

    public EmailListResponse(Integer errcode, String errmsg, String next_cursor, Integer has_more,
            List<EmailItem> mail_list) {
        this.errcode = errcode;
        this.errmsg = errmsg;
        this.next_cursor = next_cursor;
        this.has_more = has_more;
        this.mail_list = mail_list;
    }

    @Override
    public String toString() {
        return "MailListResponse{" +
                "errcode=" + errcode +
                ", errmsg='" + errmsg + '\'' +
                ", next_cursor='" + next_cursor + '\'' +
                ", has_more=" + has_more +
                ", mail_list=" + mail_list +
                '}';
    }

}
