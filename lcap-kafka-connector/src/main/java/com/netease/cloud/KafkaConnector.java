package com.netease.cloud;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.netease.cloud.dto.BrokerDTO;
import com.netease.cloud.dto.TopicDTO;
import com.netease.lowcode.core.annotation.NaslConnector;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.requests.DescribeLogDirsResponse;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.util.StringUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collectors;

@NaslConnector(connectorKind = "kafka")
public class KafkaConnector {

    private static final Logger log = LoggerFactory.getLogger(KafkaConnector.class);
    /**
     * kafka生产者
     */
    private KafkaProducer<String, Object> kafkaProducer;
    /**
     * kafka管理客户端
     */
    private AdminClient adminClient;
    /**
     * 接入点地址
     */
    private String bootstrapServers;

    /**
     * 安全协议
     */
    private String securityProtocol;
    /**
     * 协议机制
     */
    private String saslMechanism;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
    public static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 初始化kafka连接器
     *
     * @param bootstrapServers 集群地址
     * @param securityProtocol 安全协议(PLAINTEXT、SSL、SASL_PLAINTEXT、SASL_SSL)
     * @param saslMechanism    协议机制(GASAPI、PLAIN、SCRAM-SHA-256、SCRAM-SHA-512)
     * @param username         用户名
     * @param password         密码
     * @return
     */
    @NaslConnector.Creator
    public KafkaConnector initBean(String bootstrapServers, String securityProtocol, String saslMechanism, String username, String password) {
        Properties properties = buildProperties(bootstrapServers, securityProtocol, saslMechanism, username, password);
        KafkaConnector kafkaConnector = new KafkaConnector();
        kafkaConnector.bootstrapServers = bootstrapServers;
        kafkaConnector.securityProtocol = securityProtocol;
        kafkaConnector.saslMechanism = saslMechanism;
        kafkaConnector.username = username;
        kafkaConnector.password = password;
        kafkaConnector.kafkaProducer = new KafkaProducer<>(properties);
        kafkaConnector.adminClient = AdminClient.create(properties);
        return kafkaConnector;
    }

    /**
     * 将消息发送给kafka执行的主题
     *
     * @param topic 消息主题
     * @param data  发送的数据
     * @return 发送消息结果
     */
    @NaslConnector.Logic
    public Boolean send(String topic, String data) throws IllegalArgumentException {
        ProducerRecord<String, Object> kafkaMessage = new ProducerRecord<>(topic, data);
        Future<RecordMetadata> send = kafkaProducer.send(kafkaMessage);
        try {
            send.get();
        } catch (InterruptedException | ExecutionException e) {
            log.info("发送消息失败", e);
            throw new IllegalArgumentException(e);
        }
        return true;
    }

    /**
     * 按key分组，将消息发送给kafka执行的主题
     *
     * @param topic 消息主题
     * @param key   发送的key
     * @param data  发送的数据
     * @return 发送消息结果
     */
    @NaslConnector.Logic
    public Boolean sendByKey(String topic, String key, String data) throws IllegalArgumentException {
        ProducerRecord<String, Object> kafkaMessage = new ProducerRecord<>(topic, key, data);
        Future<RecordMetadata> send = kafkaProducer.send(kafkaMessage);
        try {
            send.get();
        } catch (InterruptedException | ExecutionException e) {
            log.info("发送消息失败", e);
            throw new IllegalArgumentException(e);
        }
        return true;
    }

    /**
     * 异步发送消息到kafka主题，不阻塞调用线程，发送结果通过日志记录
     *
     * @param topic 消息主题
     * @param data  发送的数据
     * @return true表示已提交至发送缓冲区
     */
    @NaslConnector.Logic
    public Boolean sendAsync(String topic, String data) {
        ProducerRecord<String, Object> kafkaMessage = new ProducerRecord<>(topic, data);
        kafkaProducer.send(kafkaMessage, (metadata, exception) -> {
            if (exception != null) {
                log.error("异步发送消息失败, topic={}", topic, exception);
            } else if (log.isDebugEnabled()) {
                log.debug("异步发送消息成功, topic={}, partition={}, offset={}",
                        metadata.topic(), metadata.partition(), metadata.offset());
            }
        });
        return true;
    }

