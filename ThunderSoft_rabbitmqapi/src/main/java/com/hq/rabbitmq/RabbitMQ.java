package com.hq.rabbitmq;

import com.hq.rabbitmq.model.*;
import com.hq.rabbitmq.template.LogicTemplate;
import com.netease.lowcode.core.annotation.NaslLogic;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.rabbitmq.client.ReturnListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.function.Function;

@Component
public class RabbitMQ {

	private static final Logger log = LoggerFactory.getLogger(RabbitMQ.class);

	private static Channel rabbitMQChannel;

	/**
	 * 发送消息
	 *
	 * @param sendModel
	 * @return
	 */
	@NaslLogic
	public static Response send(SendModel sendModel) {
		return sendBase(sendModel, false, false, null);
	}

	/**
	 * 发送消息
	 *
	 * @param sendModel
	 * @param mandatory    是否要求消息无法路由到队列时返回给发送方，发送方可以在channel监听返回的消息并进行处理
	 * @param immediate    是否要求消息没有消费者时返回给生产者，发送方可以channel监听返回的消息并进行处理
	 * @param handleReturn 返回的消息处理逻辑
	 * @return
	 */
	@NaslLogic
	public static Response sendBase(SendModel sendModel,
									Boolean mandatory,
									Boolean immediate,
									Function<ReturnModel, Boolean> handleReturn) {

		return LogicTemplate.execute(() -> {
			try {
				if (Objects.isNull(sendModel)) {
					return Response.FAIL("sendModel参数为空！");
				}
				if (StringUtils.isBlank(sendModel.msg)) {
					return Response.FAIL("发送的消息msg为空！");
				}
				RabbitMQueueDeclare declare = sendModel.declare;
				if (Objects.isNull(declare)) {
					return Response.FAIL("declare队列声明信息为空！");
				}

				rabbitMQChannel.queueDeclare(declare.queueName, declare.durable, declare.exclusive, declare.autoDelete, null);

				AMQP.BasicProperties basicProperties = buildProperties(sendModel.properties);

				if (mandatory || immediate) {
					if (Objects.isNull(handleReturn)) {
						return Response.FAIL("消息发送失败处理逻辑handleReturn为空！");
					}
					rabbitMQChannel.addReturnListener(new ReturnListener() {
						/**
						 * 返回监听器处理
						 *
						 * @param i                 消息响应码
						 * @param s                 消息响应文本
						 * @param s1                返回消息的交换机exchange
						 * @param s2                返回消息的队列名
						 * @param basicProperties   返回消息的属性信息
						 * @param bytes             返回的消息内容
						 * @throws IOException
						 */
						@Override
						public void handleReturn(int i, String s, String s1, String s2, AMQP.BasicProperties basicProperties, byte[] bytes) throws IOException {
							log.info("消息路由失败！queueName=[{}],exchange=[{}],text=[{}]", s2, s1, s);
							ReturnModel t = new ReturnModel();
							t.setCode(i);
							t.setMsg(s);
							t.setExchange(s1);
							t.setQueueName(s2);
							t.buildProperties(basicProperties);
							t.setContent(new String(bytes, StandardCharsets.UTF_8));
							handleReturn.apply(t);
						}
					});
				} else {
					rabbitMQChannel.clearReturnListeners();
				}
				String exchange = "";

				if (Objects.nonNull(sendModel.exchangeDeclare)) {
					RabbitMQExchangeDeclare exchangeDeclare = sendModel.exchangeDeclare;
					if (StringUtils.isNotBlank(exchangeDeclare.exchange)) {

						if (StringUtils.isBlank(exchangeDeclare.exchangeType)) {
							return Response.FAIL("设置交换机的情况下，交换机类型不能为空！");
						}
						exchange = exchangeDeclare.exchange;
						rabbitMQChannel.exchangeDeclare(exchange, exchangeDeclare.exchangeType, exchangeDeclare.durable, exchangeDeclare.autoDelete, exchangeDeclare.internal1, null);
						rabbitMQChannel.queueBind(declare.queueName, exchange, exchangeDeclare.routeKey);
					}
					rabbitMQChannel.basicPublish(exchange, exchangeDeclare.routeKey, mandatory, immediate, basicProperties, sendModel.msg.getBytes(StandardCharsets.UTF_8));

					return Response.OK("发送成功！");
				}
				rabbitMQChannel.basicPublish(exchange, declare.queueName, mandatory, immediate, basicProperties, sendModel.msg.getBytes(StandardCharsets.UTF_8));
				return Response.OK("发送成功！");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, "RabbitMQ#send", sendModel, mandatory, immediate);
	}

	private static AMQP.BasicProperties buildProperties(RabbitMQProperties properties) {

		if (Objects.isNull(properties)) {
			return new AMQP.BasicProperties.Builder().build();
		}

		AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();

		if (StringUtils.isNotBlank(properties.contentType)) {
			builder.contentType(properties.contentType);
		}
		if (StringUtils.isNotBlank(properties.contentEncoding)) {
			builder.contentEncoding(properties.contentEncoding);
		}
		if (Objects.nonNull(properties.deliveryMode)) {
			builder.deliveryMode(properties.deliveryMode);
		}
		if (Objects.nonNull(properties.priority)) {
			builder.priority(properties.priority);
		}
		if (StringUtils.isNotBlank(properties.correlationId)) {
			builder.correlationId(properties.correlationId);
		}
		if (StringUtils.isNotBlank(properties.replyTo)) {
			builder.replyTo(properties.replyTo);
		}
		if (StringUtils.isNotBlank(properties.expiration)) {
			builder.expiration(properties.expiration);
		}
		if (StringUtils.isNotBlank(properties.messageId)) {
			builder.messageId(properties.messageId);
		}
		if (StringUtils.isNotBlank(properties.type)) {
			builder.type(properties.type);
		}
		if (StringUtils.isNotBlank(properties.userId)) {
			builder.userId(properties.userId);
		}
		if (StringUtils.isNotBlank(properties.appId)) {
			builder.appId(properties.appId);
		}
		if (StringUtils.isNotBlank(properties.clusterId)) {
			builder.clusterId(properties.clusterId);
		}

		return builder.build();
	}

	/**
	 * 订阅消息
	 *
	 * @param queueName
	 * @param handleMsg
	 * @return
	 */
	@NaslLogic
	public static Response subscribe(String queueName, Function<String, Boolean> handleMsg) {
		return LogicTemplate.execute(() -> {
			try {
				rabbitMQChannel.queueDeclare(queueName, true, false, false, null);
				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					byte[] body = delivery.getBody();
					String s = new String(body, StandardCharsets.UTF_8);
					log.info("接收到消息queue=[{}],msg=[{}]", queueName, s);
					handleMsg.apply(s);
				};

				rabbitMQChannel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
				});

				return Response.OK("已准备好接收消息！");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, "RabbitMQ#subscribe", queueName);
	}

	/**
	 * 订阅消息
	 * 交换机 topic
	 *
	 * @param queueName
	 * @param handleMsg
	 * @return
	 */
	@NaslLogic
	public static Response subscribeExchange(String queueName, String exchange, String type, String routingKey,
											 Function<String, Boolean> handleMsg) {
		return LogicTemplate.execute(() -> {
			try {
				//交换机类型
				rabbitMQChannel.exchangeDeclare(exchange, type, true);
				//声明队列
				rabbitMQChannel.queueDeclare(queueName, true, false, false, null);
				//绑定队列和交换机
				rabbitMQChannel.queueBind(queueName, exchange, routingKey);
				DeliverCallback deliverCallback = (consumerTag, delivery) -> {
					byte[] body = delivery.getBody();
					String s = new String(body, StandardCharsets.UTF_8);
					log.info("接收到消息queue=[{}],msg=[{}]", queueName, s);
					handleMsg.apply(s);
				};
				rabbitMQChannel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
				});
				return Response.OK("已准备好接收消息！");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}, "RabbitMQ#subscribeExchange", queueName);
	}
	@Autowired
	@Qualifier("rabbitMQChannel")
	public void setRabbitMQChannel(Channel rabbitMQChannel) {
		RabbitMQ.rabbitMQChannel = rabbitMQChannel;
	}
}