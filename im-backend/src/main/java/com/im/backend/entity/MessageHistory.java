package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_history", indexes = {
    @Index(name = "idx_msg_history_conv", columnList = "conversation_id"),
    @Index(name = "idx_msg_history_user_time", columnList = "user_id, synced_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    private Long messageId;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    @Column(name = "synced_at", nullable = false)
    private LocalDateTime syncedAt;

    @Column(name = "synced_device_id")
    private String syncedDeviceId;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @PrePersist
    protected void onCreate() {
        if (syncedAt == null) {
            syncedAt = LocalDateTime.now();
        }
        if (deleted == null) {
            deleted = false;
        }
    }
}
