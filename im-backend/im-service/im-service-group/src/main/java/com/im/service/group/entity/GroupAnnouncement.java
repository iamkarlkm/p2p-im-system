package com.im.service.group.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 群公告实体
 * 对应数据库表: im_group_announcement
 * 
 * @author IM Team
 * @version 1.0
 */
@Data
@Entity
@Table(name = "im_group_announcement", indexes = {
    @Index(name = "idx_announcement_group", columnList = "groupId"),
    @Index(name = "idx_announcement_creator", columnList = "creatorId"),
    @Index(name = "idx_announcement_pinned", columnList = "isPinned"),
    @Index(name = "idx_announcement_created", columnList = "createdAt")
})
public class GroupAnnouncement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    /** 群组ID */
    @Column(nullable = false, length = 36)
    private String groupId;

    /** 创建者ID */
    @Column(nullable = false, length = 36)
    private String creatorId;

    /** 公告标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 公告内容 */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 是否置顶 */
    @Column(nullable = false)
    private Boolean isPinned = false;

    /** 置顶时间 */
    private LocalDateTime pinnedAt;

    /** 置顶者ID */
    @Column(length = 36)
    private String pinnedBy;

    /** 阅读次数 */
    @Column(nullable = false)
    private Integer readCount = 0;

    /** 是否已删除 */
    @Column(nullable = false)
    private Boolean deleted = false;

    /** 创建时间 */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * 增加阅读次数
     */
    public void incrementReadCount() {
        this.readCount = (this.readCount == null ? 0 : this.readCount) + 1;
    }

    /**
     * 置顶公告
     */
    public void pin(String pinnedBy) {
        this.isPinned = true;
        this.pinnedAt = LocalDateTime.now();
        this.pinnedBy = pinnedBy;
    }

    /**
     * 取消置顶
     */
    public void unpin() {
        this.isPinned = false;
        this.pinnedAt = null;
        this.pinnedBy = null;
    }

    /**
     * 标记删除
     */
    public void markAsDeleted() {
        this.deleted = true;
    }
}
