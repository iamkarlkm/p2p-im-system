package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "im_typing_status",
       uniqueConstraints = @UniqueConstraint(columnNames = {"conversation_id", "user_id"}),
       indexes = {
           @Index(name = "idx_typing_conversation", columnList = "conversation_id"),
           @Index(name = "idx_typing_expires", columnList = "expires_at")
       })
public class TypingStatusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 会话ID */
    @Column(name = "conversation_id", nullable = false, length = 64)
    private String conversationId;

    /** 正在输入的用户ID */
    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    /** 输入类型: PRIVATE / GROUP */
    @Column(name = "conversation_type", nullable = false, length = 16)
    private String conversationType;

    /** 最后更新时间 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 过期时间 (Typing状态5秒过期) */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    @PreUpdate
    public void initTime() {
        LocalDateTime now = LocalDateTime.now();
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
        if (this.expiresAt == null) {
            this.expiresAt = now.plusSeconds(5);
        }
    }
}
