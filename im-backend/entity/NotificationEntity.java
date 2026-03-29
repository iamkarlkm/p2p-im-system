package com.im.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "im_notifications", indexes = {
    @Index(name = "idx_notification_user", columnList = "userId"),
    @Index(name = "idx_notification_read", columnList = "userId,isRead"),
    @Index(name = "idx_notification_type", columnList = "userId,type"),
    @Index(name = "idx_notification_created", columnList = "createdAt")
})
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 通知接收者用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 通知类型: SYSTEM, FRIEND_REQUEST, GROUP_INVITE, MESSAGE, VOTE, ANNOUNCEMENT, SECURITY */
    @Column(nullable = false, length = 32)
    private String type;

    /** 通知标题 */
    @Column(nullable = false, length = 128)
    private String title;

    /** 通知内容摘要 */
    @Column(length = 512)
    private String content;

    /** 关联实体类型: MESSAGE, USER, GROUP, VOTE */
    @Column(length = 32)
    private String refType;

    /** 关联实体ID */
    private Long refId;

    /** 关联会话ID */
    private Long conversationId;

    /** 发送者用户ID (可为系统) */
    private Long senderId;

    /** 发送者昵称 */
    @Column(length = 64)
    private String senderNickname;

    /** 发送者头像URL */
    @Column(length = 256)
    private String senderAvatar;

    /** 是否已读 */
    @Column(nullable = false)
    private Boolean isRead = false;

    /** 读取时间 */
    private LocalDateTime readAt;

    /** 是否已处理 (如: 好友请求已接受/拒绝) */
    @Column(nullable = false)
    private Boolean isHandled = false;

    /** 处理结果: ACCEPTED, REJECTED, null */
    @Column(length = 16)
    private String handleResult;

    /** 免打扰级别: ALL, PRIORITY_ONLY, NONE */
    @Column(nullable = false, length = 16)
    private String dndLevel = "NONE";

    /** 过期时间 (null=永不过期) */
    private LocalDateTime expiresAt;

    /** 扩展数据 (JSON字符串) */
    @Column(columnDefinition = "TEXT")
    private String extraData;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
