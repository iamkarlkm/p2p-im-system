package com.im.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 聊天消息实体类
 * 功能 #6: 消息存储与检索引擎
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ==================== 消息类型 ====================
    public enum MessageType {
        TEXT("文本"),
        IMAGE("图片"),
        FILE("文件"),
        VOICE("语音"),
        VIDEO("视频"),
        LOCATION("位置"),
        SYSTEM("系统");
        
        private final String description;
        
        MessageType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // ==================== 消息状态 ====================
    public enum MessageStatus {
        SENDING("发送中"),
        SENT("已发送"),
        DELIVERED("已送达"),
        READ("已读"),
        FAILED("失败"),
        RECALLED("已撤回");
        
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
    private String conversationId;
    private String senderId;
    private String receiverId;
    private MessageType messageType;
    private String content;
    private Map<String, Object> extras;
    private MessageStatus status;
    private LocalDateTime sendTime;
    private LocalDateTime deliverTime;
    private LocalDateTime readTime;
    private Boolean isGroup;
    private String groupId;
    private Boolean recalled;
    private String recallReason;
    private LocalDateTime recallTime;
    private String clientMessageId;
    
    // ==================== 构造函数 ====================
    public ChatMessage() {
        this.status = MessageStatus.SENDING;
        this.sendTime = LocalDateTime.now();
        this.isGroup = false;
        this.recalled = false;
    }
    
    // ==================== 业务方法 ====================
    
    public void markSent() {
        this.status = MessageStatus.SENT;
    }
    
    public void markDelivered() {
        this.status = MessageStatus.DELIVERED;
        this.deliverTime = LocalDateTime.now();
    }
    
    public void markRead() {
        this.status = MessageStatus.READ;
        this.readTime = LocalDateTime.now();
    }
    
    public void markFailed() {
        this.status = MessageStatus.FAILED;
    }
    
    public void recall(String reason) {
        this.recalled = true;
        this.recallReason = reason;
        this.recallTime = LocalDateTime.now();
        this.status = MessageStatus.RECALLED;
    }
    
    public boolean canRecall(int minutesLimit) {
        if (recalled) return false;
        return java.time.Duration.between(sendTime, LocalDateTime.now()).toMinutes() <= minutesLimit;
    }
    
    // ==================== Getter & Setter ====================
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    
    public String getSenderId() { return senderId; }
    public void setSenderId(String senderId) { this.senderId = senderId; }
    
    public String getReceiverId() { return receiverId; }
    public void setReceiverId(String receiverId) { this.receiverId = receiverId; }
    
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Map<String, Object> getExtras() { return extras; }
    public void setExtras(Map<String, Object> extras) { this.extras = extras; }
    
    public MessageStatus getStatus() { return status; }
    public void setStatus(MessageStatus status) { this.status = status; }
    
    public LocalDateTime getSendTime() { return sendTime; }
    public void setSendTime(LocalDateTime sendTime) { this.sendTime = sendTime; }
    
    public LocalDateTime getDeliverTime() { return deliverTime; }
    public void setDeliverTime(LocalDateTime deliverTime) { this.deliverTime = deliverTime; }
    
    public LocalDateTime getReadTime() { return readTime; }
    public void setReadTime(LocalDateTime readTime) { this.readTime = readTime; }
    
    public Boolean getIsGroup() { return isGroup; }
    public void setIsGroup(Boolean isGroup) { this.isGroup = isGroup; }
    
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    
    public Boolean getRecalled() { return recalled; }
    public void setRecalled(Boolean recalled) { this.recalled = recalled; }
    
    public String getRecallReason() { return recallReason; }
    public void setRecallReason(String recallReason) { this.recallReason = recallReason; }
    
    public LocalDateTime getRecallTime() { return recallTime; }
    public void setRecallTime(LocalDateTime recallTime) { this.recallTime = recallTime; }
    
    public String getClientMessageId() { return clientMessageId; }
    public void setClientMessageId(String clientMessageId) { this.clientMessageId = clientMessageId; }
}
