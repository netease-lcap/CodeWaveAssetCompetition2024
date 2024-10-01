package com.fdddf.wechat.email;

import com.fdddf.wechat.implement.IWeixinRequest;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailScheduleRequest implements IWeixinRequest {

    public EmailRecipient to;
    public EmailRecipient cc;
    public EmailRecipient bcc;
    public String subject;
    public String content;
    public EmailSchedule schedule;
    public Integer enable_id_trans;

    public EmailScheduleRequest() {}

    public EmailScheduleRequest(EmailRecipient to, EmailRecipient cc, EmailRecipient bcc, String subject, String content, EmailSchedule schedule, Integer enable_id_trans) {
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.content = content;
        this.schedule = schedule;
        this.enable_id_trans = enable_id_trans;
    }
}