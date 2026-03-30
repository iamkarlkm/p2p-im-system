package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * @提醒请求DTO
 * 功能#28: 消息@提醒
 */
public class MentionRequest {
    
    private Long groupId;
    private Long mentionedUserId;
    private String originalMessageId;
    private String originalContent;
    private String mentionType;
    
    // Getters and Setters
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public Long getMentionedUserId() { return mentionedUserId; }
    public void setMentionedUserId(Long mentionedUserId) { this.mentionedUserId = mentionedUserId; }
    
    public String getOriginalMessageId() { return originalMessageId; }
    public void setOriginalMessageId(String originalMessageId) { this.originalMessageId = originalMessageId; }
    
    public String getOriginalContent() { return originalContent; }
    public void setOriginalContent(String originalContent) { this.originalContent = originalContent; }
    
    public String getMentionType() { return mentionType; }
    public void setMentionType(String mentionType) { this.mentionType = mentionType; }
}
