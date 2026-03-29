package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversation_pinned",
    indexes = {
        @Index(name = "idx_pinned_user", columnList = "userId"),
        @Index(name = "idx_pinned_conv", columnList = "conversationId")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationPinned {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long conversationId;

    @Column(nullable = false)
    private Integer sortOrder;

    @Column(nullable = false)
    private LocalDateTime pinnedAt;

    @Column(length = 100)
    private String pinNote;
}
