package com.hq.rabbitmq.model;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class RabbitMQExchangeDeclare {


    /**
     * rabbitMQ的交换机角色
     */
    public String exchange = "";
    /**
     * 交换机类型，[direct,fanout,headers,topic]
     * 只有topic,headers类型的交换机才会触发handleReturn
     */
    public String exchangeType;

    /**
     * 持久化
     */
    public Boolean durable = true;

    /**
     * 是否自动删除 如果为true，则表示当最后一个绑定到该交换机上的队列被删除后，该交换机会被自动删除
     */
    public Boolean autoDelete = false;

    /**
     *
     */
    public Boolean internal1 = false;

    /**
     * 路由key
     */
    public String routeKey;
}
