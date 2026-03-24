package com.im.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaMessageProducer {
    
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTemplate<String, String> stringKafkaTemplate;
    
    public static final String TOPIC_MESSAGE = "im-messages";
    public static final String TOPIC_NOTIFICATION = "im-notifications";
    public static final String TOPIC_PRESENCE = "im-presence";
    public static final String TOPIC_MESSAGE_STATUS = "im-message-status";
    public static final String TOPIC_CALL = "im-calls";
    public static final String TOPIC_MEDIA = "im-media";
    
    public CompletableFuture<SendResult<String, Object>> sendMessage(String topic, String key, Object message) {
        log.debug("Sending message to topic: {}, key: {}", topic, key);
        return kafkaTemplate.send(topic, key, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send message to topic {}: {}", topic, ex.getMessage());
                    } else {
                        log.debug("Message sent successfully to topic {} partition {} offset {}",
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
    
    public CompletableFuture<SendResult<String, String>> sendStringMessage(String topic, String key, String message) {
        return stringKafkaTemplate.send(topic, key, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send string message to topic {}: {}", topic, ex.getMessage());
                    } else {
                        log.debug("String message sent to topic {} partition {} offset {}",
                                topic,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    }
                });
    }
    
    public void sendMessageSync(String topic, String key, Object message) {
        try {
            SendResult<String, Object> result = kafkaTemplate.send(topic, key, message).get();
            log.info("Message sent synchronously to topic {} partition {} offset {}",
                    topic,
                    result.getRecordMetadata().partition(),
                    result.getRecordMetadata().offset());
        } catch (Exception e) {
            log.error("Failed to send message synchronously to topic {}: {}", topic, e.getMessage());
            throw new RuntimeException("Failed to send message", e);
        }
    }
    
    public void sendMessageAsync(String topic, String key, Object message, KafkaSendCallback callback) {
        kafkaTemplate.send(topic, key, message)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        callback.onFailure(ex);
                    } else {
                        callback.onSuccess(result);
                    }
                });
    }
    
    public interface KafkaSendCallback {
        void onSuccess(SendResult<String, Object> result);
        void onFailure(Exception ex);
    }
    
    public CompletableFuture<SendResult<String, Object>> sendChatMessage(Long senderId, Long conversationId, Object message) {
        String key = conversationId + "-" + senderId;
        return sendMessage(TOPIC_MESSAGE, key, message);
    }
    
    public CompletableFuture<SendResult<String, Object>> sendNotification(Long userId, Object notification) {
        String key = String.valueOf(userId);
        return sendMessage(TOPIC_NOTIFICATION, key, notification);
    }
    
    public CompletableFuture<SendResult<String, Object>> sendPresenceUpdate(Long userId, Object presenceUpdate) {
        String key = String.valueOf(userId);
        return sendMessage(TOPIC_PRESENCE, key, presenceUpdate);
    }
    
    public CompletableFuture<SendResult<String, Object>> sendMessageStatusUpdate(Long messageId, Object statusUpdate) {
        String key = String.valueOf(messageId);
        return sendMessage(TOPIC_MESSAGE_STATUS, key, statusUpdate);
    }
    
    public CompletableFuture<SendResult<String, Object>> sendCallEvent(Long callId, Object callEvent) {
        String key = String.valueOf(callId);
        return sendMessage(TOPIC_CALL, key, callEvent);
    }
}
