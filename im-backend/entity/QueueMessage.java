package com.im.entity;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * 队列消息实体类
 * 功能 #1: 消息队列核心系统
 */
public class QueueMessage {
    
    private String messageId;
    private String queueName;
    private String messageType;
    private byte[] payload;
    private Map<String, String> headers;
    private int priority;
    private Instant createTime;
    private Instant expireTime;
    private int retryCount;
    private int maxRetryCount;
    private MessageStatus status;
    private String producerId;
    private String consumerId;
    private Instant consumeTime;
    private String failureReason;
    private long deliveryTag;
    private boolean persistent;
    
    public enum MessageStatus {
        PENDING, DELIVERING, DELIVERED, FAILED, DEAD_LETTER, EXPIRED
    }
    
    public QueueMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.createTime = Instant.now();
        this.status = MessageStatus.PENDING;
        this.retryCount = 0;
        this.priority = 5;
        this.persistent = true;
    }
    
    public QueueMessage(String queueName, byte[] payload) {
        this();
        this.queueName = queueName;
        this.payload = payload;
    }
    
    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public byte[] getPayload() { return payload; }
    public void setPayload(byte[] payload) { this.payload = payload; }
    
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public Instant getCreateTime() { return createTime; }
    public void setCreateTime(Instant createTime) { this.createTime = createTime; }
    
    public Instant getExpireTime() { return expireTime; }
    public void setExpireTime(Instant expireTime) { this.expireTime = expireTime; }
    
    public int getRetryCount() { return retryCount; }
    public void setRetryCount(int retryCount) { this.retryCount = retryCount; }
    
    public int getMaxRetryCount() { return maxRetryCount; }
    public void setMaxRetryCount(int maxRetryCount) { this.maxRetryCount = maxRetryCount; }
    
    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }
    
    public String getProducerId() { return producerId; }
    public void setProducerId(String producerId) { this.producerId = producerId; }
    
    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }
    
    public Instant getConsumeTime() { return consumeTime; }
    public void setConsumeTime(Instant consumeTime) { this.consumeTime = consumeTime; }
    
    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }
    
    public long getDeliveryTag() { return deliveryTag; }
    public void setDeliveryTag(long deliveryTag) { this.deliveryTag = deliveryTag; }
    
    public boolean isPersistent() { return persistent; }
    public void setPersistent(boolean persistent) { this.persistent = persistent; }
    
    /**
     * 增加重试计数
     */
    public void incrementRetry() {
        this.retryCount++;
    }
    
    /**
     * 检查是否超过最大重试次数
     */
    public boolean isRetryExceeded() {
        return maxRetryCount > 0 && retryCount >= maxRetryCount;
    }
    
    /**
     * 检查消息是否已过期
     */
    public boolean isExpired() {
        return expireTime != null && Instant.now().isAfter(expireTime);
    }
    
    @Override
    public String toString() {
        return "QueueMessage{" +
                "messageId='" + messageId + '\'' +
                ", queueName='" + queueName + '\'' +
                ", status=" + status +
                ", retryCount=" + retryCount +
                ", createTime=" + createTime +
                '}';
    }
}
