package com.fdddf.wechat.email;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class EmailMeetingUsers {
    /**
     * 用户id列表
     */
    public List<String> userids;

    public EmailMeetingUsers() {}

    public EmailMeetingUsers(List<String> userids) {
        this.userids = userids;
    }
}
