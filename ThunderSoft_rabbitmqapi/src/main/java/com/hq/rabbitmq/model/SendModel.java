package com.hq.rabbitmq.model;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class SendModel {

    /**
     * 交换机的声明信息
     */
    public RabbitMQExchangeDeclare exchangeDeclare;

    /**
     * 队列声明信息
     */
    public RabbitMQueueDeclare declare;

    /**
     * 属性信息
     */
    public RabbitMQProperties properties;

    /**
     * 发送的消息内容
     */
    public String msg;

}
