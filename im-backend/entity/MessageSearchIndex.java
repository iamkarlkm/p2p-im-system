package com.im.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 消息搜索索引实体
 * 用于全文搜索的消息索引表
 */
@Entity
@Table(name = "message_search_index", indexes = {
    @Index(name = "idx_content", columnList = "content"),
    @Index(name = "idx_sender_id", columnList = "senderId"),
    @Index(name = "idx_conversation", columnList = "conversationType,conversationId"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class MessageSearchIndex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    private Long messageId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "sender_name", length = 100)
    private String senderName;

    @Column(name = "conversation_type", length = 20, nullable = false)
    private String conversationType; // PRIVATE, GROUP

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "content", length = 4000, nullable = false)
    private String content;

    @Column(name = "content_type", length = 20)
    private String contentType; // TEXT, IMAGE, FILE, VOICE

    @Column(name = "keywords", length = 1000)
    private String keywords; // 提取的关键词，逗号分隔

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public MessageSearchIndex() {}

    public MessageSearchIndex(Long messageId, Long senderId, String conversationType, 
                              Long conversationId, String content) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.conversationType = conversationType;
        this.conversationId = conversationId;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public String getKeywords() { return keywords; }
    public void setKeywords(String keywords) { this.keywords = keywords; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
