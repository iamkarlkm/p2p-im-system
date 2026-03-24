package com.im.backend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import com.im.backend.service.KafkaMessageProducer;

@Configuration
public class KafkaAdminConfig {
    
    @Bean
    public NewTopic messageTopic() {
        return TopicBuilder.name(KafkaMessageProducer.TOPIC_MESSAGE)
                .partitions(12)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic notificationTopic() {
        return TopicBuilder.name(KafkaMessageProducer.TOPIC_NOTIFICATION)
                .partitions(6)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic presenceTopic() {
        return TopicBuilder.name(KafkaMessageProducer.TOPIC_PRESENCE)
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic messageStatusTopic() {
        return TopicBuilder.name(KafkaMessageProducer.TOPIC_MESSAGE_STATUS)
                .partitions(6)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic callTopic() {
        return TopicBuilder.name(KafkaMessageProducer.TOPIC_CALL)
                .partitions(3)
                .replicas(1)
                .build();
    }
    
    @Bean
    public NewTopic mediaTopic() {
        return TopicBuilder.name(KafkaMessageProducer.TOPIC_MEDIA)
                .partitions(9)
                .replicas(1)
                .build();
    }
}
