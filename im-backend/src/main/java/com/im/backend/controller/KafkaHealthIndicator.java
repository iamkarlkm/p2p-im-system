package com.im.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
public class KafkaHealthIndicator implements HealthIndicator {
    
    @Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;
    
    @Override
    public Health health() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, 5000);
        
        try (AdminClient adminClient = AdminClient.create(props)) {
            DescribeTopicsResult result = adminClient.describeTopics(
                    Collections.singletonList("im-messages")
            );
            
            try {
                result.allTopicNames().get();
                return Health.up()
                        .withDetail("kafka", "active")
                        .withDetail("bootstrap_servers", bootstrapServers)
                        .withDetail("connection", "success")
                        .build();
            } catch (ExecutionException e) {
                if (e.getCause() instanceof TimeoutException) {
                    return Health.down()
                            .withDetail("kafka", "unreachable")
                            .withDetail("error", "Connection timeout")
                            .withDetail("bootstrap_servers", bootstrapServers)
                            .build();
                }
                return Health.down()
                        .withDetail("kafka", "error")
                        .withDetail("error", e.getMessage())
                        .build();
            }
        } catch (Exception e) {
            log.error("Kafka health check failed", e);
            return Health.down(e)
                    .withDetail("kafka", "error")
                    .withDetail("bootstrap_servers", bootstrapServers)
                    .build();
        }
    }
}
