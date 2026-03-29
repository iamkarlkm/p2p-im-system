package com.im.server.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * 消息已读回执实体
 */
@Entity
@Table(name = "im_read_receipts", indexes = {
    @Index(name = "idx_message_user", columnList = "message_id, user_id", unique = true),
    @Index(name = "idx_conversation_user", columnList = "conversation_id, user_id"),
    @Index(name = "idx_read_at", columnList = "read_at")
})
public class ReadReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "read_at", nullable = false)
    private Instant readAt;

    @Column(name = "read_status", nullable = false)
    private String readStatus;

    // Constructors
    public ReadReceipt() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }

    public String getReadStatus() { return readStatus; }
    public void setReadStatus(String readStatus) { this.readStatus = readStatus; }
}
