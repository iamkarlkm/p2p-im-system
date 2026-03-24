package com.im.message.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 消息合并转发实体
 * 支持多条消息合并为一条转发消息，保留原始消息内容和发送者信息
 */
@Entity
@Table(name = "message_forward_bundles", indexes = {
    @Index(name = "idx_forward_bundle_id", columnList = "bundle_id"),
    @Index(name = "idx_created_by", columnList = "created_by"),
    @Index(name = "idx_created_at", columnList = "created_at")
})
@Getter
@Setter
public class MessageForwardBundleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bundle_id", nullable = false, unique = true, length = 100)
    private String bundleId; // 唯一 bundle 标识符

    @Column(name = "source_conversation_id", nullable = false)
    private Long sourceConversationId; // 源会话 ID

    @Column(name = "target_conversation_id")
    private Long targetConversationId; // 目标会话 ID（转发后）

    @Column(name = "created_by", nullable = false)
    private Long createdBy; // 创建者用户 ID

    @Column(name = "forward_type", nullable = false, length = 20)
    private ForwardType forwardType; // MERGE(合并转发) / SELECT(选择转发) / INDIVIDUAL(逐条转发)

    @Column(name = "title", length = 200)
    private String title; // 合并转发的标题（可选）

    @ElementCollection
    @CollectionTable(name = "forward_bundle_messages", joinColumns = @JoinColumn(name = "bundle_id"))
    @Column(name = "message_id", nullable = false)
    @OrderColumn(name = "message_order")
    private List<Long> messageIds = new ArrayList<>(); // 包含的消息 ID 列表

    @Column(name = "message_count", nullable = false)
    private Integer messageCount = 0; // 消息总数

    @Column(name = "total_size_bytes", nullable = false)
    private Long totalSizeBytes = 0L; // 总大小（字节）

    @Column(name = "has_media", nullable = false)
    private Boolean hasMedia = false; // 是否包含媒体文件

    @Column(name = "media_count", nullable = false)
    private Integer mediaCount = 0; // 媒体文件数量

    @Column(name = "forwarded_at")
    private LocalDateTime forwardedAt; // 转发时间

    @Column(name = "status", nullable = false, length = 20)
    private ForwardStatus status = ForwardStatus.DRAFT; // DRAFT/SENT/FAILED

    @Column(name = "send_mode", nullable = false, length = 20)
    private SendMode sendMode = SendMode.MERGE; // MERGE(合并为一条) / SEPARATE(逐条发送)

    @Column(name = "include_sender_info", nullable = false)
    private Boolean includeSenderInfo = true; // 是否包含发送者信息

    @Column(name = "include_timestamp", nullable = false)
    private Boolean includeTimestamp = true; // 是否包含时间戳

    @Column(name = "anonymize_senders", nullable = false)
    private Boolean anonymizeSenders = false; // 是否匿名发送者（隐藏发送者昵称）

    @Column(name = "add_custom_comment", length = 1000)
    private String customComment; // 自定义评论（添加到转发消息开头）

    @Column(name = "forward_message_id")
    private Long forwardMessageId; // 转发后生成的消息 ID

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0; // 重试次数

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage; // 错误信息（如果失败）

    @Column(name = "metadata_json", columnDefinition = "TEXT")
    private String metadataJson; // 额外的元数据 JSON

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 转发类型枚举
     */
    public enum ForwardType {
        MERGE,      // 合并转发（多条消息合并为一条）
        SELECT,     // 选择转发（选择特定消息转发）
        INDIVIDUAL  // 逐条转发（每条消息单独发送）
    }

    /**
     * 转发状态枚举
     */
    public enum ForwardStatus {
        DRAFT,    // 草稿（未发送）
        PENDING,  // 待发送
        SENT,     // 已发送
        FAILED,   // 发送失败
        CANCELLED // 已取消
    }

    /**
     * 发送模式枚举
     */
    public enum SendMode {
        MERGE,    // 合并为一条消息发送
        SEPARATE  // 逐条发送
    }

    /**
     * 添加消息到转发列表
     */
    public void addMessage(Long messageId) {
        if (this.messageIds == null) {
            this.messageIds = new ArrayList<>();
        }
        this.messageIds.add(messageId);
        this.messageCount = this.messageIds.size();
    }

    /**
     * 移除消息从转发列表
     */
    public void removeMessage(Long messageId) {
        if (this.messageIds != null) {
            this.messageIds.remove(messageId);
            this.messageCount = this.messageIds.size();
        }
    }

    /**
     * 清除所有消息
     */
    public void clearMessages() {
        if (this.messageIds != null) {
            this.messageIds.clear();
            this.messageCount = 0;
        }
    }

    /**
     * 生成 Bundle ID
     */
    public static String generateBundleId() {
        return "fwd_" + System.currentTimeMillis() + "_" + 
               java.util.UUID.randomUUID().toString().substring(0, 8);
    }
}