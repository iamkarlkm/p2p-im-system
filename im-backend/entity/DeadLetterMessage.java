package com.im.entity;

import java.time.Instant;
import java.util.UUID;

/**
 * 死信消息实体类
 * 功能 #1: 消息队列核心系统 - 死信队列
 */
public class DeadLetterMessage {
    
    private String deadLetterId;
    private String originalMessageId;
    private String queueName;
    private String exchangeName;
    private String routingKey;
    private byte[] payload;
    private String reason;
    private String exceptionType;
    private String exceptionMessage;
    private int originalRetryCount;
    private Instant originalCreateTime;
    private Instant deadLetterTime;
    private DeadLetterStatus status;
    private String handlerId;
    private Instant handleTime;
    private String handleResult;
    private String retryQueueName;
    
    public enum DeadLetterStatus {
        PENDING, REQUEUED, ARCHIVED, DISCARDED, HANDLED
    }
    
    public DeadLetterMessage() {
        this.deadLetterId = UUID.randomUUID().toString();
        this.deadLetterTime = Instant.now();
        this.status = DeadLetterStatus.PENDING;
    }
    
    // Getters and Setters
    public String getDeadLetterId() { return deadLetterId; }
    public void setDeadLetterId(String deadLetterId) { this.deadLetterId = deadLetterId; }
    
    public String getOriginalMessageId() { return originalMessageId; }
    public void setOriginalMessageId(String originalMessageId) { this.originalMessageId = originalMessageId; }
    
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    
    public String getExchangeName() { return exchangeName; }
    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }
    
    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
    
    public byte[] getPayload() { return payload; }
    public void setPayload(byte[] payload) { this.payload = payload; }
    
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    
    public String getExceptionType() { return exceptionType; }
    public void setExceptionType(String exceptionType) { this.exceptionType = exceptionType; }
    
    public String getExceptionMessage() { return exceptionMessage; }
    public void setExceptionMessage(String exceptionMessage) { this.exceptionMessage = exceptionMessage; }
    
    public int getOriginalRetryCount() { return originalRetryCount; }
    public void setOriginalRetryCount(int originalRetryCount) { this.originalRetryCount = originalRetryCount; }
    
    public Instant getOriginalCreateTime() { return originalCreateTime; }
    public void setOriginalCreateTime(Instant originalCreateTime) { this.originalCreateTime = originalCreateTime; }
    
    public Instant getDeadLetterTime() { return deadLetterTime; }
    public void setDeadLetterTime(Instant deadLetterTime) { this.deadLetterTime = deadLetterTime; }
    
    public DeadLetterStatus getStatus() { return status; }
    public void setStatus(DeadLetterStatus status) { this.status = status; }
    
    public String getHandlerId() { return handlerId; }
    public void setHandlerId(String handlerId) { this.handlerId = handlerId; }
    
    public Instant getHandleTime() { return handleTime; }
    public void setHandleTime(Instant handleTime) { this.handleTime = handleTime; }
    
    public String getHandleResult() { return handleResult; }
    public void setHandleResult(String handleResult) { this.handleResult = handleResult; }
    
    public String getRetryQueueName() { return retryQueueName; }
    public void setRetryQueueName(String retryQueueName) { this.retryQueueName = retryQueueName; }
    
    @Override
    public String toString() {
        return "DeadLetterMessage{" +
                "deadLetterId='" + deadLetterId + '\'' +
                ", originalMessageId='" + originalMessageId + '\'' +
                ", queueName='" + queueName + '\'' +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                '}';
    }
}
