package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 置顶消息实体类
 * 用于存储会话中的置顶消息信息
 */
@Entity
@Table(name = "pinned_messages", indexes = {
    @Index(name = "idx_conversation_id", columnList = "conversationId"),
    @Index(name = "idx_pinned_by", columnList = "pinnedBy"),
    @Index(name = "idx_pin_order", columnList = "conversationId, pinOrder")
})
public class PinnedMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;
    
    @Column(name = "message_id", nullable = false)
    private Long messageId;
    
    @Column(name = "pinned_by", nullable = false)
    private Long pinnedBy;
    
    @Column(name = "pin_order", nullable = false)
    private Integer pinOrder;
    
    @Column(name = "pin_note", length = 200)
    private String pinNote;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Transient
    private Message message;
    
    @Transient
    private User pinnedByUser;
    
    // 常量定义
    public static final int MAX_PINS_PER_CONVERSATION = 50;
    public static final int DEFAULT_PIN_ORDER = 0;
    
    public PinnedMessage() {
        this.createdAt = LocalDateTime.now();
    }
    
    public PinnedMessage(Long conversationId, Long messageId, Long pinnedBy) {
        this();
        this.conversationId = conversationId;
        this.messageId = messageId;
        this.pinnedBy = pinnedBy;
        this.pinOrder = DEFAULT_PIN_ORDER;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
    
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public Long getPinnedBy() {
        return pinnedBy;
    }
    
    public void setPinnedBy(Long pinnedBy) {
        this.pinnedBy = pinnedBy;
    }
    
    public Integer getPinOrder() {
        return pinOrder;
    }
    
    public void setPinOrder(Integer pinOrder) {
        this.pinOrder = pinOrder;
    }
    
    public String getPinNote() {
        return pinNote;
    }
    
    public void setPinNote(String pinNote) {
        this.pinNote = pinNote;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Message getMessage() {
        return message;
    }
    
    public void setMessage(Message message) {
        this.message = message;
    }
    
    public User getPinnedByUser() {
        return pinnedByUser;
    }
    
    public void setPinnedByUser(User pinnedByUser) {
        this.pinnedByUser = pinnedByUser;
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    @Override
    public String toString() {
        return "PinnedMessage{id=" + id + ", conversationId=" + conversationId + 
               ", messageId=" + messageId + ", pinnedBy=" + pinnedBy + "}";
    }
}
