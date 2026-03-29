package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@Entity
@Table(name = "push_message_buffer", indexes = {
    @Index(name = "idx_buffer_key", columnList = "bufferKey", unique = true),
    @Index(name = "idx_buffer_expires", columnList = "expiresAt")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PushMessageBuffer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 256)
    private String bufferKey;

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

    @Column(length = 2000)
    private String senderNames;

    @Column(length = 1000)
    private String lastMessagePreview;

    @Column(length = 2000)
    private String mergedContent;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private Boolean isMerged;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (isMerged == null) isMerged = false;
    }
}
