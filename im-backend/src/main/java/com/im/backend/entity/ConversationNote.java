package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.Instant;

@Data
@Entity
@Table(name = "conversation_note", indexes = {
    @Index(name = "idx_note_user", columnList = "userId"),
    @Index(name = "idx_note_conversation", columnList = "conversationId")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationNote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String conversationId;

    @Column(nullable = false, length = 2000)
    private String content;

    private String quotedMessageId;

    private String quotedMessageContent;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    private Instant deletedAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = Instant.now();
        if (updatedAt == null) updatedAt = Instant.now();
    }
}
