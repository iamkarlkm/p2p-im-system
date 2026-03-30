package com.im.backend.dto;

import com.im.backend.entity.GroupMember;
import java.time.LocalDateTime;

/**
 * 群成员响应DTO
 * 对应功能 #15 - 群聊功能
 */
public class GroupMemberResponse {
    
    private Long id;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private GroupMember.MemberRole role;
    private String nickname;
    private LocalDateTime joinedAt;
    private Boolean isMuted;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
    
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
    
    public GroupMember.MemberRole getRole() { return role; }
    public void setRole(GroupMember.MemberRole role) { this.role = role; }
    
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    
    public LocalDateTime getJoinedAt() { return joinedAt; }
    public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
    
    public Boolean getIsMuted() { return isMuted; }
    public void setIsMuted(Boolean isMuted) { this.isMuted = isMuted; }
}
