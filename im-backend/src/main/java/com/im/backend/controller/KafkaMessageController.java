package com.im.backend.controller;

import com.im.backend.service.KafkaMessageProducer;
import com.im.backend.service.KafkaTopicManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/kafka")
@RequiredArgsConstructor
public class KafkaMessageController {
    
    private final KafkaMessageProducer kafkaMessageProducer;
    private final KafkaTopicManager kafkaTopicManager;
    
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody Map<String, Object> messageRequest) {
        String topic = (String) messageRequest.get("topic");
        String key = (String) messageRequest.get("key");
        Object message = messageRequest.get("message");
        
        if (topic == null || message == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Topic and message are required"));
        }
        
        kafkaMessageProducer.sendMessage(topic, key, message);
        
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "topic", topic,
                "key", key != null ? key : "null"
        ));
    }
    
    @GetMapping("/topics")
    public ResponseEntity<Map<String, Object>> listTopics() {
        try {
            var topics = kafkaTopicManager.listAllTopics();
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "topics", topics.keySet()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    
    @PostMapping("/topics/create")
    public ResponseEntity<Map<String, Object>> createTopic(@RequestBody Map<String, Object> request) {
        String topicName = (String) request.get("topicName");
        Integer partitions = (Integer) request.get("partitions");
        Short replicationFactor = request.get("replicationFactor") != null 
                ? ((Number) request.get("replicationFactor")).shortValue() 
                : (short) 1;
        
        if (topicName == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Topic name is required"));
        }
        
        try {
            kafkaTopicManager.createTopic(topicName, 
                    partitions != null ? partitions : 6, 
                    replicationFactor, 
                    null);
            
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "topicName", topicName
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    
    @DeleteMapping("/topics/{topicName}")
    public ResponseEntity<Map<String, Object>> deleteTopic(@PathVariable String topicName) {
        try {
            kafkaTopicManager.deleteTopic(topicName);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "topicName", topicName
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "kafka-producer"
        ));
    }
}
