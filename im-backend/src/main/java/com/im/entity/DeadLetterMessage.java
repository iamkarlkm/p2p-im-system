package com.im.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 死信消息实体类
 * 功能 #1: 消息队列核心系统 - 死信队列
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class DeadLetterMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ==================== 死信原因枚举 ====================
    public enum DeadLetterReason {
        RETRY_EXHAUSTED("重试次数耗尽"),
        INVALID_MESSAGE("消息格式无效"),
        PROCESSING_ERROR("处理异常"),
        EXPIRED("消息过期"),
        ROUTING_FAILED("路由失败"),
        REJECTED("被拒绝");
        
        private final String description;
        
        DeadLetterReason(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // ==================== 核心字段 ====================
    private String deadLetterId;
    private String originalMessageId;
    private String queueName;
    private String topic;
    private String payload;
    private DeadLetterReason reason;
    private String errorDetail;
    private Integer originalRetryCount;
    private String originalErrorMessage;
    private LocalDateTime deadLetterTime;
    private LocalDateTime originalCreateTime;
    private String producerId;
    private String traceId;
    private Boolean retried;
    private LocalDateTime retryTime;
    private Boolean resolved;
    private LocalDateTime resolveTime;
    
    // ==================== 构造函数 ====================
    public DeadLetterMessage() {
        this.deadLetterTime = LocalDateTime.now();
        this.retried = false;
        this.resolved = false;
    }
    
    public DeadLetterMessage(QueueMessage originalMessage, DeadLetterReason reason) {
        this();
        this.originalMessageId = originalMessage.getMessageId();
        this.queueName = originalMessage.getQueueName();
        this.topic = originalMessage.getTopic();
        this.payload = originalMessage.getPayload();
        this.reason = reason;
        this.originalRetryCount = originalMessage.getRetryCount();
        this.originalErrorMessage = originalMessage.getErrorMessage();
        this.originalCreateTime = originalMessage.getCreateTime();
        this.producerId = originalMessage.getProducerId();
        this.traceId = originalMessage.getTraceId();
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 标记为重试
     */
    public void markRetried() {
        this.retried = true;
        this.retryTime = LocalDateTime.now();
    }
    
    /**
     * 标记为已解决
     */
    public void markResolved() {
        this.resolved = true;
        this.resolveTime = LocalDateTime.now();
    }
    
    /**
     * 转换为原始消息
     */
    public QueueMessage toOriginalMessage() {
        QueueMessage message = new QueueMessage();
        message.setMessageId(this.originalMessageId);
        message.setQueueName(this.queueName);
        message.setTopic(this.topic);
        message.setPayload(this.payload);
        message.setProducerId(this.producerId);
        message.setTraceId(this.traceId);
        message.setCreateTime(this.originalCreateTime);
        return message;
    }
    
    // ==================== Getter & Setter ====================
    public String getDeadLetterId() { return deadLetterId; }
    public void setDeadLetterId(String deadLetterId) { this.deadLetterId = deadLetterId; }
    
    public String getOriginalMessageId() { return originalMessageId; }
    public void setOriginalMessageId(String originalMessageId) { this.originalMessageId = originalMessageId; }
    
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public DeadLetterReason getReason() { return reason; }
    public void setReason(DeadLetterReason reason) { this.reason = reason; }
    
    public String getErrorDetail() { return errorDetail; }
    public void setErrorDetail(String errorDetail) { this.errorDetail = errorDetail; }
    
    public Integer getOriginalRetryCount() { return originalRetryCount; }
    public void setOriginalRetryCount(Integer originalRetryCount) { this.originalRetryCount = originalRetryCount; }
    
    public String getOriginalErrorMessage() { return originalErrorMessage; }
    public void setOriginalErrorMessage(String originalErrorMessage) { this.originalErrorMessage = originalErrorMessage; }
    
    public LocalDateTime getDeadLetterTime() { return deadLetterTime; }
    public void setDeadLetterTime(LocalDateTime deadLetterTime) { this.deadLetterTime = deadLetterTime; }
    
    public LocalDateTime getOriginalCreateTime() { return originalCreateTime; }
    public void setOriginalCreateTime(LocalDateTime originalCreateTime) { this.originalCreateTime = originalCreateTime; }
    
    public String getProducerId() { return producerId; }
    public void setProducerId(String producerId) { this.producerId = producerId; }
    
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    
    public Boolean getRetried() { return retried; }
    public void setRetried(Boolean retried) { this.retried = retried; }
    
    public LocalDateTime getRetryTime() { return retryTime; }
    public void setRetryTime(LocalDateTime retryTime) { this.retryTime = retryTime; }
    
    public Boolean getResolved() { return resolved; }
    public void setResolved(Boolean resolved) { this.resolved = resolved; }
    
    public LocalDateTime getResolveTime() { return resolveTime; }
    public void setResolveTime(LocalDateTime resolveTime) { this.resolveTime = resolveTime; }
    
    @Override
    public String toString() {
        return "DeadLetterMessage{" +
                "deadLetterId='" + deadLetterId + '\'' +
                ", originalMessageId='" + originalMessageId + '\'' +
                ", reason=" + reason +
                ", deadLetterTime=" + deadLetterTime +
                '}';
    }
}
