package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息已读记录实体类
 * 对应功能 #16 - 消息已读回执功能
 */
@Entity
@Table(name = "im_message_read_receipt",
       uniqueConstraints = @UniqueConstraint(columnNames = {"message_id", "user_id"}))
public class MessageReadReceipt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "message_id", nullable = false)
    private Long messageId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "conversation_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ConversationType conversationType;
    
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;
    
    @Column(name = "read_at", nullable = false)
    private LocalDateTime readAt;
    
    public enum ConversationType {
        PRIVATE, GROUP
    }
    
    @PrePersist
    protected void onCreate() {
        readAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public ConversationType getConversationType() { return conversationType; }
    public void setConversationType(ConversationType conversationType) { this.conversationType = conversationType; }
    
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
}
