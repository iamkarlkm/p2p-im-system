package com.im.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息已读回执响应DTO
 * 对应功能 #16 - 消息已读回执功能
 */
public class MessageReadReceiptResponse {
    
    private Long messageId;
    private Long senderId;
    private int readCount;
    private int totalCount;
    private List<ReadUserInfo> readUsers;
    private LocalDateTime lastReadAt;
    
    public static class ReadUserInfo {
        private Long userId;
        private String nickname;
        private String avatar;
        private LocalDateTime readAt;
        
        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getNickname() { return nickname; }
        public void setNickname(String nickname) { this.nickname = nickname; }
        
        public String getAvatar() { return avatar; }
        public void setAvatar(String avatar) { this.avatar = avatar; }
        
        public LocalDateTime getReadAt() { return readAt; }
        public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    }
    
    // Getters and Setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public int getReadCount() { return readCount; }
    public void setReadCount(int readCount) { this.readCount = readCount; }
    
    public int getTotalCount() { return totalCount; }
    public void setTotalCount(int totalCount) { this.totalCount = totalCount; }
    
    public List<ReadUserInfo> getReadUsers() { return readUsers; }
    public void setReadUsers(List<ReadUserInfo> readUsers) { this.readUsers = readUsers; }
    
    public LocalDateTime getLastReadAt() { return lastReadAt; }
    public void setLastReadAt(LocalDateTime lastReadAt) { this.lastReadAt = lastReadAt; }
}
