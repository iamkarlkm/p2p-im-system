package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "im_message_draft",
       indexes = {
           @Index(name = "idx_draft_user", columnList = "userId"),
           @Index(name = "idx_draft_conversation", columnList = "conversationId, userId")
       })
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDraft {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String conversationId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String mentionIds;

    @Column(length = 500)
    private String replyMessageId;

    @Column(length = 50)
    private String messageType;

    @Column
    private Long updatedAt;

    @Column
    private Boolean isDeleted;

    @PrePersist
    public void prePersist() {
        if (this.updatedAt == null) {
            this.updatedAt = System.currentTimeMillis();
        }
        if (this.isDeleted == null) {
            this.isDeleted = false;
        }
    }
}
