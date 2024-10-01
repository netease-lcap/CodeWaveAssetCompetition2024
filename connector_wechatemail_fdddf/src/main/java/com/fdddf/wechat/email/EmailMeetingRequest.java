package com.fdddf.wechat.email;

import com.fdddf.wechat.implement.IWeixinRequest;
import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailMeetingRequest implements IWeixinRequest {

    /**
     * 邮件接收人
     */
    public EmailRecipient to;
    /**
     * 邮件主题
     */
    public String subject;
    /**
     * 邮件内容
     */
    public String content;
    /**
     * 邮件日程
     */
    public EmailSchedule schedule;
    /**
     * 邮件会议
     */
    public EmailMeeting meeting;
    /**
     * 是否开启id转换
     */
    public Integer enable_id_trans;

    public EmailMeetingRequest() {}

    public EmailMeetingRequest(EmailRecipient to, String subject, String content, EmailSchedule schedule,
            EmailMeeting meeting, Integer enable_id_trans) {
        this.to = to;
        this.subject = subject;
        this.content = content;
        this.schedule = schedule;
        this.meeting = meeting;
        this.enable_id_trans = enable_id_trans;
    }
}