package com.im.backend.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * 联系人好友分组实体
 * 支持自定义分组管理、分组内排序
 */
@Entity
@Table(name = "contact_group",
       indexes = {
           @Index(name = "idx_contact_group_user", columnList = "userId"),
           @Index(name = "idx_contact_group_name", columnList = "userId, groupName"),
           @Index(name = "idx_contact_group_created_at", columnList = "createdAt")
       })
public class ContactGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户 ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 分组名称
     */
    @Column(name = "group_name", nullable = false, length = 100)
    private String groupName;

    /**
     * 分组描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 分组图标
     */
    @Column(name = "icon", length = 200)
    private String icon;

    /**
     * 分组颜色（十六进制）
     */
    @Column(name = "color", length = 20)
    private String color;

    /**
     * 分组内好友数量
     */
    @Column(name = "contact_count")
    private Integer contactCount = 0;

    /**
     * 分组排序索引
     */
    @Column(name = "sort_index")
    private Integer sortIndex = 0;

    /**
     * 是否为默认分组（如"未分组"、"好友"等）
     */
    @Column(name = "is_default")
    private Boolean isDefault = false;

    /**
     * 是否隐藏空分组（无好友时自动隐藏）
     */
    @Column(name = "hide_if_empty")
    private Boolean hideIfEmpty = false;

    /**
     * 分组创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * 分组更新时间
     */
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // 构造器
    public ContactGroupEntity() {
    }

    public ContactGroupEntity(Long userId, String groupName, String description, String icon, String color) {
        this.userId = userId;
        this.groupName = groupName;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.contactCount = 0;
        this.sortIndex = 0;
        this.isDefault = false;
        this.hideIfEmpty = false;
    }

    // Getter 和 Setter
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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getContactCount() {
        return contactCount;
    }

    public void setContactCount(Integer contactCount) {
        this.contactCount = contactCount;
    }

    public Integer getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(Integer sortIndex) {
        this.sortIndex = sortIndex;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean getHideIfEmpty() {
        return hideIfEmpty;
    }

    public void setHideIfEmpty(Boolean hideIfEmpty) {
        this.hideIfEmpty = hideIfEmpty;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ContactGroupEntity that = (ContactGroupEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(userId, that.userId) 
               && Objects.equals(groupName, that.groupName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, groupName);
    }

    @Override
    public String toString() {
        return "ContactGroupEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", groupName='" + groupName + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                ", color='" + color + '\'' +
                ", contactCount=" + contactCount +
                ", sortIndex=" + sortIndex +
                ", isDefault=" + isDefault +
                ", hideIfEmpty=" + hideIfEmpty +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}