package com.im.backend.dto;

/**
 * 添加群成员请求DTO
 * 功能#29: 群成员管理
 */
public class AddGroupMemberRequest {
    
    private Long groupId;
    private Long userId;
    private String groupNickname;
    
    // Getters and Setters
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getGroupNickname() { return groupNickname; }
    public void setGroupNickname(String groupNickname) { this.groupNickname = groupNickname; }
}
