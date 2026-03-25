package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

/**
 * 已删除消息实体 - 软删除消息记录
 * 支持管理员可见残留和删除日志
 */
@Entity
@Table(name = "deleted_messages")
@Getter
@Setter
public class DeletedMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 原始消息ID */
    @Column(name = "original_message_id", nullable = false)
    private String originalMessageId;

    /** 消息类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.TEXT;

    /** 原始内容（加密存储） */
    @Column(name = "original_content", columnDefinition = "TEXT")
    private String originalContent;

    /** 内容哈希（用于验证） */
    @Column(name = "content_hash", length = 64)
    private String contentHash;

    /** 发送者用户ID */
    @Column(name = "sender_id", nullable = false)
    private String senderId;

    /** 接收者信息：用户ID或会话ID */
    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    /** 接收者类型：USER（用户）或 GROUP（群组） */
    @Enumerated(EnumType.STRING)
    @Column(name = "receiver_type", nullable = false)
    private ReceiverType receiverType = ReceiverType.USER;

    /** 删除时间 */
    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt = LocalDateTime.now();

    /** 删除原因 */
    @Enumerated(EnumType.STRING)
    @Column(name = "delete_reason", nullable = false)
    private DeleteReason deleteReason = DeleteReason.USER_SELF_DELETE;

    /** 删除者用户ID（谁执行了删除） */
    @Column(name = "deleted_by_user_id", nullable = false)
    private String deletedByUserId;

    /** 删除者类型：USER（普通用户）或 ADMIN（管理员） */
    @Enumerated(EnumType.STRING)
    @Column(name = "deleted_by_type", nullable = false)
    private DeletedByType deletedByType = DeletedByType.USER;

    /** 是否管理员可见 */
    @Column(name = "admin_visible", nullable = false)
    private boolean adminVisible = true;

    /** 审核状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "audit_status", nullable = false)
    private AuditStatus auditStatus = AuditStatus.PENDING;

    /** 审核备注 */
    @Column(name = "audit_notes", length = 500)
    private String auditNotes;

    /** 审核时间 */
    @Column(name = "audited_at")
    private LocalDateTime auditedAt;

    /** 审核者用户ID */
    @Column(name = "audited_by_user_id")
    private String auditedByUserId;

    /** 删除操作日志ID（关联管理日志） */
    @Column(name = "operation_log_id")
    private String operationLogId;

    /** 是否彻底删除（物理删除标记） */
    @Column(name = "permanently_deleted", nullable = false)
    private boolean permanentlyDeleted = false;

    /** 彻底删除时间 */
    @Column(name = "permanent_delete_at")
    private LocalDateTime permanentDeleteAt;

    /** 保留期限（天） */
    @Column(name = "retention_days", nullable = false)
    private Integer retentionDays = 30;

    /** 到期删除时间 */
    @Column(name = "expire_delete_at")
    private LocalDateTime expireDeleteAt;

    /** 元数据（JSON格式） */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    // 枚举定义

    /** 消息类型枚举 */
    public enum MessageType {
        TEXT,
        IMAGE,
        VOICE,
        VIDEO,
        FILE,
        LOCATION,
        CONTACT,
        LINK,
        SYSTEM,
        RICH_TEXT,
        FORWARD,
        REPLY,
        QUOTE,
        REACTION,
        POLL,
        EVENT,
        CUSTOM
    }

    /** 接收者类型枚举 */
    public enum ReceiverType {
        USER,
        GROUP,
        CHANNEL,
        TOPIC
    }

    /** 删除原因枚举 */
    public enum DeleteReason {
        USER_SELF_DELETE,          // 用户自行删除
        GROUP_ADMIN_DELETE,        // 群管理员删除
        SYSTEM_ADMIN_DELETE,       // 系统管理员删除
        AUTOMATIC_CLEANUP,         // 自动清理
        CONTENT_VIOLATION,         // 内容违规
        USER_REPORTED,             // 用户举报
        LEGAL_REQUEST,             // 法律要求
        DATA_CORRUPTION,           // 数据损坏
        MIGRATION_CLEANUP,         // 迁移清理
        OTHER                      // 其他原因
    }

    /** 删除者类型枚举 */
    public enum DeletedByType {
        USER,
        ADMIN,
        SYSTEM,
        BOT
    }

    /** 审核状态枚举 */
    public enum AuditStatus {
        PENDING,        // 待审核
        APPROVED,       // 审核通过
        REJECTED,       // 审核拒绝
        REVIEWED,       // 已审查
        ESCALATED,      // 已升级
        IGNORED         // 已忽略
    }

    // 构造方法

    public DeletedMessageEntity() {
        // 计算到期删除时间（保留期限后）
        this.expireDeleteAt = LocalDateTime.now().plusDays(retentionDays);
    }

    public DeletedMessageEntity(String originalMessageId, String senderId, String receiverId, 
                               ReceiverType receiverType, String deletedByUserId, DeleteReason deleteReason) {
        this();
        this.originalMessageId = originalMessageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.receiverType = receiverType;
        this.deletedByUserId = deletedByUserId;
        this.deleteReason = deleteReason;
    }

    // 辅助方法

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireDeleteAt);
    }

    public boolean needsReview() {
        return deleteReason == DeleteReason.CONTENT_VIOLATION || 
               deleteReason == DeleteReason.USER_REPORTED ||
               deleteReason == DeleteReason.LEGAL_REQUEST;
    }

    public String getSafePreview(int maxLength) {
        if (originalContent == null || originalContent.isEmpty()) {
            return "[无内容]";
        }
        if (originalContent.length() <= maxLength) {
            return originalContent;
        }
        return originalContent.substring(0, maxLength) + "...";
    }

    public void markAsAudited(AuditStatus status, String notes, String auditorId) {
        this.auditStatus = status;
        this.auditNotes = notes;
        this.auditedByUserId = auditorId;
        this.auditedAt = LocalDateTime.now();
    }

    public void markAsPermanentlyDeleted() {
        this.permanentlyDeleted = true;
        this.permanentDeleteAt = LocalDateTime.now();
    }
}