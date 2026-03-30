package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 名片分享请求DTO
 * 功能#27: 名片分享
 */
public class ContactCardRequest {
    
    private Long receiverId;
    private Long groupId;
    private String conversationType;
    private Long contactUserId;
    private String contactNickname;
    private String contactAvatar;
    private String contactRemark;
    
    // Getters and Setters
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
}
