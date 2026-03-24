package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_forwards",
    indexes = {
        @Index(name = "idx_forward_message", columnList = "originalMessageId"),
        @Index(name = "idx_forward_conversation", columnList = "targetConversationId"),
        @Index(name = "idx_forward_user", columnList = "forwardedBy")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageForward {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long originalMessageId;

    @Column(nullable = false)
    private Long targetConversationId;

    @Column(nullable = false)
    private Long forwardedBy;

    @Column(nullable = false)
    private LocalDateTime forwardedAt;

    @Column(length = 1000)
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForwardType forwardType = ForwardType.SINGLE;

    public enum ForwardType {
        SINGLE,
        MERGED
    }
}
