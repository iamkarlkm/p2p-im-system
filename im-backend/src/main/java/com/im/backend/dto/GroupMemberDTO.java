package com.im.backend.dto;

import com.im.backend.entity.GroupMemberRole;
import java.time.LocalDateTime;

/**
 * 群成员DTO
 * 功能#29: 群成员管理
 */
public class GroupMemberDTO {
    
    private Long id;
    private Long groupId;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private GroupMemberRole role;
    private String groupNickname;
    private LocalDateTime muteUntil;
    private LocalDateTime joinTime;
    private Boolean isMuted;
    private Boolean isBlocked;
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getUserNickname() { return userNickname; }
    public void setUserNickname(String userNickname) { this.userNickname = userNickname; }
    
    public String getUserAvatar() { return userAvatar; }
    public void setUserAvatar(String userAvatar) { this.userAvatar = userAvatar; }
    
    public GroupMemberRole getRole() { return role; }
    public void setRole(GroupMemberRole role) { this.role = role; }
    
    public String getGroupNickname() { return groupNickname; }
    public void setGroupNickname(String groupNickname) { this.groupNickname = groupNickname; }
    
    public LocalDateTime getMuteUntil() { return muteUntil; }
    public void setMuteUntil(LocalDateTime muteUntil) { this.muteUntil = muteUntil; }
    
    public LocalDateTime getJoinTime() { return joinTime; }
    public void setJoinTime(LocalDateTime joinTime) { this.joinTime = joinTime; }
    
    public Boolean getIsMuted() { return isMuted; }
    public void setIsMuted(Boolean isMuted) { this.isMuted = isMuted; }
    
    public Boolean getIsBlocked() { return isBlocked; }
    public void setIsBlocked(Boolean isBlocked) { this.isBlocked = isBlocked; }
}
