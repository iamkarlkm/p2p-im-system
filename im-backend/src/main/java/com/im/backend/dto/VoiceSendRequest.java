package com.im.backend.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 发送语音消息请求DTO
 */
public class VoiceSendRequest {
    
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;
    
    @NotNull(message = "语音文件ID不能为空")
    private String voiceFileId;
    
    @NotNull(message = "语音时长不能为空")
    private Integer duration;
    
    private String textContent;
    
    private String messageType = "PRIVATE";
    
    private Long groupId;
    
    // Getters and Setters
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public String getVoiceFileId() { return voiceFileId; }
    public void setVoiceFileId(String voiceFileId) { this.voiceFileId = voiceFileId; }
    
    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }
    
    public String getTextContent() { return textContent; }
    public void setTextContent(String textContent) { this.textContent = textContent; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
}