    /**
     * 按key分组，异步发送消息到kafka主题，不阻塞调用线程，发送结果通过日志记录
     *
     * @param topic 消息主题
     * @param key   发送的key
     * @param data  发送的数据
     * @return true表示已提交至发送缓冲区
     */
    @NaslConnector.Logic
    public Boolean sendAsyncByKey(String topic, String key, String data) {
        ProducerRecord<String, Object> kafkaMessage = new ProducerRecord<>(topic, key, data);
        kafkaProducer.send(kafkaMessage, (metadata, exception) -> {
            if (exception != null) {
                log.error("异步发送消息失败, topic={}, key={}", topic, key, exception);
            } else if (log.isDebugEnabled()) {
                log.debug("异步发送消息成功, topic={}, key={}, partition={}, offset={}",
                        metadata.topic(), key, metadata.partition(), metadata.offset());
            }
        });
        return true;
    }

    /**
     * 新增主题
     *
     * @param topic       主题名称
     * @param partition   分区数量,默认为1
     * @param replication 副本数量,默认为1
     * @return
     */
    @NaslConnector.Logic
    public Boolean createTopic(String topic, Long partition, Long replication) throws IllegalArgumentException {
        try {
            Set<String> topicNames = getTopicSet();
            if (topicNames.contains(topic)) {
                throw new IllegalArgumentException(MessageFormat.format("主题{0}已存在", topic));
            }
            NewTopic newTopic = new NewTopic(topic, (partition != null && partition.intValue() >= 1) ? partition.intValue() : 1, (replication != null && replication.shortValue() >= 1) ? replication.shortValue() : 1);
            CreateTopicsResult topicsResult = adminClient.createTopics(Collections.singleton(newTopic));
            KafkaFuture<Void> kafkaFuture = topicsResult.all();
            kafkaFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("创建主题失败", e);
            throw new IllegalArgumentException(e);
        }
        return true;
    }

