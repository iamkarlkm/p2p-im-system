package com.im.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息撤回事件记录实体
 * 记录所有消息撤回事件，用于事件溯源、审计和通知
 */
@Data
@Entity
@Table(name = "im_message_recall_event",
    indexes = {
        @Index(name = "idx_recall_message", columnList = "message_id"),
        @Index(name = "idx_recall_conversation", columnList = "conversation_id"),
        @Index(name = "idx_recall_recaller", columnList = "recaller_id"),
        @Index(name = "idx_recall_time", columnList = "recalled_at")
    })
public class MessageRecallEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 被撤回的消息ID */
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    /** 所属会话ID */
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    /** 原始发送者ID */
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    /** 执行撤回操作的用户ID (可能是发送者本人或管理员) */
    @Column(name = "recaller_id", nullable = false)
    private Long recallerId;

    /** 撤回角色: SENDER / ADMIN / GROUP_ADMIN / SYSTEM */
    @Column(name = "recall_role", nullable = false, length = 16)
    private String recallRole;

    /** 撤回原因: USER_RECALL / ADMIN_DELETE / SPAM / ABUSE / EXPIRED / OTHER */
    @Column(name = "recall_reason", length = 32)
    private String recallReason;

    /** 撤回原因详细描述 */
    @Column(name = "reason_detail", length = 256)
    private String reasonDetail;

    /** 撤回方式: SINGLE / BATCH / AUTO_CLEANUP / POLICY_EXPIRE */
    @Column(name = "recall_type", nullable = false, length = 16)
    private String recallType;

    /** 原消息内容的哈希 (用于审计，不可逆加密) */
    @Column(name = "original_content_hash", length = 64)
    private String originalContentHash;

    /** 原始消息类型 (TEXT/IMAGE/FILE/AUDIO/VIDEO 等) */
    @Column(name = "original_type", length = 16)
    private String originalType;

    /** 原始消息发送时间 */
    @Column(name = "original_sent_at")
    private LocalDateTime originalSentAt;

    /** 撤回时间 */
    @Column(name = "recalled_at", nullable = false)
    private LocalDateTime recalledAt;

    /** 是否已通知相关用户 */
    @Column(name = "notified", nullable = false)
    private Boolean notified;

    /** 通知失败重试次数 */
    @Column(name = "retry_count")
    private Integer retryCount;

    /** 是否全局撤回 (管理员操作所有设备) */
    @Column(name = "global_recall", nullable = false)
    private Boolean globalRecall;

    /** 撤回影响的设备数 */
    @Column(name = "affected_devices")
    private Integer affectedDevices;

    @PrePersist
    protected void onCreate() {
        if (recalledAt == null) recalledAt = LocalDateTime.now();
        if (notified == null) notified = false;
        if (retryCount == null) retryCount = 0;
        if (globalRecall == null) globalRecall = false;
        if (affectedDevices == null) affectedDevices = 0;
    }
}
