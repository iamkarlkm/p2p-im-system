package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "sync_checkpoint", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_device_conv", columnNames = {"user_id", "device_id", "conversation_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncCheckpoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @Column(name = "last_message_id", nullable = false)
    private Long lastMessageId;

    @Column(name = "last_synced_at", nullable = false)
    private LocalDateTime lastSyncedAt;

    @Column(name = "sync_token")
    private String syncToken;

    @PrePersist
    protected void onCreate() {
        if (lastSyncedAt == null) {
            lastSyncedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        lastSyncedAt = LocalDateTime.now();
    }
}
