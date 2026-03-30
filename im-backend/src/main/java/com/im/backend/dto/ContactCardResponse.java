package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 名片分享响应DTO
 * 功能#27: 名片分享
 */
public class ContactCardResponse {
    
    private String messageId;
    private Long senderId;
    private Long receiverId;
    private Long groupId;
    private String conversationType;
    private Long contactUserId;
    private String contactNickname;
    private String contactAvatar;
    private String contactRemark;
    private Boolean isRead;
    private LocalDateTime readTime;
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }
    
    public Long getContactUserId() { return contactUserId; }
    public void setContactUserId(Long contactUserId) { this.contactUserId = contactUserId; }
    
    public String getContactNickname() { return contactNickname; }
    public void setContactNickname(String contactNickname) { this.contactNickname = contactNickname; }
    
    public String getContactAvatar() { return contactAvatar; }
    public void setContactAvatar(String contactAvatar) { this.contactAvatar = contactAvatar; }
    
    public String getContactRemark() { return contactRemark; }
    public void setContactRemark(String contactRemark) { this.contactRemark = contactRemark; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public LocalDateTime getReadTime() { return readTime; }
    public void setReadTime(LocalDateTime readTime) { this.readTime = readTime; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
