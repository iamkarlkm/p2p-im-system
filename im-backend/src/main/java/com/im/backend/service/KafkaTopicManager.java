package com.im.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaTopicManager {
    
    private final KafkaAdmin kafkaAdmin;
    
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;
    
    @Value("${spring.kafka.topics.partitions:6}")
    private int defaultPartitions;
    
    @Value("${spring.kafka.topics.replication-factor:1}")
    private short replicationFactor;
    
    private static final Map<String, TopicConfigTemplate> TOPIC_CONFIGS = new HashMap<>();
    
    static {
        TOPIC_CONFIGS.put(KafkaMessageProducer.TOPIC_MESSAGE, 
            new TopicConfigTemplate(12, (short) 3, "message retention high"));
        TOPIC_CONFIGS.put(KafkaMessageProducer.TOPIC_NOTIFICATION, 
            new TopicConfigTemplate(6, (short) 1, "notification retention medium"));
        TOPIC_CONFIGS.put(KafkaMessageProducer.TOPIC_PRESENCE, 
            new TopicConfigTemplate(3, (short) 1, "presence retention low"));
        TOPIC_CONFIGS.put(KafkaMessageProducer.TOPIC_MESSAGE_STATUS, 
            new TopicConfigTemplate(6, (short) 1, "status retention medium"));
        TOPIC_CONFIGS.put(KafkaMessageProducer.TOPIC_CALL, 
            new TopicConfigTemplate(3, (short) 1, "call retention low"));
        TOPIC_CONFIGS.put(KafkaMessageProducer.TOPIC_MEDIA, 
            new TopicConfigTemplate(9, (short) 1, "media retention medium"));
    }
    
    @PostConstruct
    public void initializeTopics() {
        log.info("Initializing Kafka topics...");
        try {
            createTopicsIfNotExist();
            log.info("Kafka topics initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize Kafka topics: {}", e.getMessage());
        }
    }
    
    public void createTopicsIfNotExist() throws ExecutionException, InterruptedException {
        AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
        
        List<String> topicNames = new ArrayList<>(TOPIC_CONFIGS.keySet());
        ListTopicsResult listTopics = adminClient.listTopics();
        Set<String> existingTopics = listTopics.names().get();
        
        List<String> topicsToCreate = topicNames.stream()
                .filter(t -> !existingTopics.contains(t))
                .toList();
        
        if (!topicsToCreate.isEmpty()) {
            Map<String, TopicSpecification> topicSpecs = new HashMap<>();
            for (String topicName : topicsToCreate) {
                TopicConfigTemplate config = TOPIC_CONFIGS.get(topicName);
                topicSpecs.put(topicName, new TopicSpecification()
                        .withNumPartitions(config.partitions)
                        .withReplicationFactor(config.replicationFactor)
                        .withConfigs(config.getConfigs()));
            }
            
            CreateTopicsResult result = adminClient.createTopics(topicSpecs);
            result.all().get();
            log.info("Created {} new topics: {}", topicsToCreate.size(), topicsToCreate);
        } else {
            log.info("All topics already exist");
        }
        
        adminClient.close();
    }
    
    public void createTopic(String topicName, int partitions, short replicationFactor, Map<String, String> configs) {
        try {
            AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
            
            NewTopic topic = new NewTopic(topicName, partitions, replicationFactor);
            if (configs != null && !configs.isEmpty()) {
                topic.configs(configs);
            }
            
            CreateTopicsResult result = adminClient.createTopics(Collections.singleton(topic));
            result.all().get();
            
            log.info("Created topic: {}", topicName);
            adminClient.close();
        } catch (Exception e) {
            log.error("Failed to create topic {}: {}", topicName, e.getMessage());
            throw new RuntimeException("Failed to create topic", e);
        }
    }
    
    public void deleteTopic(String topicName) {
        try {
            AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
            DeleteTopicsResult result = adminClient.deleteTopics(Collections.singleton(topicName));
            result.all().get();
            
            log.info("Deleted topic: {}", topicName);
            adminClient.close();
        } catch (Exception e) {
            log.error("Failed to delete topic {}: {}", topicName, e.getMessage());
            throw new RuntimeException("Failed to delete topic", e);
        }
    }
    
    public Map<String, TopicDescription> describeTopics(List<String> topicNames) {
        try {
            AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
            DescribeTopicsResult result = adminClient.describeTopics(topicNames);
            Map<String, TopicDescription> descriptions = result.allTopicNames().get();
            adminClient.close();
            return descriptions;
        } catch (Exception e) {
            log.error("Failed to describe topics: {}", e.getMessage());
            throw new RuntimeException("Failed to describe topics", e);
        }
    }
    
    public Map<String, TopicDescription> listAllTopics() {
        try {
            AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
            ListTopicsResult result = adminClient.listTopics();
            Set<String> topicNames = result.names().get();
            
            if (topicNames.isEmpty()) {
                return Collections.emptyMap();
            }
            
            DescribeTopicsResult describeResult = adminClient.describeTopics(new ArrayList<>(topicNames));
            Map<String, TopicDescription> descriptions = describeResult.allTopicNames().get();
            adminClient.close();
            return descriptions;
        } catch (Exception e) {
            log.error("Failed to list topics: {}", e.getMessage());
            throw new RuntimeException("Failed to list topics", e);
        }
    }
    
    public void updateTopicConfig(String topicName, Map<String, String> configs) {
        try {
            AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
            
            Map<ConfigResource, Config> configUpdates = new HashMap<>();
            ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, topicName);
            Config config = new Config(configs.entrySet().stream()
                    .map(e -> new ConfigEntry(e.getKey(), e.getValue()))
                    .toList());
            configUpdates.put(resource, config);
            
            AlterConfigsResult result = adminClient.alterConfigs(configUpdates);
            result.all().get();
            
            log.info("Updated config for topic: {}", topicName);
            adminClient.close();
        } catch (Exception e) {
            log.error("Failed to update topic config {}: {}", topicName, e.getMessage());
            throw new RuntimeException("Failed to update topic config", e);
        }
    }
    
    public void addPartitions(String topicName, int additionalPartitions) {
        try {
            AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
            
            Map<String, NewPartitions> partitionsMap = new HashMap<>();
            partitionsMap.put(topicName, NewPartitions.increaseTo(
                    getTopicPartitionCount(topicName) + additionalPartitions));
            
            CreatePartitionsResult result = adminClient.createPartitions(partitionsMap);
            result.all().get();
            
            log.info("Added {} partitions to topic: {}", additionalPartitions, topicName);
            adminClient.close();
        } catch (Exception e) {
            log.error("Failed to add partitions to topic {}: {}", topicName, e.getMessage());
            throw new RuntimeException("Failed to add partitions", e);
        }
    }
    
    public int getTopicPartitionCount(String topicName) {
        try {
            AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
            DescribeTopicsResult result = adminClient.describeTopics(Collections.singleton(topicName));
            TopicDescription description = result.allTopicNames().get().get(topicName);
            adminClient.close();
            return description.partitions().size();
        } catch (Exception e) {
            log.error("Failed to get partition count for topic {}: {}", topicName, e.getMessage());
            return defaultPartitions;
        }
    }
    
    private static class TopicConfigTemplate {
        final int partitions;
        final short replicationFactor;
        final String description;
        
        TopicConfigTemplate(int partitions, short replicationFactor, String description) {
            this.partitions = partitions;
            this.replicationFactor = replicationFactor;
            this.description = description;
        }
        
        Map<String, String> getConfigs() {
            Map<String, String> configs = new HashMap<>();
            configs.put(TopicConfig.RETENTION_MS_CONFIG, getRetentionMs());
            configs.put(TopicConfig.CLEANUP_POLICY_CONFIG, TopicConfig.CLEANUP_POLICY_DELETE);
            configs.put(TopicConfig.MIN_IN_SYNC_REPLICAS_CONFIG, String.valueOf(replicationFactor));
            return configs;
        }
        
        String getRetentionMs() {
            if (description.contains("high")) return String.valueOf(7 * 24 * 3600 * 1000L);
            if (description.contains("medium")) return String.valueOf(3 * 24 * 3600 * 1000L);
            return String.valueOf(24 * 3600 * 1000L);
        }
    }
}
