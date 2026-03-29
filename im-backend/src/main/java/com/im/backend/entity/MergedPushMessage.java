package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "merged_push_message", indexes = {
    @Index(name = "idx_merged_user", columnList = "userId"),
    @Index(name = "idx_merged_device", columnList = "deviceToken"),
    @Index(name = "idx_merged_status", columnList = "status"),
    @Index(name = "idx_merged_schedule", columnList = "scheduledAt")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MergedPushMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, length = 128)
    private String deviceToken;

    @Column(nullable = false)
    private String conversationId;

    @Column(nullable = false)
    private String conversationType;

    @Column(nullable = false)
    private Integer messageCount;

    @Column(length = 512)
    private String title;

    @Column(length = 1000)
    private String previewText;

    @Column(length = 2000)
    private String mergedContent;

    @Column(length = 1000)
    private String senderNames;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant scheduledAt;

    private Instant sentAt;

    @Column(nullable = false)
    private String status;

    @Column
    private Integer ttlSeconds;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (status == null) status = "PENDING";
        if (ttlSeconds == null) ttlSeconds = 300;
    }
}
