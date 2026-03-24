package com.im.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    
    public enum NotificationType {
        MESSAGE,          // 新消息
        GROUP_INVITE,     // 群组邀请
        FRIEND_REQUEST,   // 好友请求
        MENTION,          // @提及
        REACTION,         // 消息回应
        VOICE_CALL,       // 语音通话
        VIDEO_CALL,       // 视频通话
        SYSTEM_ALERT,     // 系统通知
        GROUP_EVENT,      // 群组活动
        VOTE_RESULT,      // 投票结果
        FILE_SHARED,      // 文件共享
        BOT_MESSAGE       // 机器人消息
    }
    
    public enum NotificationStatus {
        UNREAD,           // 未读
        READ,             // 已读
        ARCHIVED,         // 已归档
        DISMISSED         // 已忽略
    }
    
    public enum NotificationPriority {
        HIGH,             // 高优先级（消息、@提及）
        MEDIUM,           // 中优先级（群组活动）
        LOW               // 低优先级（系统通知）
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "sender_id")
    private Long senderId;
    
    @Column(name = "sender_name")
    private String senderName;
    
    @Column(name = "sender_avatar")
    private String senderAvatar;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatus status = NotificationStatus.UNREAD;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private NotificationPriority priority = NotificationPriority.MEDIUM;
    
    @Column(name = "title", nullable = false, length = 500)
    private String title;
    
    @Column(name = "content", length = 2000)
    private String content;
    
    @Column(name = "data", length = 4000)
    private String data; // JSON格式的额外数据
    
    @Column(name = "group_id")
    private Long groupId;
    
    @Column(name = "message_id")
    private Long messageId;
    
    @Column(name = "channel")
    private String channel; // 通知渠道：app, email, push, sms
    
    @Column(name = "is_muted", nullable = false)
    private Boolean isMuted = false;
    
    @Column(name = "is_actionable", nullable = false)
    private Boolean isActionable = true;
    
    @Column(name = "action_url")
    private String actionUrl;
    
    @Column(name = "action_label")
    private String actionLabel;
    
    @Column(name = "expire_at")
    private LocalDateTime expireAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "archived_at")
    private LocalDateTime archivedAt;
    
    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public String getSenderAvatar() { return senderAvatar; }
    public void setSenderAvatar(String senderAvatar) { this.senderAvatar = senderAvatar; }
    
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    
    public NotificationStatus getStatus() { return status; }
    public void setStatus(NotificationStatus status) { this.status = status; }
    
    public NotificationPriority getPriority() { return priority; }
    public void setPriority(NotificationPriority priority) { this.priority = priority; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    
    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    
    public Boolean getIsMuted() { return isMuted; }
    public void setIsMuted(Boolean isMuted) { this.isMuted = isMuted; }
    
    public Boolean getIsActionable() { return isActionable; }
    public void setIsActionable(Boolean isActionable) { this.isActionable = isActionable; }
    
    public String getActionUrl() { return actionUrl; }
    public void setActionUrl(String actionUrl) { this.actionUrl = actionUrl; }
    
    public String getActionLabel() { return actionLabel; }
    public void setActionLabel(String actionLabel) { this.actionLabel = actionLabel; }
    
    public LocalDateTime getExpireAt() { return expireAt; }
    public void setExpireAt(LocalDateTime expireAt) { this.expireAt = expireAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    public LocalDateTime getArchivedAt() { return archivedAt; }
    public void setArchivedAt(LocalDateTime archivedAt) { this.archivedAt = archivedAt; }
    
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public void setDeliveredAt(LocalDateTime deliveredAt) { this.deliveredAt = deliveredAt; }
    
    // Helper methods
    public boolean isUnread() {
        return status == NotificationStatus.UNREAD;
    }
    
    public boolean isHighPriority() {
        return priority == NotificationPriority.HIGH;
    }
    
    public boolean isExpired() {
        return expireAt != null && LocalDateTime.now().isAfter(expireAt);
    }
    
    public void markAsRead() {
        this.status = NotificationStatus.READ;
        this.readAt = LocalDateTime.now();
    }
    
    public void markAsArchived() {
        this.status = NotificationStatus.ARCHIVED;
        this.archivedAt = LocalDateTime.now();
    }
}