package com.im.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 用户好友分组成员关系实体
 * 记录好友与分组的关联关系
 */
@Entity
@Table(name = "user_friend_group_members", indexes = {
    @Index(name = "idx_group_friend", columnList = "group_id, friend_id", unique = true),
    @Index(name = "idx_friend_user", columnList = "friend_id, user_id"),
    @Index(name = "idx_group_sort", columnList = "group_id, sort_order")
}, uniqueConstraints = {
    @UniqueConstraint(columnNames = {"group_id", "friend_id"}, name = "uk_group_friend")
})
public class UserFriendGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "分组ID不能为空")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private UserFriendGroup group;

    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "好友ID不能为空")
    @Column(name = "friend_id", nullable = false)
    private Long friendId;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "is_starred")
    private Boolean isStarred = false;

    @Column(name = "is_muted")
    private Boolean isMuted = false;

    @Column(name = "remark")
    private String remark;

    @Column(name = "added_at", nullable = false, updatable = false)
    private LocalDateTime addedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        addedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (isStarred == null) {
            isStarred = false;
        }
        if (isMuted == null) {
            isMuted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserFriendGroup getGroup() {
        return group;
    }

    public void setGroup(UserFriendGroup group) {
        this.group = group;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(Boolean isStarred) {
        this.isStarred = isStarred;
    }

    public Boolean getIsMuted() {
        return isMuted;
    }

    public void setIsMuted(Boolean isMuted) {
        this.isMuted = isMuted;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(LocalDateTime addedAt) {
        this.addedAt = addedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFriendGroupMember that = (UserFriendGroupMember) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UserFriendGroupMember{" +
                "id=" + id +
                ", userId=" + userId +
                ", friendId=" + friendId +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
