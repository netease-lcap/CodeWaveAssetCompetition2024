package com.netease.cloud;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netease.cloud.dto.BrokerDTO;
import com.netease.cloud.dto.TopicDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest(classes = SpringEnvironmentConfiguration.class)
@RunWith(SpringRunner.class)
public class KafkaTest {
    private static final Logger log = LoggerFactory.getLogger(KafkaTest.class);
    @Autowired
    @Qualifier("kafka1")
    private KafkaConnector kafkaConnector1;
    @Autowired
    @Qualifier("kafka2")
    private KafkaConnector kafkaConnector2;

    /**
     * 发送消息
     */
    @Test
    public void send() {
        Boolean send = kafkaConnector1.send("topic1", "测试发送");
        log.info("发送结果： " + send);
        log.info("===========");
        try {
            //等待接收消息
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 异步发送消息
     */
    @Test
    public void sendAsync() {
        Boolean send = kafkaConnector1.sendAsync("topic1", "测试异步发送");
        log.info("异步发送提交结果： " + send);
        log.info("===========");
        try {
            //等待回调与接收消息
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 按key异步发送消息
     */
    @Test
    public void sendAsyncByKey() {
        Boolean send = kafkaConnector1.sendAsyncByKey("topic1", "key1", "测试按key异步发送");
        log.info("异步发送提交结果： " + send);
        log.info("===========");
        try {
            //等待回调与接收消息
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 测试创建主题
     */
    @Test
    public void testCreateTopic() {
        Boolean test = kafkaConnector1.createTopic("topic1", 1L, 1L);
        log.info("创建主题结果： " + test);
    }

    /**
     * 测试获取主题列表
     */
    @Test
    public void testTopic() throws JsonProcessingException {
        List<TopicDTO> topic = new ObjectMapper().readValue(kafkaConnector1.getTopic(""), new TypeReference<List<TopicDTO>>() {
        });
        topic.forEach(topicDTO -> {
            log.info("主题名称： " + topicDTO.getName());
            log.info("主题分区数： " + topicDTO.getPartitionsCount());
            log.info("主题副本数： " + topicDTO.getReplicaCount());
            log.info("主题消费组： " + topicDTO.getConsumerGroups());
            log.info("主题数据大小： " + topicDTO.getTotalLogSize());
            List<BrokerDTO> brokers = topicDTO.getBrokers();
            brokers.forEach(brokerDTO -> {
                log.info("Broker ID： " + brokerDTO.getId());
                log.info("Broker 地址： " + brokerDTO.getHost());
                log.info("Broker 端口： " + brokerDTO.getPort());
                log.info("Broker Leader 分区： " + brokerDTO.getLeaderPartitions());
                log.info("Broker Follower 分区： " + brokerDTO.getFollowerPartitions());
            });
            log.info("=======================================");
        });
    }

    /**
     * 测试删除主题
     */
    @Test
    public void testDeleteTopic() {
        Boolean test = kafkaConnector1.deleteTopic("topic1");
        log.info("删除主题结果： " + test);
    }
}
