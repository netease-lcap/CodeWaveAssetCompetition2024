package com.fdddf.wechat.email;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailItem {
    /**
     * 邮件id
     */
    public String mail_id;

    public EmailItem() {}

    public EmailItem(String mail_id) {
        this.mail_id = mail_id;
    }

    public String toString() {
        return "mail_id: " + mail_id;
    }

}
