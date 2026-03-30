package com.im.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 队列消息实体类
 * 功能 #1: 消息队列核心系统 - 消息队列引擎
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class QueueMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ==================== 消息状态枚举 ====================
    public enum MessageStatus {
        PENDING("待处理"),
        PROCESSING("处理中"),
        SUCCESS("成功"),
        FAILED("失败"),
        DEAD_LETTER("死信"),
        RETRYING("重试中");
        
        private final String description;
        
        MessageStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // ==================== 核心字段 ====================
    private String messageId;
    private String queueName;
    private String topic;
    private String messageType;
    private String payload;
    private Map<String, String> headers;
    private MessageStatus status;
    private Integer retryCount;
    private Integer maxRetryCount;
    private String producerId;
    private String consumerId;
    private LocalDateTime createTime;
    private LocalDateTime processTime;
    private LocalDateTime completeTime;
    private LocalDateTime nextRetryTime;
    private String errorMessage;
    private String traceId;
    private Long priority;
    private Long ttl;
    private Boolean persistent;
    
    // ==================== 构造函数 ====================
    public QueueMessage() {
        this.messageId = UUID.randomUUID().toString();
        this.headers = new HashMap<>();
        this.status = MessageStatus.PENDING;
        this.retryCount = 0;
        this.maxRetryCount = 3;
        this.createTime = LocalDateTime.now();
        this.priority = 0L;
        this.persistent = true;
        this.traceId = UUID.randomUUID().toString();
    }
    
    public QueueMessage(String queueName, String payload) {
        this();
        this.queueName = queueName;
        this.payload = payload;
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 增加重试次数
     */
    public void incrementRetry() {
        this.retryCount++;
        this.status = MessageStatus.RETRYING;
        this.nextRetryTime = LocalDateTime.now().plusSeconds(calculateRetryDelay());
    }
    
    /**
     * 计算重试延迟（指数退避）
     */
    private long calculateRetryDelay() {
        return (long) Math.pow(2, retryCount) * 1000;
    }
    
    /**
     * 标记为成功
     */
    public void markSuccess() {
        this.status = MessageStatus.SUCCESS;
        this.completeTime = LocalDateTime.now();
    }
    
    /**
     * 标记为失败
     */
    public void markFailed(String error) {
        this.status = MessageStatus.FAILED;
        this.errorMessage = error;
        this.completeTime = LocalDateTime.now();
    }
    
    /**
     * 标记为死信
     */
    public void markDeadLetter(String reason) {
        this.status = MessageStatus.DEAD_LETTER;
        this.errorMessage = reason;
        this.completeTime = LocalDateTime.now();
    }
    
    /**
     * 是否需要重试
     */
    public boolean shouldRetry() {
        return retryCount < maxRetryCount && status != MessageStatus.DEAD_LETTER;
    }
    
    /**
     * 添加消息头
     */
    public void addHeader(String key, String value) {
        this.headers.put(key, value);
    }
    
    /**
     * 获取消息头
     */
    public String getHeader(String key) {
        return this.headers.get(key);
    }
    
    // ==================== Getter & Setter ====================
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
    
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    
    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Integer getMaxRetryCount() { return maxRetryCount; }
    public void setMaxRetryCount(Integer maxRetryCount) { this.maxRetryCount = maxRetryCount; }
    
    public String getProducerId() { return producerId; }
    public void setProducerId(String producerId) { this.producerId = producerId; }
    
    public String getConsumerId() { return consumerId; }
    public void setConsumerId(String consumerId) { this.consumerId = consumerId; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getProcessTime() { return processTime; }
    public void setProcessTime(LocalDateTime processTime) { this.processTime = processTime; }
    
    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }
    
    public LocalDateTime getNextRetryTime() { return nextRetryTime; }
    public void setNextRetryTime(LocalDateTime nextRetryTime) { this.nextRetryTime = nextRetryTime; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    
    public Long getPriority() { return priority; }
    public void setPriority(Long priority) { this.priority = priority; }
    
    public Long getTtl() { return ttl; }
    public void setTtl(Long ttl) { this.ttl = ttl; }
    
    public Boolean getPersistent() { return persistent; }
    public void setPersistent(Boolean persistent) { this.persistent = persistent; }
    
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
