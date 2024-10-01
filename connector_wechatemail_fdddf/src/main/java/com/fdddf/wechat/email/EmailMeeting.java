package com.fdddf.wechat.email;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class EmailMeeting {
    /**
     * 邮件会议参数
     */
    public EmailMeetingOption option;
    /**
     * 会议主持人列表
     */
    public EmailMeetingUsers hosts;
    /**
     * 会议管理员字段, , 仅可指定1人，只支持传userid
     */
    public EmailMeetingUsers meeting_admins;

    public EmailMeeting() {}

    public EmailMeeting(EmailMeetingOption option, EmailMeetingUsers hosts, EmailMeetingUsers meeting_admins)
    {
        this.option = option;
        this.hosts = hosts;
        this.meeting_admins = meeting_admins;
    }
}