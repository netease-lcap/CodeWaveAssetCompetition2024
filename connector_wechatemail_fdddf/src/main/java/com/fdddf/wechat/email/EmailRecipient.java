package com.fdddf.wechat.email;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class EmailRecipient {
    public List<String> emails;
    public List<String> userids;

    public EmailRecipient() {}

    public EmailRecipient(List<String> emails, List<String> userids) {
        this.emails = emails;
        this.userids = userids;
    }
}
