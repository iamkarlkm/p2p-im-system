package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@Entity
@Table(name = "media_link", indexes = {
    @Index(name = "idx_link_conversation", columnList = "conversationId"),
    @Index(name = "idx_link_domain", columnList = "domain")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String conversationId;

    @Column(nullable = false)
    private String messageId;

    @Column(nullable = false)
    private String senderId;

    @Column(nullable = false)
    private String url;

    private String title;

    private String description;

    private String image;

    private String domain;

    private String favicon;

    @Enumerated(EnumType.STRING)
    private MediaLinkType linkType;

    @Column(nullable = false)
    private Instant createdAt;

    public enum MediaLinkType {
        ARTICLE, VIDEO, PRODUCT, MUSIC, DOCUMENT, UNKNOWN
    }

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
