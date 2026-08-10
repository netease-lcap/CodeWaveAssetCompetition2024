package com.fdddf.websocket.service;

import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class Register {
    private static Function<CommonRequestMessage, CommonReplyMessage> messageHandler;

    /**
     * 注册消息处理函数
     * @param messageHandler 消息处理函数
     * @return 是否注册成功
     */
    @NaslLogic
    public Boolean registerMessageHandler(Function<CommonRequestMessage, CommonReplyMessage> messageHandler)
    {
        Register.messageHandler = messageHandler;
        return true;
    }

    public static Function<CommonRequestMessage, CommonReplyMessage> getMessageHandler() {
        return messageHandler;
    }
}
