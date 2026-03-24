package com.im.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "message_quotes")
public class MessageQuoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(name = "quoted_message_id", nullable = false)
    private UUID quotedMessageId;

    @Column(name = "conversation_id", nullable = false)
    private UUID conversationId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "quoted_content", columnDefinition = "TEXT")
    private String quotedContent;

    @Column(name = "quoted_sender_id")
    private UUID quotedSenderId;

    @Column(name = "quoted_sender_name", length = 100)
    private String quotedSenderName;

    @Column(name = "quote_type", length = 20)
    private String quoteType;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "quote_preview", length = 500)
    private String quotePreview;

    @Column(name = "attachment_count")
    private Integer attachmentCount = 0;

    @Column(name = "has_attachment")
    private Boolean hasAttachment = false;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (quoteType == null) {
            quoteType = "TEXT";
        }
        if (isDeleted == null) {
            isDeleted = false;
        }
        if (hasAttachment == null) {
            hasAttachment = false;
        }
        if (attachmentCount == null) {
            attachmentCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public MessageQuoteEntity() {}

    public MessageQuoteEntity(UUID messageId, UUID quotedMessageId, UUID conversationId, UUID userId) {
        this.messageId = messageId;
        this.quotedMessageId = quotedMessageId;
        this.conversationId = conversationId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted = false;
        this.hasAttachment = false;
        this.attachmentCount = 0;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public UUID getQuotedMessageId() {
        return quotedMessageId;
    }

    public void setQuotedMessageId(UUID quotedMessageId) {
        this.quotedMessageId = quotedMessageId;
    }

    public UUID getConversationId() {
        return conversationId;
    }

    public void setConversationId(UUID conversationId) {
        this.conversationId = conversationId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getQuotedContent() {
        return quotedContent;
    }

    public void setQuotedContent(String quotedContent) {
        this.quotedContent = quotedContent;
    }

    public UUID getQuotedSenderId() {
        return quotedSenderId;
    }

    public void setQuotedSenderId(UUID quotedSenderId) {
        this.quotedSenderId = quotedSenderId;
    }

    public String getQuotedSenderName() {
        return quotedSenderName;
    }

    public void setQuotedSenderName(String quotedSenderName) {
        this.quotedSenderName = quotedSenderName;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getQuotePreview() {
        return quotePreview;
    }

    public void setQuotePreview(String quotePreview) {
        this.quotePreview = quotePreview;
    }

    public Integer getAttachmentCount() {
        return attachmentCount;
    }

    public void setAttachmentCount(Integer attachmentCount) {
        this.attachmentCount = attachmentCount;
    }

    public Boolean getHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(Boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }
}