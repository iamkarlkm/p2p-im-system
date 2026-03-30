package com.im.dto;

import java.util.Map;

/**
 * 发送消息请求DTO
 * 功能 #1: 消息队列核心系统
 */
public class SendMessageRequest {
    
    private String queueName;
    private String exchangeName;
    private String routingKey;
    private String messageType;
    private byte[] payload;
    private Map<String, String> headers;
    private int priority;
    private Long ttl;
    private boolean persistent;
    private String correlationId;
    private String replyTo;
    private Long deliveryTimeout;
    
    public SendMessageRequest() {
        this.persistent = true;
        this.priority = 5;
    }
    
    // Getters and Setters
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    
    public String getExchangeName() { return exchangeName; }
    public void setExchangeName(String exchangeName) { this.exchangeName = exchangeName; }
    
    public String getRoutingKey() { return routingKey; }
    public void setRoutingKey(String routingKey) { this.routingKey = routingKey; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public byte[] getPayload() { return payload; }
    public void setPayload(byte[] payload) { this.payload = payload; }
    
    public Map<String, String> getHeaders() { return headers; }
    public void setHeaders(Map<String, String> headers) { this.headers = headers; }
    
    public int getPriority() { return priority; }
    public void setPriority(int priority) { this.priority = priority; }
    
    public Long getTtl() { return ttl; }
    public void setTtl(Long ttl) { this.ttl = ttl; }
    
    public boolean isPersistent() { return persistent; }
    public void setPersistent(boolean persistent) { this.persistent = persistent; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    
    public String getReplyTo() { return replyTo; }
    public void setReplyTo(String replyTo) { this.replyTo = replyTo; }
    
    public Long getDeliveryTimeout() { return deliveryTimeout; }
    public void setDeliveryTimeout(Long deliveryTimeout) { this.deliveryTimeout = deliveryTimeout; }
    
    @Override
    public String toString() {
        return "SendMessageRequest{" +
                "queueName='" + queueName + '\'' +
                ", messageType='" + messageType + '\'' +
                ", priority=" + priority +
                '}';}
}
