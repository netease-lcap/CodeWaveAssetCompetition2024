package com.fdddf.openai;

import com.netease.lowcode.core.annotation.NaslStructure;

import java.util.List;

@NaslStructure
public class ChatResponse {
    public List<ChatMessage> messages;
    public String model;
    public Long promptTokens;
    public Long completionTokens;
    public Long totalTokens;

    public Integer errorCode;
    public String errorMessage;

    @Override
    public String toString() {
        return "ChatResponse{" +
                "messages=" + messages +
                ", model='" + model +
                ", promptTokens=" + promptTokens +
                ", completionTokens=" + completionTokens +
                ", totalTokens=" + totalTokens +
                ", errorCode=" + errorCode +
                ", errorMessage='" + errorMessage +
                '}';
    }
}
