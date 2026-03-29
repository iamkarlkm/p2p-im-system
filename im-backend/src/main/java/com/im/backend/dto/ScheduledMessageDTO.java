package com.im.backend.dto;

import com.im.backend.model.ScheduledMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * 定时消息数据传输对象
 */
public class ScheduledMessageDTO {

    private Long id;
    private Long senderId;
    
    @NotNull(message = "接收者ID不能为空")
    private Long receiverId;
    
    @NotBlank(message = "消息内容不能为空")
    @Size(max = 5000, message = "消息内容不能超过5000字符")
    private String content;
    
    private ScheduledMessage.Status status;
    
    @NotNull(message = "定时时间不能为空")
    private LocalDateTime scheduledTime;
    
    private LocalDateTime sentTime;
    private String failureReason;
    private LocalDateTime createdAt;
    private String receiverNickname;
    private String receiverAvatar;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public ScheduledMessage.Status getStatus() { return status; }
    public void setStatus(ScheduledMessage.Status status) { this.status = status; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public LocalDateTime getSentTime() { return sentTime; }
    public void setSentTime(LocalDateTime sentTime) { this.sentTime = sentTime; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getReceiverNickname() { return receiverNickname; }
    public void setReceiverNickname(String receiverNickname) { this.receiverNickname = receiverNickname; }

    public String getReceiverAvatar() { return receiverAvatar; }
    public void setReceiverAvatar(String receiverAvatar) { this.receiverAvatar = receiverAvatar; }
}
