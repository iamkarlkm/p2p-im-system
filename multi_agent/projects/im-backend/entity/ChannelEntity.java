package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 频道实体 - 用于大型群组的频道划分
 * 频道是群组的顶级分类，每个群组可有多个频道
 */
@Entity
@Table(name = "im_channel",
       indexes = {
           @Index(name = "idx_channel_group", columnList = "groupId"),
           @Index(name = "idx_channel_sort", columnList = "groupId, sortOrder"),
           @Index(name = "idx_channel_type", columnList = "channelType"),
           @Index(name = "idx_channel_creator", columnList = "createdBy")
       })
public class ChannelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 频道UUID */
    @Column(nullable = false, unique = true, length = 36)
    private String channelId;

    /** 所属群组ID */
    @Column(nullable = false, length = 36)
    private String groupId;

    /** 频道名称 */
    @Column(nullable = false, length = 100)
    private String name;

    /** 频道描述 */
    @Column(length = 500)
    private String description;

    /** 频道类型: ANNOUNCEMENT=公告, DISCUSSION=讨论, GENERAL=综合, SUPPORT=支持 */
    @Column(nullable = false, length = 20)
    private String channelType;

    /** 排序顺序 */
    @Column(nullable = false)
    private Integer sortOrder;

    /** 是否公开频道 */
    @Column(nullable = false)
    private Boolean isPublic;

    /** 所需角色: ANY=任何人, MEMBER=成员, MODERATOR=版主, ADMIN=管理员 */
    @Column(nullable = false, length = 20)
    private String requiredRole;

    /** 消息数统计 */
    @Column(nullable = false)
    private Long messageCount;

    /** 最后消息时间 */
    @Column
    private LocalDateTime lastMessageAt;

    /** 创建者用户ID */
    @Column(nullable = false, length = 36)
    private String createdBy;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column
    private LocalDateTime updatedAt;

    /** 状态: ACTIVE=活跃, ARCHIVED=归档, DELETED=已删除 */
    @Column(nullable = false, length = 20)
    private String status;

    /** 父频道ID (用于嵌套频道) */
    @Column(length = 36)
    private String parentChannelId;

    /** 频道图标/emoji */
    @Column(length = 50)
    private String icon;

    /** 同步到成员: NEW_MEMBER=新成员自动加入 */
    @Column(length = 20)
    private String syncPolicy;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getChannelId() { return channelId; }
    public void setChannelId(String channelId) { this.channelId = channelId; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getChannelType() { return channelType; }
    public void setChannelType(String channelType) { this.channelType = channelType; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public String getRequiredRole() { return requiredRole; }
    public void setRequiredRole(String requiredRole) { this.requiredRole = requiredRole; }

    public Long getMessageCount() { return messageCount; }
    public void setMessageCount(Long messageCount) { this.messageCount = messageCount; }

    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getParentChannelId() { return parentChannelId; }
    public void setParentChannelId(String parentChannelId) { this.parentChannelId = parentChannelId; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getSyncPolicy() { return syncPolicy; }
    public void setSyncPolicy(String syncPolicy) { this.syncPolicy = syncPolicy; }
}
