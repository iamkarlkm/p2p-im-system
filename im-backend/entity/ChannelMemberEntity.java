package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 频道成员实体
 * 控制用户对频道的访问权限
 */
@Entity
@Table(name = "im_channel_member",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_channel_user", columnNames = {"channelId", "userId"})
       },
       indexes = {
           @Index(name = "idx_cm_channel", columnList = "channelId"),
           @Index(name = "idx_cm_user", columnList = "userId"),
           @Index(name = "idx_cm_role", columnList = "role"),
           @Index(name = "idx_cm_joined", columnList = "joinedAt")
       })
public class ChannelMemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 成员记录ID */
    @Column(nullable = false, unique = true, length = 36)
    private String memberId;

    /** 频道ID */
    @Column(nullable = false, length = 36)
    private String channelId;

    /** 用户ID */
    @Column(nullable = false, length = 36)
    private String userId;

    /** 角色: OWNER=所有者, ADMIN=管理员, MODERATOR=版主, MEMBER=普通成员, GUEST=访客 */
    @Column(nullable = false, length = 20)
    private String role;

    /** 是否已读通知 */
    @Column(nullable = false)
    private Boolean notificationsEnabled;

    /** 免打扰级别: ALL=所有消息, MENTIONS=仅@提及, NONE=无 */
    @Column(nullable = false, length = 20)
    private String notificationLevel;

    /** 加入时间 */
    @Column(nullable = false)
    private LocalDateTime joinedAt;

    /** 加入方式: INVITE=邀请, JOIN=自己加入, AUTO=自动加入 */
    @Column(length = 20)
    private String joinMethod;

    /** 邀请人用户ID */
    @Column(length = 36)
    private String invitedBy;

    /** 最后访问时间 */
    @Column
    private LocalDateTime lastReadAt;

    /** 未读消息数 */
    @Column(nullable = false)
    private Long unreadCount;

    /** 用户名/昵称 (冗余存储) */
    @Column(length = 100)
    private String displayName;

    /** 状态: ACTIVE=活跃, MUTED=静音, BLOCKED=已屏蔽 */
    @Column(nullable = false, length = 20)
    private String status;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }

    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(Boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

    public String getNotificationLevel() { return notificationLevel; }
    public void setNotificationLevel(String notificationLevel) { this.notificationLevel = notificationLevel; }

    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }

    public String getJoinMethod() { return joinMethod; }
    public void setJoinMethod(String joinMethod) { this.joinMethod = joinMethod; }

    public String getInvitedBy() { return invitedBy; }
    public void setInvitedBy(String invitedBy) { this.invitedBy = invitedBy; }

    public LocalDateTime getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(LocalDateTime lastReadAt) { this.lastReadAt = lastReadAt; }

    public Long getUnreadCount() { return unreadCount; }
    public void setUnreadCount(Long unreadCount) { this.unreadCount = unreadCount; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
