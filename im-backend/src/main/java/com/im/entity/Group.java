package com.im.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * 群组实体类
 * 功能 #5: 群组管理基础模块
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class Group implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ==================== 群组类型 ====================
    public enum GroupType {
        PUBLIC("公开群"),
        PRIVATE("私有群"),
        TEMPORARY("临时群");
        
        private final String description;
        
        GroupType(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // ==================== 核心字段 ====================
    private String groupId;
    private String groupName;
    private String description;
    private String avatar;
    private String ownerId;
    private GroupType type;
    private Integer maxMembers;
    private Integer currentMembers;
    private Set<String> adminIds;
    private String announcement;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean muteAll;
    private Boolean inviteConfirm;
    
    // ==================== 构造函数 ====================
    public Group() {
        this.type = GroupType.PUBLIC;
        this.maxMembers = 500;
        this.currentMembers = 1;
        this.muteAll = false;
        this.inviteConfirm = true;
        this.createTime = LocalDateTime.now();
    }
    
    // ==================== 业务方法 ====================
    
    public boolean isFull() {
        return currentMembers >= maxMembers;
    }
    
    public boolean isOwner(String userId) {
        return ownerId.equals(userId);
    }
    
    public boolean isAdmin(String userId) {
        return adminIds != null && adminIds.contains(userId);
    }
    
    public boolean hasPermission(String userId) {
        return isOwner(userId) || isAdmin(userId);
    }
    
    public void addMember() {
        if (!isFull()) {
            this.currentMembers++;
        }
    }
    
    public void removeMember() {
        if (currentMembers > 0) {
            this.currentMembers--;
        }
    }
    
    // ==================== Getter & Setter ====================
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    
    public String getGroupName() { return groupName; }
    public void setGroupName(String groupName) { this.groupName = groupName; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }
    
    public GroupType getType() { return type; }
    public void setType(GroupType type) { this.type = type; }
    
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    
    public Integer getCurrentMembers() { return currentMembers; }
    public void setCurrentMembers(Integer currentMembers) { this.currentMembers = currentMembers; }
    
    public Set<String> getAdminIds() { return adminIds; }
    public void setAdminIds(Set<String> adminIds) { this.adminIds = adminIds; }
    
    public String getAnnouncement() { return announcement; }
    public void setAnnouncement(String announcement) { this.announcement = announcement; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    
    public Boolean getMuteAll() { return muteAll; }
    public void setMuteAll(Boolean muteAll) { this.muteAll = muteAll; }
    
    public Boolean getInviteConfirm() { return inviteConfirm; }
    public void setInviteConfirm(Boolean inviteConfirm) { this.inviteConfirm = inviteConfirm; }
}
