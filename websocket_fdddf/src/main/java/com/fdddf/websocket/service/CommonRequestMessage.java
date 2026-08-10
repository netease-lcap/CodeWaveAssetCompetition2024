package com.fdddf.websocket.service;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class CommonRequestMessage {
    /**
     * 消息内容
     */
    public String content;

    /**
     * 发送者id
     */
    public Long userId;

    public CommonRequestMessage() {}

    public CommonRequestMessage(String message, long userId) {
        this.content = message;
        this.userId = userId;
    }
}
