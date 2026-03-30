package com.im.backend.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 消息撤回记录实体
 */
@Entity
@Table(name = "message_recall", indexes = {
    @Index(name = "idx_message_id", columnList = "messageId"),
    @Index(name = "idx_sender_id", columnList = "senderId")
})
public class MessageRecall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    private Long messageId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "conversation_type", length = 20, nullable = false)
    private String conversationType;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "original_content", length = 4000)
    private String originalContent;

    @Column(name = "recall_reason", length = 200)
    private String recallReason;

    @CreationTimestamp
    @Column(name = "recalled_at", nullable = false, updatable = false)
    private LocalDateTime recalledAt;

    // Constructors
    public MessageRecall() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public String getOriginalContent() { return originalContent; }
    public void setOriginalContent(String originalContent) { this.originalContent = originalContent; }

    public String getRecallReason() { return recallReason; }
    public void setRecallReason(String recallReason) { this.recallReason = recallReason; }

    public LocalDateTime getRecalledAt() { return recalledAt; }
    public void setRecalledAt(LocalDateTime recalledAt) { this.recalledAt = recalledAt; }
}
