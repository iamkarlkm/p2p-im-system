package com.im.service.message.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 消息实体 - 即时通讯消息核心实体
 * 对应数据库表: im_message
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Entity
@Table(name = "im_message", indexes = {
    @Index(name = "idx_msg_conv", columnList = "conversationId"),
    @Index(name = "idx_msg_sender", columnList = "senderId"),
    @Index(name = "idx_msg_created", columnList = "createdAt"),
    @Index(name = "idx_msg_status", columnList = "status"),
    @Index(name = "idx_msg_conv_created", columnList = "conversationId, createdAt")
})
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    /** 
     * 会话ID - 关联会话表
     */
    @Column(nullable = false, length = 36)
    private String conversationId;

    /** 
     * 发送者ID - 关联用户表
     */
    @Column(nullable = false, length = 36)
    private String senderId;

    /** 
     * 接收者ID - 单聊时为对方用户ID，群聊时为群ID
     */
    @Column(nullable = false, length = 36)
    private String receiverId;

    /** 
     * 消息类型: TEXT(文本), IMAGE(图片), FILE(文件), VOICE(语音), 
     * VIDEO(视频), LOCATION(位置), SYSTEM(系统消息), CUSTOM(自定义)
     */
    @Column(nullable = false, length = 20)
    private String type;

    /** 
     * 会话类型: PRIVATE(私聊), GROUP(群聊), CHANNEL(频道)
     */
    @Column(nullable = false, length = 20)
    private String conversationType = "PRIVATE";

    /** 
     * 消息内容 - 文本内容或富文本描述
     */
    @Column(columnDefinition = "TEXT", length = 10000)
    private String content;

    /** 
     * 消息内容摘要 - 用于预览显示
     */
    @Column(length = 200)
    private String contentSummary;

    /** 
     * 消息状态: 
     * SENDING(发送中), SENT(已发送), DELIVERED(已送达), 
     * READ(已读), FAILED(发送失败), RECALLED(已撤回)
     */
    @Column(nullable = false, length = 20)
    private String status = "SENDING";

    /** 
     * 发送序号 - 用于消息排序和去重
     */
    private Long sequence;

    // ========== 删除相关字段 ==========
    
    /** 发送者是否删除 */
    private Boolean senderDeleted = false;
    
    /** 接收者是否删除 */
    private Boolean receiverDeleted = false;
    
    /** 发送者删除时间 */
    private LocalDateTime senderDeletedAt;
    
    /** 接收者删除时间 */
    private LocalDateTime receiverDeletedAt;

    // ========== 撤回相关字段 ==========
    
    /** 是否已撤回 */
    private Boolean recalled = false;

    /** 撤回时间 */
    private LocalDateTime recalledAt;

    /** 撤回者ID */
    @Column(length = 36)
    private String recalledBy;

    // ========== 收藏相关字段 ==========
    
    /** 是否已收藏 */
    private Boolean favorited = false;

    /** 收藏时间 */
    private LocalDateTime favoritedAt;

    // ========== 置顶相关字段 ==========
    
    /** 是否置顶 */
    private Boolean pinned = false;

    /** 置顶时间 */
    private LocalDateTime pinnedAt;

    /** 置顶者ID */
    @Column(length = 36)
    private String pinnedBy;

    // ========== 引用/回复相关字段 ==========
    
    /** 引用消息ID */
    @Column(length = 36)
    private String replyToId;

    /** 引用消息发送者ID */
    @Column(length = 36)
    private String replyToSenderId;

    /** 引用消息内容摘要 */
    @Column(length = 200)
    private String replyToContentSummary;

    /** 根消息ID - 用于消息链 */
    @Column(length = 36)
    private String rootMessageId;

    // ========== 媒体/附件相关字段 ==========
    
    /** 附件信息 (JSON格式存储) */
    @Column(columnDefinition = "LONGTEXT")
    private String attachments;

    /** 附件数量 */
    private Integer attachmentCount = 0;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 文件类型 */
    @Column(length = 50)
    private String mimeType;

    /** 文件URL */
    @Column(length = 500)
    private String fileUrl;

    /** 缩略图URL */
    @Column(length = 500)
    private String thumbnailUrl;

    // ========== 扩展数据字段 ==========
    
    /** 扩展数据 (JSON格式存储) */
    @Column(columnDefinition = "LONGTEXT")
    private String extraData;

    /** 表情回应 (JSON格式存储) */
    @Column(columnDefinition = "TEXT")
    private String reactions;

    /** @提及的用户ID列表 (JSON数组) */
    @Column(columnDefinition = "TEXT")
    private String mentions;

    /** 是否全员@ */
    private Boolean mentionAll = false;

    // ========== 位置相关字段 ==========
    
    /** 位置信息 (JSON格式: {"latitude": xxx, "longitude": xxx, "address": "xxx"}) */
    @Column(columnDefinition = "TEXT")
    private String location;

    // ========== 已读相关字段 ==========
    
    /** 已读人数 - 群聊使用 */
    private Integer readCount = 0;

    /** 未读人数 - 群聊使用 */
    private Integer unreadCount = 0;

    /** 最后阅读时间 */
    private LocalDateTime lastReadAt;

    // ========== 安全/加密相关字段 ==========
    
    /** 是否加密消息 */
    private Boolean encrypted = false;

    /** 加密类型 */
    @Column(length = 20)
    private String encryptionType;

    /** 是否阅后即焚 */
    private Boolean selfDestruct = false;

    /** 阅后即焚倒计时(秒) */
    private Integer selfDestructTime;

    /** 是否已被销毁 */
    private Boolean destroyed = false;

    /** 销毁时间 */
    private LocalDateTime destroyedAt;

    // ========== 编辑相关字段 ==========
    
    /** 是否已编辑 */
    private Boolean edited = false;

    /** 编辑时间 */
    private LocalDateTime editedAt;

    /** 编辑前的内容 */
    @Column(columnDefinition = "TEXT")
    private String originalContent;

    // ========== 元数据字段 ==========
    
    /** 客户端消息ID - 用于去重 */
    @Column(length = 64)
    private String clientMessageId;

    /** 来源设备类型 */
    @Column(length = 20)
    private String deviceType;

    /** 来源IP地址 */
    @Column(length = 50)
    private String sourceIp;

    /** 创建时间 */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /** 发送时间 */
    private LocalDateTime sentAt;

    /** 送达时间 */
    private LocalDateTime deliveredAt;

    /** 已读时间 */
    private LocalDateTime readAt;

    // ========== 生命周期回调 ==========

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "SENDING";
        if (recalled == null) recalled = false;
        if (senderDeleted == null) senderDeleted = false;
        if (receiverDeleted == null) receiverDeleted = false;
        if (favorited == null) favorited = false;
        if (pinned == null) pinned = false;
        if (encrypted == null) encrypted = false;
        if (selfDestruct == null) selfDestruct = false;
        if (destroyed == null) destroyed = false;
        if (edited == null) edited = false;
        if (mentionAll == null) mentionAll = false;
        if (readCount == null) readCount = 0;
        if (unreadCount == null) unreadCount = 0;
        if (attachmentCount == null) attachmentCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        // updatedAt 由 @UpdateTimestamp 自动处理
    }

    // ========== 便捷方法 ==========

    /**
     * 标记消息为已发送
     */
    public void markAsSent() {
        this.status = "SENT";
        this.sentAt = LocalDateTime.now();
    }

    /**
     * 标记消息为已送达
     */
    public void markAsDelivered() {
        this.status = "DELIVERED";
        this.deliveredAt = LocalDateTime.now();
    }

    /**
     * 标记消息为已读
     */
    public void markAsRead() {
        this.status = "READ";
        this.readAt = LocalDateTime.now();
        this.lastReadAt = LocalDateTime.now();
    }

    /**
     * 撤回消息
     */
    public void recall(String userId) {
        this.recalled = true;
        this.recalledAt = LocalDateTime.now();
        this.recalledBy = userId;
        this.status = "RECALLED";
        this.content = "[消息已撤回]";
    }

    /**
     * 判断消息是否可以被撤回
     * @param userId 操作用户ID
     * @param recallTimeoutMinutes 撤回超时时间(分钟)
     * @return 是否可以撤回
     */
    public boolean canRecall(String userId, int recallTimeoutMinutes) {
        if (this.recalled) return false;
        if (!this.senderId.equals(userId)) return false;
        if (this.createdAt.plusMinutes(recallTimeoutMinutes).isBefore(LocalDateTime.now())) return false;
        return true;
    }

    /**
     * 判断用户是否可以查看此消息
     */
    public boolean canView(String userId) {
        if (this.destroyed) return false;
        if (this.selfDestruct && this.senderId.equals(userId) && this.destroyedAt != null) return false;
        return true;
    }
}
