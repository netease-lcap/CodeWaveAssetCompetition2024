package com.fdddf.openai;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class ChatMessage {

    /**
     * 角色 user: 用户，system：系统，assistant：Assistant
     */
    public String roleName;

    public String content;

    public ChatMessage() {}

    public ChatMessage(String roleName, String content) {
        this.content = content;
        this.roleName = roleName;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "roleName='" + roleName + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
