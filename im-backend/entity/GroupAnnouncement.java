package com.im.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 群公告实体类
 * 功能ID: #30
 * 功能名称: 群公告
 * 
 * @author developer-agent
 * @since 2026-03-30
 */
@Entity
@Table(name = "group_announcement", indexes = {
    @Index(name = "idx_group_id", columnList = "groupId"),
    @Index(name = "idx_creator_id", columnList = "creatorId"),
    @Index(name = "idx_created_at", columnList = "createdAt"),
    @Index(name = "idx_pinned", columnList = "groupId,pinned")
})
public class GroupAnnouncement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 群组ID */
    @Column(name = "group_id", nullable = false)
    private Long groupId;

    /** 公告标题 */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /** 公告内容 */
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 创建者ID */
    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    /** 是否置顶 */
    @Column(name = "pinned", nullable = false)
    private Boolean pinned = false;

    /** 置顶时间 */
    @Column(name = "pinned_at")
    private LocalDateTime pinnedAt;

    /** 是否已确认 (所有人都已读) */
    @Column(name = "confirmed", nullable = false)
    private Boolean confirmed = false;

    /** 确认人数 */
    @Column(name = "read_count", nullable = false)
    private Integer readCount = 0;

    /** 群组总人数 (用于计算已读比例) */
    @Column(name = "total_members", nullable = false)
    private Integer totalMembers = 0;

    /** 附件URL列表 (逗号分隔) */
    @Column(name = "attachments", length = 2000)
    private String attachments;

    /** 创建时间 */
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** 是否删除 */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    /** 删除时间 */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** 已读用户ID集合 (JSON存储) */
    @Column(name = "read_user_ids", columnDefinition = "TEXT")
    private String readUserIds = "[]";

    // 构造函数
    public GroupAnnouncement() {}

    public GroupAnnouncement(Long groupId, String title, String content, Long creatorId) {
        this.groupId = groupId;
        this.title = title;
        this.content = content;
        this.creatorId = creatorId;
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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
        if (pinned != null && pinned) {
            this.pinnedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(LocalDateTime pinnedAt) {
        this.pinnedAt = pinnedAt;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
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

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
        if (deleted != null && deleted) {
            this.deletedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getReadUserIds() {
        return readUserIds;
    }

    public void setReadUserIds(String readUserIds) {
        this.readUserIds = readUserIds;
    }

    // 业务方法
    /**
     * 增加已读人数
     */
    public void incrementReadCount() {
        if (this.readCount == null) {
            this.readCount = 0;
        }
        this.readCount++;
        if (this.totalMembers > 0 && this.readCount >= this.totalMembers) {
            this.confirmed = true;
        }
    }

    /**
     * 计算已读百分比
     */
    public double getReadPercentage() {
        if (totalMembers == null || totalMembers == 0) {
            return 0.0;
        }
        return (double) readCount / totalMembers * 100;
    }

    @Override
    public String toString() {
        return "GroupAnnouncement{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", title='" + title + '\'' +
                ", creatorId=" + creatorId +
                ", pinned=" + pinned +
                ", readCount=" + readCount +
                ", totalMembers=" + totalMembers +
                ", createdAt=" + createdAt +
                ", confirmed=" + confirmed +
                '}';
    }
}
