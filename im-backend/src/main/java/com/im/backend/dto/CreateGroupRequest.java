package com.im.backend.dto;

import java.util.List;

/**
 * 创建群组请求DTO
 * 对应功能 #15 - 群聊功能
 */
public class CreateGroupRequest {
    
    private String name;
    private String description;
    private String avatar;
    private List<Long> memberIds;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public List<Long> getMemberIds() { return memberIds; }
    public void setMemberIds(List<Long> memberIds) { this.memberIds = memberIds; }
}
