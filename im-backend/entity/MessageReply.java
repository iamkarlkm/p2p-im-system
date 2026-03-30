package com.im.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 消息引用回复实体
 */
@Entity
@Table(name = "message_reply", indexes = {
    @Index(name = "idx_original_msg", columnList = "originalMessageId"),
    @Index(name = "idx_reply_msg", columnList = "replyMessageId"),
    @Index(name = "idx_sender", columnList = "senderId"),
    @Index(name = "idx_conversation", columnList = "conversationType,conversationId")
})
public class MessageReply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_message_id", nullable = false)
    private Long originalMessageId;

    @Column(name = "reply_message_id", nullable = false, unique = true)
    private Long replyMessageId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "sender_name", length = 100)
    private String senderName;

    @Column(name = "conversation_type", length = 20, nullable = false)
    private String conversationType;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "reply_content", length = 4000, nullable = false)
    private String replyContent;

    @Column(name = "original_content_preview", length = 500)
    private String originalContentPreview;

    @Column(name = "original_sender_name", length = 100)
    private String originalSenderName;

    @Column(name = "parent_reply_id")
    private Long parentReplyId; // 支持嵌套引用

    @Column(name = "reply_level")
    private Integer replyLevel = 1; // 引用层级

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructors
    public MessageReply() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getOriginalMessageId() { return originalMessageId; }
    public void setOriginalMessageId(Long originalMessageId) { this.originalMessageId = originalMessageId; }

    public Long getReplyMessageId() { return replyMessageId; }
    public void setReplyMessageId(Long replyMessageId) { this.replyMessageId = replyMessageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getReplyContent() { return replyContent; }
    public void setReplyContent(String replyContent) { this.replyContent = replyContent; }

    public String getOriginalContentPreview() { return originalContentPreview; }
    public void setOriginalContentPreview(String originalContentPreview) { this.originalContentPreview = originalContentPreview; }

    public String getOriginalSenderName() { return originalSenderName; }
    public void setOriginalSenderName(String originalSenderName) { this.originalSenderName = originalSenderName; }

    public Long getParentReplyId() { return parentReplyId; }
    public void setParentReplyId(Long parentReplyId) { this.parentReplyId = parentReplyId; }

    public Integer getReplyLevel() { return replyLevel; }
    public void setReplyLevel(Integer replyLevel) { this.replyLevel = replyLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
