package com.im.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 群组成员实体类
 * 功能 #5: 群组管理基础模块
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class GroupMember implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ==================== 成员角色 ====================
    public enum MemberRole {
        OWNER("群主"),
        ADMIN("管理员"),
        MEMBER("成员");
        
        private final String description;
        
        MemberRole(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // ==================== 核心字段 ====================
    private String memberId;
    private String groupId;
    private String userId;
    private MemberRole role;
    private String groupNickname;
    private LocalDateTime joinTime;
    private LocalDateTime lastActiveTime;
    private Boolean muted;
    private Long muteUntil;
    
    // ==================== 构造函数 ====================
    public GroupMember() {
        this.role = MemberRole.MEMBER;
        this.joinTime = LocalDateTime.now();
        this.muted = false;
    }
    
    // ==================== 业务方法 ====================
    
    public boolean isMuted() {
        if (!muted) return false;
        if (muteUntil != null && System.currentTimeMillis() > muteUntil) {
            muted = false;
            return false;
        }
        return true;
    }
    
    public void mute(int minutes) {
        this.muted = true;
        this.muteUntil = System.currentTimeMillis() + (minutes * 60 * 1000);
    }
    
    public void unmute() {
        this.muted = false;
        this.muteUntil = null;
    }
    
    // ==================== Getter & Setter ====================
    public String getMemberId() { return memberId; }
    public void setMemberId(String memberId) { this.memberId = memberId; }
    
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public MemberRole getRole() { return role; }
    public void setRole(MemberRole role) { this.role = role; }
    
    public String getGroupNickname() { return groupNickname; }
    public void setGroupNickname(String groupNickname) { this.groupNickname = groupNickname; }
    
    public LocalDateTime getJoinTime() { return joinTime; }
    public void setJoinTime(LocalDateTime joinTime) { this.joinTime = joinTime; }
    
    public LocalDateTime getLastActiveTime() { return lastActiveTime; }
    public void setLastActiveTime(LocalDateTime lastActiveTime) { this.lastActiveTime = lastActiveTime; }
    
    public Boolean getMuted() { return muted; }
    public void setMuted(Boolean muted) { this.muted = muted; }
    
    public Long getMuteUntil() { return muteUntil; }
    public void setMuteUntil(Long muteUntil) { this.muteUntil = muteUntil; }
}
