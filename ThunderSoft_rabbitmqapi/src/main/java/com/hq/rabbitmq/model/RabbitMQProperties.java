package com.hq.rabbitmq.model;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class RabbitMQProperties {

    /**
     * 消息内容类型，例如：application/json 或 text/plain
     */
    public String contentType;
    /**
     * 消息的编码方式，例如 utf-8
     */
    public String contentEncoding;

    /**
     * 消息持久化方式，1表示非持久化、2表示持久化
     */
    public Integer deliveryMode;
    /**
     * 消息的优先级
     */
    public Integer priority;
    /**
     * 消息的关联id
     */
    public String correlationId;
    /**
     * 指定消息的回复队列
     */
    public String replyTo;
    /**
     * 消息的过期时间
     */
    public String expiration;
    /**
     * 指定消息id
     */
    public String messageId;
    /**
     * 消息类型
     */
    public String type;
    /**
     * 消息的用户id
     */
    public String userId;
    /**
     * 消息的应用id
     */
    public String appId;
    /**
     * 消息的集群id
     */
    public String clusterId;

}
