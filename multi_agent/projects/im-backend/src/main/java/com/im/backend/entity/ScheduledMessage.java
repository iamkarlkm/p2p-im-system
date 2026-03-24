package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 定时消息实体
 */
@Entity
@Table(name = "scheduled_messages")
public class ScheduledMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String scheduleId; // UUID

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String targetType; // USER, GROUP

    @Column(nullable = false)
    private Long targetId;

    @Column(nullable = false)
    private String content;

    private String type; // TEXT, IMAGE, FILE, AUDIO, VIDEO

    private String attachmentUrl;

    private String attachmentName;

    private Long attachmentSize;

    private LocalDateTime scheduledTime;

    private LocalDateTime createdAt;

    private LocalDateTime sentAt;

    @Column(nullable = false)
    private String status; // PENDING, SENT, CANCELLED, FAILED

    private String failReason;

    private Integer retryCount;

    private Boolean recurring; // 重复发送
    private String recurrencePattern; // DAILY, WEEKLY, MONTHLY
    private Integer recurrenceInterval;
    private LocalDateTime recurrenceEndTime;

    @PrePersist
    protected void onCreate() {
        if (status == null) status = "PENDING";
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (retryCount == null) retryCount = 0;
        if (recurring == null) recurring = false;
    }

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
}
