package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Entity
@Table(name = "im_message_reaction",
    uniqueConstraints = @UniqueConstraint(columnNames = {"messageId", "userId", "emoji"}),
    indexes = {
        @Index(name = "idx_reaction_msg", columnList = "messageId"),
        @Index(name = "idx_reaction_user", columnList = "userId")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageReactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long messageId;

    @Column(nullable = false)
    private Long userId;

    /** 表情 Unicode 或 emoji ID */
    @Column(nullable = false, length = 64)
    private String emoji;

    /** 是否为自定义表情包 */
    @Builder.Default
    private Boolean isCustom = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
