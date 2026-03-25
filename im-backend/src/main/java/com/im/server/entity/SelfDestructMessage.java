package com.im.server.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * 阅后即焚消息实体
 */
@Entity
@Table(name = "im_self_destruct_messages")
public class SelfDestructMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id", nullable = false)
    private Long receiverId;

    @Column(name = "timer_type", nullable = false)
    private String timerType;

    @Column(name = "duration_seconds", nullable = false)
    private Integer durationSeconds;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name = "destroyed_at")
    private Instant destroyedAt;

    @Column(name = "destroy_reason")
    private String destroyReason;

    // Constructors
    public SelfDestructMessage() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getTimerType() { return timerType; }
    public void setTimerType(String timerType) { this.timerType = timerType; }

    public Integer getDurationSeconds() { return durationSeconds; }
    public void setDurationSeconds(Integer durationSeconds) { this.durationSeconds = durationSeconds; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getReadAt() { return readAt; }
    public void setReadAt(Instant readAt) { this.readAt = readAt; }

    public Instant getDestroyedAt() { return destroyedAt; }
    public void setDestroyedAt(Instant destroyedAt) { this.destroyedAt = destroyedAt; }

    public String getDestroyReason() { return destroyReason; }
    public void setDestroyReason(String destroyReason) { this.destroyReason = destroyReason; }
}
