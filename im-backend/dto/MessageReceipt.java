package com.im.dto;

import java.time.Instant;

/**
 * 消息回执DTO
 * 功能 #1: 消息队列核心系统
 */
public class MessageReceipt {
    
    private String messageId;
    private String queueName;
    private ReceiptStatus status;
    private Instant sendTime;
    private Instant confirmTime;
    private long deliveryTag;
    private String errorCode;
    private String errorMessage;
    private long queuePosition;
    private long estimatedDeliveryTime;
    
    public enum ReceiptStatus {
        ACCEPTED, REJECTED, QUEUED, DELIVERED, FAILED, TIMEOUT
    }
    
    public MessageReceipt() {
        this.sendTime = Instant.now();
    }
    
    public MessageReceipt(String messageId, ReceiptStatus status) {
        this();
        this.messageId = messageId;
        this.status = status;
    }
    
    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getQueueName() { return queueName; }
    public void setQueueName(String queueName) { this.queueName = queueName; }
    
    public ReceiptStatus getStatus() { return status; }
    public void setStatus(ReceiptStatus status) { this.status = status; }
    
    public Instant getSendTime() { return sendTime; }
    public void setSendTime(Instant sendTime) { this.sendTime = sendTime; }
    
    public Instant getConfirmTime() { return confirmTime; }
    public void setConfirmTime(Instant confirmTime) { this.confirmTime = confirmTime; }
    
    public long getDeliveryTag() { return deliveryTag; }
    public void setDeliveryTag(long deliveryTag) { this.deliveryTag = deliveryTag; }
    
    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public long getQueuePosition() { return queuePosition; }
    public void setQueuePosition(long queuePosition) { this.queuePosition = queuePosition; }
    
    public long getEstimatedDeliveryTime() { return estimatedDeliveryTime; }
    public void setEstimatedDeliveryTime(long estimatedDeliveryTime) { this.estimatedDeliveryTime = estimatedDeliveryTime; }
    
    /**
     * 创建成功回执
     */
    public static MessageReceipt success(String messageId) {
        MessageReceipt receipt = new MessageReceipt(messageId, ReceiptStatus.ACCEPTED);
        receipt.setConfirmTime(Instant.now());
        return receipt;
    }
    
    /**
     * 创建失败回执
     */
    public static MessageReceipt failure(String messageId, String errorCode, String errorMessage) {
        MessageReceipt receipt = new MessageReceipt(messageId, ReceiptStatus.FAILED);
        receipt.setErrorCode(errorCode);
        receipt.setErrorMessage(errorMessage);
        receipt.setConfirmTime(Instant.now());
        return receipt;
    }
    
    @Override
    public String toString() {
        return "MessageReceipt{" +
                "messageId='" + messageId + '\'' +
                ", status=" + status +
                ", sendTime=" + sendTime +
                '}';
    }
}
