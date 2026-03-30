package com.im.backend.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 语音消息实体类
 * 存储语音消息的元数据信息
 */
@Entity
@Table(name = "voice_messages")
public class VoiceMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;
    
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    @Column(name = "receiver_id")
    private Long receiverId;
    
    @Column(name = "group_id")
    private Long groupId;
    
    @Column(name = "voice_file_id", nullable = false)
    private String voiceFileId;
    
    @Column(name = "duration", nullable = false)
    private Integer duration;
    
    @Column(name = "text_content")
    private String textContent;
    
    @Column(name = "is_converted", nullable = false)
    private Boolean isConverted = false;
    
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;
    
    @Column(name = "is_played", nullable = false)
    private Boolean isPlayed = false;
    
    @Column(name = "play_count", nullable = false)
    private Integer playCount = 0;
    
    @Column(name = "message_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.PRIVATE;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum MessageType {
        PRIVATE, GROUP
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public String getVoiceFileId() { return voiceFileId; }
    public void setVoiceFileId(String voiceFileId) { this.voiceFileId = voiceFileId; }
    
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
    
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
