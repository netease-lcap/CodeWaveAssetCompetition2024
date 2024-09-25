package com.hq.rabbitmq.model;

import com.netease.lowcode.core.annotation.NaslStructure;
import com.rabbitmq.client.AMQP;

@NaslStructure
public class ReturnModel {
    public Integer code;
    public String msg;
    public String exchange;
    public String queueName;
    public RabbitMQProperties properties;
    public String content;

    public void buildProperties(AMQP.BasicProperties basicProperties) {
        RabbitMQProperties props = new RabbitMQProperties();
        props.contentType = basicProperties.getContentType();
        props.contentEncoding = basicProperties.getContentEncoding();
        props.deliveryMode = basicProperties.getDeliveryMode();
        props.priority = basicProperties.getPriority();
        props.correlationId = basicProperties.getCorrelationId();
        props.replyTo = basicProperties.getReplyTo();
        props.expiration = basicProperties.getExpiration();
        props.messageId = basicProperties.getMessageId();
        props.type = basicProperties.getType();
        props.userId = basicProperties.getUserId();
        props.appId = basicProperties.getAppId();
        props.clusterId = basicProperties.getClusterId();
        this.properties = props;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public RabbitMQProperties getProperties() {
        return properties;
    }

    public void setProperties(RabbitMQProperties properties) {
        this.properties = properties;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
