package com.im.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息草稿跨设备同步实体
 * 存储用户在所有设备上的消息草稿，实现实时同步
 */
@Entity
@Table(name = "message_draft")
@Data
public class MessageDraftEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 草稿所属用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 设备唯一标识
     */
    @Column(name = "device_id", length = 128)
    private String deviceId;
    
    /**
     * 草稿对应的会话ID（群聊/私聊/频道等）
     */
    @Column(name = "conversation_id", nullable = false)
    private String conversationId;
    
    /**
     * 会话类型：PRIVATE/GROUP/CHANNEL/TOPIC
     */
    @Column(name = "conversation_type", length = 32)
    private String conversationType;
    
    /**
     * 草稿内容（纯文本或富文本）
     */
    @Column(name = "draft_content", columnDefinition = "TEXT")
    private String draftContent;
    
    /**
     * 草稿类型：TEXT/VOICE/IMAGE/VIDEO/COMPOSITE
     */
    @Column(name = "draft_type", length = 32)
    private String draftType = "TEXT";
    
    /**
     * 草稿中引用的消息ID（回复引用）
     */
    @Column(name = "reply_to_message_id")
    private String replyToMessageId;
    
    /**
     * 草稿中的附件列表（JSON格式）
     */
    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;
    
    /**
     * 草稿中的提及列表（JSON格式）
     */
    @Column(name = "mentions", columnDefinition = "TEXT")
    private String mentions;
    
    /**
     * 草稿的本地版本号（用于冲突解决）
     */
    @Column(name = "local_version")
    private Long localVersion = 0L;
    
    /**
     * 草稿的服务端版本号（用于同步）
     */
    @Column(name = "server_version")
    private Long serverVersion = 0L;
    
    /**
     * 草稿最后更新时间
     */
    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;
    
    /**
     * 草稿同步状态：PENDING/SYNCING/SYNCED/CONFLICT
     */
    @Column(name = "sync_status", length = 32)
    private String syncStatus = "PENDING";
    
    /**
     * 草稿冲突信息（JSON格式）
     */
    @Column(name = "conflict_info", columnDefinition = "TEXT")
    private String conflictInfo;
    
    /**
     * 是否自动保存（true）还是手动保存（false）
     */
    @Column(name = "is_auto_save")
    private Boolean autoSave = false;
    
    /**
     * 草稿创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 草稿是否已被清空（用户清空了输入框）
     */
    @Column(name = "is_cleared")
    private Boolean cleared = false;
    
    /**
     * 草稿在设备上的光标位置
     */
    @Column(name = "cursor_position")
    private Integer cursorPosition = 0;
    
    /**
     * 草稿的选择范围（JSON格式）
     */
    @Column(name = "selection_range")
    private String selectionRange;
    
    /**
     * 草稿的语言（用于AI输入建议）
     */
    @Column(name = "language", length = 16)
    private String language;
    
    /**
     * 草稿的输入法状态（JSON格式）
     */
    @Column(name = "ime_state")
    private String imeState;
    
    /**
     * 是否为活跃草稿（设备当前正在编辑）
     */
    @Column(name = "is_active")
    private Boolean active = false;
    
    /**
     * 草稿的上下文信息（JSON格式）
     */
    @Column(name = "context_info", columnDefinition = "TEXT")
    private String contextInfo;
    
    /**
     * 草稿的元数据（JSON格式）
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastUpdatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = LocalDateTime.now();
        // 自动增加版本号
        if (serverVersion == null) {
            serverVersion = 0L;
        }
        serverVersion++;
    }
    
    /**
     * 同步操作类型枚举
     */
    public enum SyncOperation {
        CREATE, UPDATE, DELETE, CONFLICT_RESOLUTION
    }
    
    /**
     * 同步状态枚举
     */
    public enum SyncStatus {
        PENDING,        // 等待同步
        SYNCING,        // 正在同步
        SYNCED,         // 已同步
        CONFLICT,       // 存在冲突
        ERROR           // 同步错误
    }
    
    /**
     * 草稿类型枚举
     */
    public enum DraftType {
        TEXT,           // 文本草稿
        VOICE,          // 语音草稿
        IMAGE,          // 图片草稿
        VIDEO,          // 视频草稿
        COMPOSITE       // 复合类型草稿
    }
    
    /**
     * 会话类型枚举
     */
    public enum ConversationType {
        PRIVATE,        // 私聊
        GROUP,          // 群聊
        CHANNEL,        // 频道
        TOPIC           // 话题
    }
}