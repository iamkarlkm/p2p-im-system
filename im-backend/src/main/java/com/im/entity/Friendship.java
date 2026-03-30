package com.im.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 好友关系实体类
 * 功能 #4: 好友关系管理系统
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public class Friendship implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // ==================== 关系状态 ====================
    public enum FriendshipStatus {
        PENDING("待确认"),
        ACCEPTED("已接受"),
        REJECTED("已拒绝"),
        BLOCKED("已屏蔽"),
        DELETED("已删除");
        
        private final String description;
        
        FriendshipStatus(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // ==================== 核心字段 ====================
    private String friendshipId;
    private String userId;
    private String friendId;
    private FriendshipStatus status;
    private String remark;
    private String groupId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private String requestMessage;
    private LocalDateTime acceptTime;
    
    // ==================== 构造函数 ====================
    public Friendship() {
        this.status = FriendshipStatus.PENDING;
        this.createTime = LocalDateTime.now();
    }
    
    // ==================== 业务方法 ====================
    
    public void accept() {
        this.status = FriendshipStatus.ACCEPTED;
        this.acceptTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }
    
    public void reject() {
        this.status = FriendshipStatus.REJECTED;
        this.updateTime = LocalDateTime.now();
    }
    
    public void block() {
        this.status = FriendshipStatus.BLOCKED;
        this.updateTime = LocalDateTime.now();
    }
    
    public boolean isAccepted() {
        return status == FriendshipStatus.ACCEPTED;
    }
    
    // ==================== Getter & Setter ====================
    public String getFriendshipId() { return friendshipId; }
    public void setFriendshipId(String friendshipId) { this.friendshipId = friendshipId; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getFriendId() { return friendId; }
    public void setFriendId(String friendId) { this.friendId = friendId; }
    
    public FriendshipStatus getStatus() { return status; }
    public void setStatus(FriendshipStatus status) { this.status = status; }
    
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    
    public String getRequestMessage() { return requestMessage; }
    public void setRequestMessage(String requestMessage) { this.requestMessage = requestMessage; }
    
    public LocalDateTime getAcceptTime() { return acceptTime; }
    public void setAcceptTime(LocalDateTime acceptTime) { this.acceptTime = acceptTime; }
}
