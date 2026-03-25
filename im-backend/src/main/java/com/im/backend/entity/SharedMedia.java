package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@Entity
@Table(name = "shared_media", indexes = {
    @Index(name = "idx_conversation_id", columnList = "conversationId"),
    @Index(name = "idx_media_type", columnList = "mediaType"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SharedMedia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String conversationId;

    @Column(nullable = false)
    private String messageId;

    @Column(nullable = false)
    private String senderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MediaType mediaType;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String fileUrl;

    private String thumbnailUrl;

    private Long fileSize;

    private String mimeType;

    private Integer width;

    private Integer height;

    private Long duration;

    private String description;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant expiresAt;

    private Boolean isDeleted;

    public enum MediaType {
        IMAGE, VIDEO, AUDIO, FILE, LINK, VOICE
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (isDeleted == null) isDeleted = false;
    }
}
