package com.im.backend.dto;

import com.im.backend.entity.GroupMember;
import java.time.LocalDateTime;

/**
 * 群组响应DTO
 * 对应功能 #15 - 群聊功能
 */
public class GroupResponse {
    
    private Long id;
    private String name;
    private String description;
    private String avatar;
    private Long ownerId;
    private Integer maxMembers;
    private Integer currentMembers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private GroupMember.MemberRole myRole;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    
    public Integer getMaxMembers() { return maxMembers; }
    public void setMaxMembers(Integer maxMembers) { this.maxMembers = maxMembers; }
    
    public Integer getCurrentMembers() { return currentMembers; }
    public void setCurrentMembers(Integer currentMembers) { this.currentMembers = currentMembers; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public GroupMember.MemberRole getMyRole() { return myRole; }
    public void setMyRole(GroupMember.MemberRole myRole) { this.myRole = myRole; }
}
