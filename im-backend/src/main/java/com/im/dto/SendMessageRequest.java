package com.im.dto;

import java.util.Map;

/**
 * 发送消息请求DTO
 * 功能 #1: 消息队列核心系统 - 消息发送
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class SendMessageRequest {
    
    // ==================== 核心字段 ====================
    private String queueName;
    private String topic;
    private String messageType;
    private String payload;
    private Map<String, String> headers;
    private Long priority;
    private Long ttl;
    private String producerId;
    private Boolean persistent;
    private Integer maxRetryCount;
    
    // ==================== 构造函数 ====================
    public SendMessageRequest() {}
    
    public SendMessageRequest(String queueName, String payload) {
        this.queueName = queueName;
        this.payload = payload;
    }
    
    // ==================== Builder模式 ====================
    public static Builder builder() {
        return new Builder();
    }
    
    public static class Builder {
        private SendMessageRequest request = new SendMessageRequest();
        
        public Builder queueName(String queueName) {
            request.queueName = queueName;
            return this;
        }
        
        public Builder topic(String topic) {
            request.topic = topic;
            return this;
        }
        
        public Builder messageType(String messageType) {
            request.messageType = messageType;
            return this;
        }
        
        public Builder payload(String payload) {
            request.payload = payload;
            return this;
        }
        
        public Builder headers(Map<String, String> headers) {
            request.headers = headers;
            return this;
        }
        
        public Builder priority(Long priority) {
            request.priority = priority;
            return this;
        }
        
        public Builder ttl(Long ttl) {
            request.ttl = ttl;
            return this;
        }
        
        public Builder producerId(String producerId) {
            request.producerId = producerId;
            return this;
        }
        
        public Builder persistent(Boolean persistent) {
            request.persistent = persistent;
            return this;
        }
        
        public Builder maxRetryCount(Integer maxRetryCount) {
            request.maxRetryCount = maxRetryCount;
            return this;
        }
        
        public SendMessageRequest build() {
            return request;
        }
    }
    
    // ==================== Getter & Setter ====================
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
    
    public Long getPriority() { return priority; }
    public void setPriority(Long priority) { this.priority = priority; }
    
    public Long getTtl() { return ttl; }
    public void setTtl(Long ttl) { this.ttl = ttl; }
    
    public String getProducerId() { return producerId; }
    public void setProducerId(String producerId) { this.producerId = producerId; }
    
    public Boolean getPersistent() { return persistent; }
    public void setPersistent(Boolean persistent) { this.persistent = persistent; }
    
    public Integer getMaxRetryCount() { return maxRetryCount; }
    public void setMaxRetryCount(Integer maxRetryCount) { this.maxRetryCount = maxRetryCount; }
    
    @Override
    public String toString() {
        return "SendMessageRequest{" +
                "queueName='" + queueName + '\'' +
                ", topic='" + topic + '\'' +
                ", messageType='" + messageType + '\'' +
                '}';
    }
}
