package com.hq.rabbitmq.rabbitmq;

import com.netease.lowcode.core.EnvironmentType;
import com.netease.lowcode.core.annotation.Environment;
import com.netease.lowcode.core.annotation.NaslConfiguration;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@Configuration
public class RabbitMQConfiguration {

    @Value("${rabbitMQServer}")
    @NaslConfiguration(
            defaultValue = {@Environment(type = EnvironmentType.DEV, value = "172.29.0.11")})
    String rabbitMQServer;
    @Value("${rabbitMQPort}")
    @NaslConfiguration(
            defaultValue = {@Environment(type = EnvironmentType.DEV, value = "5672")})
    Integer rabbitMQPort;
    @Value("${rabbitMQUsername}")
    @NaslConfiguration(
            defaultValue = {@Environment(type = EnvironmentType.DEV, value = "guesthq")})
    String rabbitMQUsername;
    @Value("${rabbitMQPassword}")
    @NaslConfiguration(
            defaultValue = {@Environment(type = EnvironmentType.DEV, value = "guest")})
    String rabbitMQPassword;

    @Bean(name = "rabbitMQChannel")
    public Channel getChannel() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(rabbitMQServer);
        factory.setPort(rabbitMQPort);
        factory.setUsername(rabbitMQUsername);
        factory.setPassword(rabbitMQPassword);
        try {
            Connection connection = factory.newConnection();
            return connection.createChannel();
        } catch (IOException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

}
