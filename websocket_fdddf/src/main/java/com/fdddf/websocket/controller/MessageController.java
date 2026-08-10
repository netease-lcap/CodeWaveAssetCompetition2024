package com.fdddf.websocket.controller;

import com.fdddf.websocket.service.*;
import com.netease.lowcode.core.annotation.NaslLogic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.function.Function;

@Component
@Controller
public class MessageController {
    @MessageMapping("/common")
    @SendTo("/topic/common")
    public CommonReplyMessage reply(CommonRequestMessage message) {
        Function<CommonRequestMessage, CommonReplyMessage> handler = Register.getMessageHandler();
        if (handler == null) {
            return new CommonReplyMessage("No handler found to process message: "+ message.content, message.userId);
        }
        return handler.apply(message);
    }

    @Autowired
    private SimpMessagingTemplate template;

    /**
     * 向/topic/common发送消息 [测试用]
     * @param message CommonRequestMessage
     * @return CommonRequestMessage
     */
    @ResponseBody
    @RequestMapping(value = "/sendMessage", method = RequestMethod.POST)
    public CommonRequestMessage testSendMessage(@RequestBody CommonRequestMessage message)
    {
        this.template.convertAndSend("/topic/common", message);
        return message;
    }

    /**
     * 向/topic/common发送消息
     * @param body CommonRequestMessage
     * @return Boolean
     */
    @NaslLogic
    public Boolean sendMessage(CommonRequestMessage body)
    {
        this.template.convertAndSend("/topic/common", body);
        return true;
    }
}
