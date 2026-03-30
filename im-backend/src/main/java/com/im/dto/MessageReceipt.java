package com.im.dto;

import java.time.LocalDateTime;

/**
 * 消息回执DTO
 * 功能 #1: 消息队列核心系统 - 消息确认
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class MessageReceipt {
    
    // ==================== 回执状态枚举 ====================
    public enum ReceiptStatus {
        ACCEPTED("已接受"),
        REJECTED("被拒绝"),
        FAILED("失败"),
        DUPLICATE("重复消息");
        
        private final String description;
        
        ReceiptStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // ==================== 核心字段 ====================
    private String messageId;
    private ReceiptStatus status;
    private String errorMessage;
    private LocalDateTime receiptTime;
    private Long queueOffset;
    private String traceId;
    
    // ==================== 构造函数 ====================
    public MessageReceipt() {
        this.receiptTime = LocalDateTime.now();
    }
    
    public MessageReceipt(String messageId, ReceiptStatus status) {
        this();
        this.messageId = messageId;
        this.status = status;
    }
    
    // ==================== 工厂方法 ====================
    
    /**
     * 成功接受
     */
    public static MessageReceipt accepted(String messageId) {
        return new MessageReceipt(messageId, ReceiptStatus.ACCEPTED);
    }
    
    /**
     * 被拒绝
     */
    public static MessageReceipt rejected(String messageId, String reason) {
        MessageReceipt receipt = new MessageReceipt(messageId, ReceiptStatus.REJECTED);
        receipt.setErrorMessage(reason);
        return receipt;
    }
    
    /**
     * 失败
     */
    public static MessageReceipt failed(String messageId, String error) {
        MessageReceipt receipt = new MessageReceipt(messageId, ReceiptStatus.FAILED);
        receipt.setErrorMessage(error);
        return receipt;
    }
    
    /**
     * 重复消息
     */
    public static MessageReceipt duplicate(String messageId) {
        return new MessageReceipt(messageId, ReceiptStatus.DUPLICATE);
    }
    
    /**
     * 是否成功
     */
    public boolean isSuccess() {
        return status == ReceiptStatus.ACCEPTED;
    }
    
    // ==================== Getter & Setter ====================
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public ReceiptStatus getStatus() { return status; }
    public void setStatus(ReceiptStatus status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public LocalDateTime getReceiptTime() { return receiptTime; }
    public void setReceiptTime(LocalDateTime receiptTime) { this.receiptTime = receiptTime; }
    
    public Long getQueueOffset() { return queueOffset; }
    public void setQueueOffset(Long queueOffset) { this.queueOffset = queueOffset; }
    
    public String getTraceId() { return traceId; }
    public void setTraceId(String traceId) { this.traceId = traceId; }
    
    @Override
    public String toString() {
        return "MessageReceipt{" +
                "messageId='" + messageId + '\'' +
                ", status=" + status +
                ", receiptTime=" + receiptTime +
                '}';
    }
}