    /**
     * 获取主题信息
     *
     * @param topic 主题名称，可以为空
     * @return
     */
    @NaslConnector.Logic
    public String getTopic(String topic) throws IllegalArgumentException {
        Set<String> requestedTopics = new HashSet<>(Arrays.asList(topic.split(",")));
        try {
            Set<String> topicNames = getTopicSet();
            if (StringUtils.isEmpty(topic)) {
                return topics(topicNames).toString();
            } else {
                //两个集合的交集
                Set<String> intersectionSet = new HashSet<>(topicNames);
                intersectionSet.retainAll(requestedTopics);
                if (!intersectionSet.isEmpty()) {
                    return topics(requestedTopics).toString();
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error("获取主题信息失败", e);
            throw new IllegalArgumentException(e);
        }
        return Collections.emptyList().toString();
    }

    public List<TopicDTO> topics(Set<String> topicNames) throws InterruptedException, ExecutionException {
        // 获取主题详情
        Map<String, TopicDescription> stringTopicDescriptionMap = adminClient.describeTopics(topicNames).all().get();
        // 获取集群详情
        Collection<Node> clusterDetails = adminClient.describeCluster().nodes().get();
        //获取消费者分组
        HashMap<String, Set<String>> topicsByGroupMap = getTopicsByGroupMap(topicNames);

        // 获取集群节点信息
        List<BrokerDTO> brokers = clusterDetails.stream().map(node -> {
            BrokerDTO broker = new BrokerDTO();
            broker.setId((long) node.id());
            broker.setHost(node.host());
            broker.setPort((long) node.port());
            return broker;
        }).collect(Collectors.toList());

        List<TopicDTO> topics = stringTopicDescriptionMap.entrySet().stream()
                .map(entry -> {
                    TopicDTO topic = new TopicDTO();
                    ArrayList<BrokerDTO> brokerDTOS = new ArrayList<>();
                    String topicName = entry.getKey();
                    if (topicsByGroupMap.containsKey(topicName)) {
                        topic.setConsumerGroups(new ArrayList<>(topicsByGroupMap.get(topicName)));
                    }
                    topic.setName(topicName);
                    TopicDescription topicDescription = entry.getValue();
                    List<TopicPartitionInfo> partitions = topicDescription.partitions();
                    topic.setPartitionsCount(partitions.size());
                    topic.setReplicaCount(partitions.isEmpty() ? 0 : partitions.get(0).replicas().size());
                    //添加节点信息
                    partitions.forEach(partition -> {
                        Node leader = partition.leader();
                        BrokerDTO leaderBroker = findBrokerById(brokers, leader);
                        if (leaderBroker != null) {
                            leaderBroker.getLeaderPartitions().add((long) partition.partition());
                            brokerDTOS.add(leaderBroker);
                        }
                        partition.replicas().forEach(replica -> {
                            BrokerDTO replicaBroker = findBrokerById(brokers, replica);
                            if (replicaBroker != null) {
                                replicaBroker.getFollowerPartitions().add((long) partition.partition());
                                if (!brokerDTOS.contains(replicaBroker)) {
                                    brokerDTOS.add(replicaBroker);
                                }
                            }
                        });
                    });
                    topic.setBrokers(brokerDTOS);
                    return topic;
                })
                .collect(Collectors.toList());
        //计算每个topic数据大小
        calculateTotalLogSize(brokers, topics);
        return topics;
    }

    // 计算每个topic数据大小
    public List<TopicDTO> calculateTotalLogSize(List<BrokerDTO> brokers, List<TopicDTO> topics) {
        List<Integer> brokerIds = brokers.stream()
                .filter(broker -> !broker.getFollowerPartitions().isEmpty() || !broker.getLeaderPartitions().isEmpty())
                .map(broker -> Math.toIntExact(broker.getId()))
                .collect(Collectors.toList());
        Map<Integer, Map<String, DescribeLogDirsResponse.LogDirInfo>> integerMapMap = null;
        try {
            integerMapMap = adminClient.describeLogDirs(brokerIds).all().get();
        } catch (InterruptedException | ExecutionException ignored) {
            for (TopicDTO topic : topics) {
                topic.setTotalLogSize(-1L);
            }
        }
        for (TopicDTO topic : topics) {
            long totalLogSize = integerMapMap.values().stream()
                    .flatMap(descriptionMap -> descriptionMap.values().stream())
                    .flatMap(logDirDescription -> logDirDescription.replicaInfos.entrySet().stream())
                    .filter(entry -> entry.getKey().topic().equals(topic.getName()))
                    .mapToLong(entry -> entry.getValue().size)
                    .sum();
            topic.setTotalLogSize(totalLogSize);
        }
        return topics;
    }

    // 获取消费者组
    private HashMap<String, Set<String>> getTopicsByGroupMap(Set<String> topicNames) throws ExecutionException, InterruptedException {
        HashMap<String, Set<String>> resultMap = new HashMap<>();
        // 获取消费者组详情
        Collection<ConsumerGroupListing> consumerGroupListings = adminClient.listConsumerGroups().all().get();
        List<String> groupIds = consumerGroupListings.stream()
                .map(ConsumerGroupListing::groupId)
                .collect(Collectors.toList());
        Map<String, ConsumerGroupDescription> groups = groupIds.isEmpty() ? Collections.emptyMap() : adminClient.describeConsumerGroups(groupIds).all().get();
        for (ConsumerGroupListing consumerGroupListing : consumerGroupListings) {
            String groupId = consumerGroupListing.groupId();
            if (!consumerGroupListing.isSimpleConsumerGroup()) {
                //不依赖于Kafka的Group Coordinator来进行协调和管理的消费者组
                Set<String> topics = groups.get(groupId).members().stream()
                        .map(s -> s.assignment().topicPartitions())
                        .flatMap(Collection::stream)
                        .map(TopicPartition::topic)
                        .collect(Collectors.toSet());
                for (String topic : topics) {
                    resultMap.computeIfAbsent(topic, k -> new HashSet<>()).add(groupId);
                }
            } else {
                Map<TopicPartition, OffsetAndMetadata> topicPartitionOffsetAndMetadataMap = adminClient.listConsumerGroupOffsets(groupId)
                        .partitionsToOffsetAndMetadata()
                        .get();
                for (TopicPartition topicPartition : topicPartitionOffsetAndMetadataMap.keySet()) {
                    String topic = topicPartition.topic();
                    resultMap.computeIfAbsent(topic, k -> new HashSet<>()).add(groupId);
                }
            }
        }
        return resultMap;
    }

    private BrokerDTO findBrokerById(List<BrokerDTO> brokers, Node node) {
        return brokers.stream()
                .filter(broker -> broker.getId() == node.id())
                .findFirst()
                .orElse(null);
    }

    /**
     * 删除主题，里面的消息也会清空
     *
     * @param topic 主题名称
     * @return
     */
    @NaslConnector.Logic
    public Boolean deleteTopic(String topic) throws IllegalArgumentException {
        try {
            Set<String> strings = getTopicSet();
            if (!strings.contains(topic)) {
                throw new IllegalArgumentException(MessageFormat.format("主题{0}不存在", topic));
            }
            DeleteTopicsResult topicsResult = adminClient.deleteTopics(Collections.singleton(topic));
            KafkaFuture<Void> all = topicsResult.all();
            all.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("删除主题失败", e);
            throw new IllegalArgumentException(e);
        }
        return true;
    }

    /**
     * 获取主题名称集合
     */
    public Set<String> getTopicSet() throws ExecutionException, InterruptedException {
        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        listTopicsOptions.timeoutMs(60000);
        listTopicsOptions.listInternal(false);
        // 获取主题列表
        ListTopicsResult topics = adminClient.listTopics(listTopicsOptions);
        return topics.names().get();
    }

    /**
     * 订阅消息
     *
     * @param topic    主题
     * @param group    分组名称
     * @param callback 处理消息的逻辑
     * @return
     */
    @NaslConnector.Trigger
    public Void subscribe(String topic, String group, Function<String, String> callback) {
        Map<String, Object> consumerProps = new HashMap<>();
        Properties properties = buildProperties(bootstrapServers, securityProtocol, saslMechanism, username, password);
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            consumerProps.put(key, properties.get(key));
        }
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, group);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        properties.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        DefaultKafkaConsumerFactory<String, Object> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProps);
        ContainerProperties containerProps = new ContainerProperties(topic);
        KafkaMessageListenerContainer<String, Object> container = new KafkaMessageListenerContainer<>(consumerFactory, containerProps);
        MessageListener<String, String> msg = data -> callback.apply(data.value());
        container.setupMessageListener(msg);
        container.start();
        return null;
    }

    /**
     * 连通性测试
     *
     * @param bootstrapServers 集群地址
     * @param securityProtocol 安全协议(PLAINTEXT、SSL、SASL_PLAINTEXT、SASL_SSL)
     * @param saslMechanism    协议机制(GASAPI、PLAIN、SCRAM-SHA-256、SCRAM-SHA-512)
     * @param username         用户名
     * @param password         密码
     * @return true:成功，false:失败
     */
    @NaslConnector.Tester
    public Boolean testListTopics(String bootstrapServers, String securityProtocol, String saslMechanism, String username, String password) {

        Properties properties = buildProperties(bootstrapServers, securityProtocol, saslMechanism, username, password);
        try (AdminClient client = KafkaAdminClient.create(properties)) {
            ListTopicsResult listTopicsResult = client.listTopics();
            listTopicsResult.names().get();
            return true;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构建kafka配置
     *
     * @param bootstrapServers 接入点地址
     * @param securityProtocol 安全协议(PLAINTEXT、SSL、SASL_PLAINTEXT、SASL_SSL)
     * @param saslMechanism    协议机制(GASAPI、PLAIN、SCRAM-SHA-256、SCRAM-SHA-512)
     * @param username         用户名
     * @param password         密码
     * @return kafka配置
     */
    private Properties buildProperties(String bootstrapServers, String securityProtocol, String saslMechanism, String username, String password) {
        Properties properties = new Properties();
        //设置接入点
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        //每个连接的最大空闲时间
        properties.put(ProducerConfig.CONNECTIONS_MAX_IDLE_MS_CONFIG, 60000);
        //发送消息请求的超时时间
        properties.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 60000);
//        // Kafka broker等待的最大时间。默认情况下，max.block.ms 的值为 60 秒
        properties.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, 15000);
        //Kafka消息的序列化方式
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        //设置客户端内部重试次数
        properties.put(ProducerConfig.RETRIES_CONFIG, 3);
        //设置客户端内部重试间隔
        properties.put(ProducerConfig.RECONNECT_BACKOFF_MS_CONFIG, 90000);
        if (StringUtils.hasText(securityProtocol)) {
            //设置安全协议
            properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, securityProtocol);
        }
        if (StringUtils.hasText(saslMechanism)) {
            //设置sasl机制
            properties.put(SaslConfigs.SASL_MECHANISM, saslMechanism);
        }

        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            //设置sasl认证信息
            properties.put("sasl.jaas.config", MessageFormat.format("org.apache.kafka.common.security.plain.PlainLoginModule required username=\"{0}\" password=\"{1}\";", username, password));
        }
        if (StringUtils.endsWithIgnoreCase(securityProtocol, "SASL_SSL")) {
            //设置SSL根证书的路径，阿里云ssl公共证书地址
            String sslPath = Objects.requireNonNull(KafkaConnector.class.getClassLoader().getResource("mix.4096.client.truststore.jks")).getPath();
            properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, sslPath);
            //根证书store的密码，保持不变
            properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "KafkaOnsClient");
            //hostname校验改成空
            properties.put(SslConfigs.SSL_ENDPOINT_IDENTIFICATION_ALGORITHM_CONFIG, "");
        }

        return properties;
    }

    public void close() {
        if (kafkaProducer != null) {
            kafkaProducer.close();
        }
    }
}
