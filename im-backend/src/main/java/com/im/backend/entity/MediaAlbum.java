package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@Entity
@Table(name = "media_album", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_album_type", columnList = "albumType")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaAlbum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String conversationId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlbumType albumType;

    @Column(nullable = false)
    private String name;

    private String description;

    private String coverMediaId;

    private Integer mediaCount;

    private Long totalSize;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    public enum AlbumType {
        IMAGE, VIDEO, AUDIO, FILE, ALL
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
        if (mediaCount == null) mediaCount = 0;
        if (totalSize == null) totalSize = 0L;
    }
}
