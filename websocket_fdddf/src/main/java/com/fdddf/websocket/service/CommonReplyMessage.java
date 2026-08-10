package com.fdddf.websocket.service;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class CommonReplyMessage {
    /**
     * 消息内容
     */
    public String content;

    /**
     * 发送者id
     */
    public Long userId;

    public CommonReplyMessage() {}

    public CommonReplyMessage(String content, Long userId)
    {
        this.content = content;
        this.userId = userId;
    }
}

