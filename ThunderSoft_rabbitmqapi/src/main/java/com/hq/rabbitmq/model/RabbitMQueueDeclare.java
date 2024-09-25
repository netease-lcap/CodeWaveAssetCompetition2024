package com.hq.rabbitmq.model;

import com.netease.lowcode.core.annotation.NaslStructure;

@NaslStructure
public class RabbitMQueueDeclare {

    /**
     * 队列名称
     */
    public String queueName;

    /**
     * 是否持久化，true在服务器重启时队列会保存
     */
    public Boolean durable = true;
    /**
     * 是否排他，true只有声明这个队列的连接可以使用它，连接关闭时删除队列
     */
    public Boolean exclusive = false;
    /**
     * 是否自动删除，true最后一个消费者断开连接时，自动删除队列
     */
    public Boolean autoDelete = false;

}
