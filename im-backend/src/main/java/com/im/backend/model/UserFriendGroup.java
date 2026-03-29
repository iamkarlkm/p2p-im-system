package com.im.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * 用户好友分组实体
 * 用于管理用户的好友分组，如"家人"、"同事"、"朋友"等
 */
@Entity
@Table(name = "user_friend_groups", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_user_sort", columnList = "userId, sortOrder"),
    @Index(name = "idx_group_name", columnList = "userId, groupName")
})
public class UserFriendGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotBlank(message = "分组名称不能为空")
    @Size(max = 50, message = "分组名称不能超过50个字符")
    @Column(name = "group_name", nullable = false, length = 50)
    private String groupName;

    @Size(max = 200, message = "分组描述不能超过200个字符")
    @Column(name = "description", length = 200)
    private String description;

    @NotNull(message = "排序顺序不能为空")
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    @Column(name = "is_default")
    private Boolean isDefault = false;

    @Column(name = "member_count")
    private Integer memberCount = 0;

    @Column(name = "max_members")
    private Integer maxMembers = 500;

    @Size(max = 20, message = "分组颜色标识不能超过20个字符")
    @Column(name = "color_tag", length = 20)
    private String colorTag;

    @Size(max = 50, message = "分组图标不能超过50个字符")
    @Column(name = "icon", length = 50)
    private String icon;

    @Column(name = "is_visible")
    private Boolean isVisible = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, addedAt ASC")
    private Set<UserFriendGroupMember> members = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (sortOrder == null) {
            sortOrder = 0;
        }
        if (memberCount == null) {
            memberCount = 0;
        }
        if (isDefault == null) {
            isDefault = false;
        }
        if (isVisible == null) {
            isVisible = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 添加成员到分组
     */
    public void addMember(UserFriendGroupMember member) {
        members.add(member);
        member.setGroup(this);
        memberCount = members.size();
    }

    /**
     * 从分组移除成员
     */
    public void removeMember(UserFriendGroupMember member) {
        members.remove(member);
        member.setGroup(null);
        memberCount = members.size();
    }

    /**
     * 更新成员数量
     */
    public void updateMemberCount() {
        this.memberCount = members != null ? members.size() : 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public String getColorTag() {
        return colorTag;
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
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

    public Set<UserFriendGroupMember> getMembers() {
        return members;
    }

    public void setMembers(Set<UserFriendGroupMember> members) {
        this.members = members;
        updateMemberCount();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserFriendGroup that = (UserFriendGroup) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "UserFriendGroup{" +
                "id=" + id +
                ", userId=" + userId +
                ", groupName='" + groupName + '\'' +
                ", memberCount=" + memberCount +
                ", sortOrder=" + sortOrder +
                '}';
    }
}
