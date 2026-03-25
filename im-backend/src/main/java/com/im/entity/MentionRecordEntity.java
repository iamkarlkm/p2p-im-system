package com.im.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * @Mention 提醒记录实体
 * 当消息中包含 @提及 时创建此记录
 */
@Data
@Entity
@Table(name = "im_mention_record",
    indexes = {
        @Index(name = "idx_conversation", columnList = "conversation_id"),
        @Index(name = "idx_mentioned_user", columnList = "mentioned_user_id"),
        @Index(name = "idx_message", columnList = "message_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_created", columnList = "created_at")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_message_mentioned",
            columnNames = {"message_id", "mentioned_user_id"})
    })
public class MentionRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 消息ID */
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    /** 会话ID */
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    /** 发送者的用户ID (谁发了这条带@的消息) */
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    /** 被@提及的用户ID */
    @Column(name = "mentioned_user_id", nullable = false)
    private Long mentionedUserId;

    /** @提及类型: USER / ALL / ONLINE_MEMBERS / CHANNEL / ROLE */
    @Column(name = "mention_type", nullable = false, length = 32)
    private String mentionType;

    /** 提及文本原文 (用于预览) */
    @Column(name = "mention_text", length = 128)
    private String mentionText;

    /** 角色ID (当mention_type=ROLE时) */
    @Column(name = "role_id")
    private Long roleId;

    /** 状态: UNREAD / READ / DISMISSED / REPLIED */
    @Column(name = "status", nullable = false, length = 16)
    private String status;

    /** 是否已推送通知 */
    @Column(name = "notified", nullable = false)
    private Boolean notified;

    /** 是否需要推送 (用户在免打扰时段则不推送) */
    @Column(name = "push_enabled", nullable = false)
    private Boolean pushEnabled;

    /** 创建时间 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 已读时间 */
    @Column(name = "read_at")
    private LocalDateTime readAt;

    /** 过期时间 (超过则不显示提醒) */
    @Column(name = "expire_at")
    private LocalDateTime expireAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (status == null) status = "UNREAD";
        if (notified == null) notified = false;
        if (pushEnabled == null) pushEnabled = true;
    }
}
