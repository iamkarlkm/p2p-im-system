package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * @提醒消息实体
 * 功能#28: 消息@提醒
 */
@Entity
@Table(name = "mention_messages")
public class MentionMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;
    
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    @Column(name = "group_id", nullable = false)
    private Long groupId;
    
    @Column(name = "mentioned_user_id", nullable = false)
    private Long mentionedUserId;
    
    @Column(name = "original_message_id", nullable = false)
    private String originalMessageId;
    
    @Column(name = "original_content", length = 2000)
    private String originalContent;
    
    @Column(name = "mention_type", length = 20)
    private String mentionType; // @user, @all
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "read_time")
    private LocalDateTime readTime;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public Long getMentionedUserId() { return mentionedUserId; }
    public void setMentionedUserId(Long mentionedUserId) { this.mentionedUserId = mentionedUserId; }
    
    public String getOriginalMessageId() { return originalMessageId; }
    public void setOriginalMessageId(String originalMessageId) { this.originalMessageId = originalMessageId; }
    
    public String getOriginalContent() { return originalContent; }
    public void setOriginalContent(String originalContent) { this.originalContent = originalContent; }
    
    public String getMentionType() { return mentionType; }
    public void setMentionType(String mentionType) { this.mentionType = mentionType; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public LocalDateTime getReadTime() { return readTime; }
    public void setReadTime(LocalDateTime readTime) { this.readTime = readTime; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
