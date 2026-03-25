package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 频道话题实体 - 频道内的二级话题/主题讨论串
 */
@Entity
@Table(name = "im_channel_topic",
       indexes = {
           @Index(name = "idx_topic_channel", columnList = "channelId"),
           @Index(name = "idx_topic_parent", columnList = "parentTopicId"),
           @Index(name = "idx_topic_author", columnList = "authorId"),
           @Index(name = "idx_topic_sort", columnList = "channelId, sortOrder"),
           @Index(name = "idx_topic_created", columnList = "createdAt")
       })
public class ChannelTopicEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 话题UUID */
    @Column(nullable = false, unique = true, length = 36)
    private String topicId;

    /** 所属频道ID */
    @Column(nullable = false, length = 36)
    private String channelId;

    /** 话题标题 */
    @Column(nullable = false, length = 200)
    private String title;

    /** 话题内容/描述 */
    @Column(columnDefinition = "TEXT")
    private String content;

    /** 父话题ID (用于嵌套回复) */
    @Column(length = 36)
    private String parentTopicId;

    /** 根话题ID (顶级话题) */
    @Column(length = 36)
    private String rootTopicId;

    /** 话题深度 (0=根话题) */
    @Column(nullable = false)
    private Integer depth;

    /** 排序顺序 */
    @Column(nullable = false)
    private Integer sortOrder;

    /** 回复数 */
    @Column(nullable = false)
    private Long replyCount;

    /** 最后回复时间 */
    @Column
    private LocalDateTime lastReplyAt;

    /** 作者用户ID */
    @Column(nullable = false, length = 36)
    private String authorId;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column
    private LocalDateTime updatedAt;

    /** 是否置顶 */
    @Column(nullable = false)
    private Boolean isPinned;

    /** 是否锁定 (禁止新回复) */
    @Column(nullable = false)
    private Boolean isLocked;

    /** 状态: OPEN=开放, CLOSED=关闭, ARCHIVED=归档, DELETED=已删除 */
    @Column(nullable = false, length = 20)
    private String status;

    /** 标签列表 (JSON数组) */
    @Column(length = 500)
    private String tags;

    /** 视图数 */
    @Column(nullable = false)
    private Long viewCount;

    /** 表情反应统计 (JSON: {emoji: count}) */
    @Column(columnDefinition = "TEXT")
    private String reactions;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTopicId() { return topicId; }
    public void setTopicId(String topicId) { this.topicId = topicId; }

    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getParentTopicId() { return parentTopicId; }
    public void setParentTopicId(String parentTopicId) { this.parentTopicId = parentTopicId; }

    public String getRootTopicId() { return rootTopicId; }
    public void setRootTopicId(String rootTopicId) { this.rootTopicId = rootTopicId; }

    public Integer getDepth() { return depth; }
    public void setDepth(Integer depth) { this.depth = depth; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Long getReplyCount() { return replyCount; }
    public void setReplyCount(Long replyCount) { this.replyCount = replyCount; }

    public LocalDateTime getLastReplyAt() { return lastReplyAt; }
    public void setLastReplyAt(LocalDateTime lastReplyAt) { this.lastReplyAt = lastReplyAt; }

    public String getAuthorId() { return authorId; }
    public void setAuthorId(String authorId) { this.authorId = authorId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Boolean getIsPinned() { return isPinned; }
    public void setIsPinned(Boolean isPinned) { this.isPinned = isPinned; }

    public Boolean getIsLocked() { return isLocked; }
    public void setIsLocked(Boolean isLocked) { this.isLocked = isLocked; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }

    public String getReactions() { return reactions; }
    public void setReactions(String reactions) { this.reactions = reactions; }
}
