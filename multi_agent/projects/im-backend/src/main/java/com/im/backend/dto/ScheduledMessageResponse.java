package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 定时消息响应DTO
 */
public class ScheduledMessageResponse {

    private Long id;
    private String scheduleId;
    private Long senderId;
    private String targetType;
    private Long targetId;
    private String content;
    private String type;
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private LocalDateTime scheduledTime;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    private String status;
    private String failReason;
    private Integer retryCount;
    private Boolean recurring;
    private String recurrencePattern;
    private Integer recurrenceInterval;
    private LocalDateTime recurrenceEndTime;
    private Long remainingSeconds;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getScheduleId() { return scheduleId; }
    public void setScheduleId(String scheduleId) { this.scheduleId = scheduleId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAttachmentUrl() { return attachmentUrl; }
    public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }

    public String getAttachmentName() { return attachmentName; }
    public void setAttachmentName(String attachmentName) { this.attachmentName = attachmentName; }

    public Long getAttachmentSize() { return attachmentSize; }
    public void setAttachmentSize(Long attachmentSize) { this.attachmentSize = attachmentSize; }

    public LocalDateTime getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(LocalDateTime scheduledTime) { this.scheduledTime = scheduledTime; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getFailReason() { return failReason; }
    public void setFailReason(String failReason) { this.failReason = failReason; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public Boolean getRecurring() { return recurring; }
    public void setRecurring(Boolean recurring) { this.recurring = recurring; }

    public String getRecurrencePattern() { return recurrencePattern; }
    public void setRecurrencePattern(String recurrencePattern) { this.recurrencePattern = recurrencePattern; }

    public Integer getRecurrenceInterval() { return recurrenceInterval; }
    public void setRecurrenceInterval(Integer recurrenceInterval) { this.recurrenceInterval = recurrenceInterval; }

    public LocalDateTime getRecurrenceEndTime() { return recurrenceEndTime; }
    public void setRecurrenceEndTime(LocalDateTime recurrenceEndTime) { this.recurrenceEndTime = recurrenceEndTime; }

    public Long getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(Long remainingSeconds) { this.remainingSeconds = remainingSeconds; }
}
