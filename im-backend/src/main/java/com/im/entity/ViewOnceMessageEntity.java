package com.im.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 一次性媒体消息实体
 * 图片/语音/视频查看即销毁
 */
@Data
@Entity
@Table(name = "view_once_messages", indexes = {
    @Index(name = "idx_message_id", columnList = "messageId"),
    @Index(name = "idx_conversation_id", columnList = "conversationId"),
    @Index(name = "idx_sender_id", columnList = "senderId"),
    @Index(name = "idx_receiver_id", columnList = "receiverId"),
    @Index(name = "idx_expire_at", columnList = "expireAt")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewOnceMessageEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 关联的消息ID
     */
    @Column(nullable = false, unique = true, length = 64)
    private String messageId;
    
    /**
     * 会话ID
     */
    @Column(nullable = false, length = 64)
    private String conversationId;
    
    /**
     * 发送者ID
     */
    @Column(nullable = false, length = 64)
    private String senderId;
    
    /**
     * 接收者ID
     */
    @Column(nullable = false, length = 64)
    private String receiverId;
    
    /**
     * 媒体类型: IMAGE, VOICE, VIDEO
     */
    @Column(nullable = false, length = 16)
    private String mediaType;
    
    /**
     * 原始媒体文件URL
     */
    @Column(length = 512)
    private String mediaUrl;
    
    /**
     * 媒体文件大小（字节）
     */
    private Long mediaSize;
    
    /**
     * 媒体MIME类型
     */
    @Column(length = 64)
    private String mimeType;
    
    /**
     * 媒体加密密钥（Base64编码）
     */
    @Column(length = 256)
    private String encryptionKey;
    
    /**
     * 是否已查看
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean viewed = false;
    
    /**
     * 查看时间
     */
    private LocalDateTime viewedAt;
    
    /**
     * 查看者IP
     */
    @Column(length = 64)
    private String viewedByIp;
    
    /**
     * 查看者设备ID
     */
    @Column(length = 64)
    private String viewedByDeviceId;
    
    /**
     * 媒体是否已销毁
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean destroyed = false;
    
    /**
     * 销毁时间
     */
    private LocalDateTime destroyedAt;
    
    /**
     * 销毁原因: VIEWED, EXPIRED, MANUAL, SYSTEM
     */
    @Column(length = 16)
    private String destroyReason;
    
    /**
     * 过期时间（可选，用于临时一次性消息）
     */
    private LocalDateTime expireAt;
    
    /**
     * 是否启用截图检测
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean screenshotDetection = false;
    
    /**
     * 截图检测记录（JSON数组）
     */
    @Column(columnDefinition = "TEXT")
    private String screenshotRecords;
    
    /**
     * 元数据（JSON，存储媒体时长、尺寸等）
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 是否激活（未过期的有效消息）
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 标记为已查看
     */
    public void markAsViewed(String ip, String deviceId) {
        this.viewed = true;
        this.viewedAt = LocalDateTime.now();
        this.viewedByIp = ip;
        this.viewedByDeviceId = deviceId;
    }
    
    /**
     * 标记为已销毁
     */
    public void markAsDestroyed(String reason) {
        this.destroyed = true;
        this.destroyedAt = LocalDateTime.now();
        this.destroyReason = reason;
        this.active = false;
    }
    
    /**
     * 添加截图记录
     */
    public void addScreenshotRecord(String timestamp, String details) {
        // 简单的JSON数组追加实现
        String record = String.format("{\"timestamp\":\"%s\",\"details\":\"%s\"}", timestamp, details);
        if (this.screenshotRecords == null || this.screenshotRecords.isEmpty()) {
            this.screenshotRecords = "[" + record + "]";
        } else {
            this.screenshotRecords = this.screenshotRecords.substring(0, this.screenshotRecords.length() - 1) + "," + record + "]";
        }
    }
}
