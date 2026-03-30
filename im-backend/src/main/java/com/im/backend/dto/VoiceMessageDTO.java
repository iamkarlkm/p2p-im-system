package com.im.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

/**
 * 语音消息DTO
 */
public class VoiceMessageDTO {
    
    private String messageId;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private Long groupId;
    private String voiceFileId;
    private String voiceUrl;
    private Integer duration;
    private String textContent;
    private Boolean isConverted;
    private Boolean isRead;
    private Boolean isPlayed;
    private Integer playCount;
    private String messageType;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    // Getters and Setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public String getVoiceFileId() { return voiceFileId; }
    public void setVoiceFileId(String voiceFileId) { this.voiceFileId = voiceFileId; }
    
    public String getVoiceUrl() { return voiceUrl; }
    public void setVoiceUrl(String voiceUrl) { this.voiceUrl = voiceUrl; }
    
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }
    
    public Boolean getIsConverted() { return isConverted; }
    public void setIsConverted(Boolean isConverted) { this.isConverted = isConverted; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public Boolean getIsPlayed() { return isPlayed; }
    public void setIsPlayed(Boolean isPlayed) { this.isPlayed = isPlayed; }
    
    public Integer getPlayCount() { return playCount; }
    public void setPlayCount(Integer playCount) { this.playCount = playCount; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
