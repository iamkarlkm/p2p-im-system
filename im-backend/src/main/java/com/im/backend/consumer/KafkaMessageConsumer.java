package com.im.backend.consumer;

import com.im.backend.service.KafkaMessageProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageConsumer {
    
    @KafkaListener(topics = KafkaMessageProducer.TOPIC_MESSAGE, groupId = "message-processor")
    public void consumeMessage(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        log.debug("Received message from topic: {}, partition: {}, offset: {}",
                record.topic(), record.partition(), record.offset());
        try {
            processMessage(record);
            ack.acknowledge();
            log.debug("Message processed and acknowledged");
        } catch (Exception e) {
            log.error("Failed to process message: {}", e.getMessage(), e);
            handleMessageFailure(record, e);
        }
    }
    
    @KafkaListener(topics = KafkaMessageProducer.TOPIC_NOTIFICATION, groupId = "notification-processor")
    public void consumeNotification(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        log.debug("Received notification from topic: {}", record.topic());
        try {
            processNotification(record);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process notification: {}", e.getMessage(), e);
            handleMessageFailure(record, e);
        }
    }
    
    @KafkaListener(topics = KafkaMessageProducer.TOPIC_PRESENCE, groupId = "presence-processor")
    public void consumePresence(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        log.debug("Received presence update from topic: {}", record.topic());
        try {
            processPresenceUpdate(record);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process presence update: {}", e.getMessage(), e);
            handleMessageFailure(record, e);
        }
    }
    
    @KafkaListener(topics = KafkaMessageProducer.TOPIC_MESSAGE_STATUS, groupId = "status-processor")
    public void consumeMessageStatus(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        log.debug("Received message status update from topic: {}", record.topic());
        try {
            processMessageStatus(record);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process message status: {}", e.getMessage(), e);
            handleMessageFailure(record, e);
        }
    }
    
    @KafkaListener(topics = KafkaMessageProducer.TOPIC_CALL, groupId = "call-processor")
    public void consumeCallEvent(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        log.debug("Received call event from topic: {}", record.topic());
        try {
            processCallEvent(record);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process call event: {}", e.getMessage(), e);
            handleMessageFailure(record, e);
        }
    }
    
    @KafkaListener(topics = KafkaMessageProducer.TOPIC_MEDIA, groupId = "media-processor")
    public void consumeMedia(ConsumerRecord<String, Object> record, Acknowledgment ack) {
        log.debug("Received media event from topic: {}", record.topic());
        try {
            processMediaEvent(record);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Failed to process media event: {}", e.getMessage(), e);
            handleMessageFailure(record, e);
        }
    }
    
    @KafkaListener(topics = KafkaMessageProducer.TOPIC_MESSAGE, groupId = "message-indexer", containerFactory = "batchKafkaListenerContainerFactory")
    public void consumeMessageBatch(List<ConsumerRecord<String, Object>> records, Acknowledgment ack) {
        log.debug("Received batch of {} messages", records.size());
        try {
            for (ConsumerRecord<String, Object> record : records) {
                processMessage(record);
            }
            ack.acknowledge();
            log.debug("Batch of {} messages processed and acknowledged", records.size());
        } catch (Exception e) {
            log.error("Failed to process message batch: {}", e.getMessage(), e);
            handleBatchFailure(records, e);
        }
    }
    
    private void processMessage(ConsumerRecord<String, Object> record) {
        log.info("Processing message: key={}, topic={}, partition={}, offset={}",
                record.key(), record.topic(), record.partition(), record.offset());
    }
    
    private void processNotification(ConsumerRecord<String, Object> record) {
        log.info("Processing notification: key={}", record.key());
    }
    
    private void processPresenceUpdate(ConsumerRecord<String, Object> record) {
        log.info("Processing presence update: key={}", record.key());
    }
    
    private void processMessageStatus(ConsumerRecord<String, Object> record) {
        log.info("Processing message status: key={}", record.key());
    }
    
    private void processCallEvent(ConsumerRecord<String, Object> record) {
        log.info("Processing call event: key={}", record.key());
    }
    
    private void processMediaEvent(ConsumerRecord<String, Object> record) {
        log.info("Processing media event: key={}", record.key());
    }
    
    private void handleMessageFailure(ConsumerRecord<String, Object> record, Exception e) {
        log.error("Message processing failed: topic={}, partition={}, offset={}, key={}, error={}",
                record.topic(), record.partition(), record.offset(), record.key(), e.getMessage());
    }
    
    private void handleBatchFailure(List<ConsumerRecord<String, Object>> records, Exception e) {
        log.error("Batch processing failed: size={}, error={}", records.size(), e.getMessage());
    }
}
