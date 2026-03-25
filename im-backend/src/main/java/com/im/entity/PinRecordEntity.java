package com.im.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 消息置顶记录实体
 * 支持会话置顶和消息置顶两种模式
 */
@Entity
@Table(name = "im_pin_record", indexes = {
    @Index(name = "idx_conversation_pin", columnList = "conversationId, pinType"),
    @Index(name = "idx_user_pin", columnList = "userId, pinType"),
    @Index(name = "idx_pinned_at", columnList = "pinnedAt")
}, uniqueConstraints = {
    @UniqueConstraint(name = "uk_conversation_message", columnNames = {"conversationId", "messageId", "pinType", "userId"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PinRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 会话ID */
    @Column(nullable = false, length = 64)
    private String conversationId;

    /** 消息ID (消息置顶时使用) */
    @Column(length = 64)
    private String messageId;

    /**
     * 置顶类型:
     * - CONVERSATION: 会话置顶
     * - MESSAGE: 消息置顶
     */
    @Column(nullable = false, length = 20)
    private String pinType;

    /** 置顶的用户ID */
    @Column(nullable = false, length = 64)
    private String userId;

    /** 置顶顺序 (数字越大越靠前) */
    @Column(nullable = false)
    @Builder.Default
    private Long sortOrder = 0L;

    /** 置顶时间 */
    @Column(nullable = false)
    private LocalDateTime pinnedAt;

    /** 置顶备注 */
    @Column(length = 255)
    private String note;

    @PrePersist
    protected void onCreate() {
        if (pinnedAt == null) pinnedAt = LocalDateTime.now();
    }
}
