package com.im.backend.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 群公告实体
 * 存储群组公告信息，支持发布、编辑、撤回、已读回执
 */
@Entity
@Table(name = "group_announcements", indexes = {
    @Index(name = "idx_group_id", columnList = "groupId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class GroupAnnouncement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 群组ID
     */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /**
     * 发布者ID
     */
    @Column(name = "publisher_id", nullable = false)
    private Long publisherId;

    /**
     * 公告标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 公告内容
     */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /**
     * 公告状态：ACTIVE-生效中, WITHDRAWN-已撤回, EXPIRED-已过期
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AnnouncementStatus status = AnnouncementStatus.ACTIVE;

    /**
     * 公告类型：NORMAL-普通, IMPORTANT-重要, PINNED-置顶
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "announcement_type", nullable = false, length = 20)
    private AnnouncementType announcementType = AnnouncementType.NORMAL;

    /**
     * 是否允许评论
     */
    @Column(name = "allow_comment", nullable = false)
    private Boolean allowComment = false;

    /**
     * 阅读人数统计
     */
    @Column(name = "read_count", nullable = false)
    private Integer readCount = 0;

    /**
     * 确认人数统计
     */
    @Column(name = "confirm_count", nullable = false)
    private Integer confirmCount = 0;

    /**
     * 需要确认的成员数
     */
    @Column(name = "need_confirm_count", nullable = false)
    private Integer needConfirmCount = 0;

    /**
     * 群组成员总数
     */
    @Column(name = "total_members", nullable = false)
    private Integer totalMembers = 0;

    /**
     * 附件列表（JSON格式存储）
     */
    @Column(name = "attachments", columnDefinition = "TEXT")
    private String attachments;

    /**
     * 撤回时间
     */
    @Column(name = "withdrawn_at")
    private LocalDateTime withdrawnAt;

    /**
     * 撤回操作人
     */
    @Column(name = "withdrawn_by")
    private Long withdrawnBy;

    /**
     * 撤回原因
     */
    @Column(name = "withdraw_reason", length = 500)
    private String withdrawReason;

    /**
     * 生效开始时间
     */
    @Column(name = "effective_start")
    private LocalDateTime effectiveStart;

    /**
     * 生效结束时间
     */
    @Column(name = "effective_end")
    private LocalDateTime effectiveEnd;

    /**
     * 是否发送通知
     */
    @Column(name = "send_notification", nullable = false)
    private Boolean sendNotification = true;

    /**
     * 通知范围：ALL-全体成员, ADMIN-仅管理员, PARTIAL-部分成员
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_scope", length = 20)
    private NotificationScope notificationScope = NotificationScope.ALL;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 已读记录（非数据库字段，用于查询）
     */
    @Transient
    private Set<Long> readUserIds = new HashSet<>();

    /**
     * 确认记录（非数据库字段，用于查询）
     */
    @Transient
    private Set<Long> confirmedUserIds = new HashSet<>();

    /**
     * 公告状态枚举
     */
    public enum AnnouncementStatus {
        ACTIVE,      // 生效中
        WITHDRAWN,   // 已撤回
        EXPIRED      // 已过期
    }

    /**
     * 公告类型枚举
     */
    public enum AnnouncementType {
        NORMAL,      // 普通公告
        IMPORTANT,   // 重要公告
        PINNED       // 置顶公告
    }

    /**
     * 通知范围枚举
     */
    public enum NotificationScope {
        ALL,         // 全体成员
        ADMIN,       // 仅管理员
        PARTIAL      // 部分成员
    }

    // 构造函数
    public GroupAnnouncement() {}

    public GroupAnnouncement(Long groupId, Long publisherId, String title, String content) {
        this.groupId = groupId;
        this.publisherId = publisherId;
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public AnnouncementStatus getStatus() {
        return status;
    }

    public void setStatus(AnnouncementStatus status) {
        this.status = status;
    }

    public AnnouncementType getAnnouncementType() {
        return announcementType;
    }

    public void setAnnouncementType(AnnouncementType announcementType) {
        this.announcementType = announcementType;
    }

    public Boolean getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Boolean allowComment) {
        this.allowComment = allowComment;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Integer getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(Integer confirmCount) {
        this.confirmCount = confirmCount;
    }

    public Integer getNeedConfirmCount() {
        return needConfirmCount;
    }

    public void setNeedConfirmCount(Integer needConfirmCount) {
        this.needConfirmCount = needConfirmCount;
    }

    public Integer getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Integer totalMembers) {
        this.totalMembers = totalMembers;
    }

    public String getAttachments() {
        return attachments;
    }

    public void setAttachments(String attachments) {
        this.attachments = attachments;
    }

    public LocalDateTime getWithdrawnAt() {
        return withdrawnAt;
    }

    public void setWithdrawnAt(LocalDateTime withdrawnAt) {
        this.withdrawnAt = withdrawnAt;
    }

    public Long getWithdrawnBy() {
        return withdrawnBy;
    }

    public void setWithdrawnBy(Long withdrawnBy) {
        this.withdrawnBy = withdrawnBy;
    }

    public String getWithdrawReason() {
        return withdrawReason;
    }

    public void setWithdrawReason(String withdrawReason) {
        this.withdrawReason = withdrawReason;
    }

    public LocalDateTime getEffectiveStart() {
        return effectiveStart;
    }

    public void setEffectiveStart(LocalDateTime effectiveStart) {
        this.effectiveStart = effectiveStart;
    }

    public LocalDateTime getEffectiveEnd() {
        return effectiveEnd;
    }

    public void setEffectiveEnd(LocalDateTime effectiveEnd) {
        this.effectiveEnd = effectiveEnd;
    }

    public Boolean getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public NotificationScope getNotificationScope() {
        return notificationScope;
    }

    public void setNotificationScope(NotificationScope notificationScope) {
        this.notificationScope = notificationScope;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Long> getReadUserIds() {
        return readUserIds;
    }

    public void setReadUserIds(Set<Long> readUserIds) {
        this.readUserIds = readUserIds;
    }

    public Set<Long> getConfirmedUserIds() {
        return confirmedUserIds;
    }

    public void setConfirmedUserIds(Set<Long> confirmedUserIds) {
        this.confirmedUserIds = confirmedUserIds;
    }

    /**
     * 检查公告是否已撤回
     */
    public boolean isWithdrawn() {
        return status == AnnouncementStatus.WITHDRAWN;
    }

    /**
     * 检查公告是否已过期
     */
    public boolean isExpired() {
        if (effectiveEnd == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(effectiveEnd);
    }

    /**
     * 检查公告是否生效中
     */
    public boolean isActive() {
        if (status != AnnouncementStatus.ACTIVE) {
            return false;
        }
        if (effectiveStart != null && LocalDateTime.now().isBefore(effectiveStart)) {
            return false;
        }
        if (effectiveEnd != null && LocalDateTime.now().isAfter(effectiveEnd)) {
            return false;
        }
        return true;
    }

    /**
     * 获取阅读率
     */
    public double getReadRate() {
        if (totalMembers == null || totalMembers == 0) {
            return 0.0;
        }
        return (double) readCount / totalMembers * 100;
    }

    /**
     * 获取确认率
     */
    public double getConfirmRate() {
        if (needConfirmCount == null || needConfirmCount == 0) {
            return 0.0;
        }
        return (double) confirmCount / needConfirmCount * 100;
    }

    @Override
    public String toString() {
        return "GroupAnnouncement{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", publisherId=" + publisherId +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", announcementType=" + announcementType +
                ", readCount=" + readCount +
                ", createdAt=" + createdAt +
                '}';
    }
}
