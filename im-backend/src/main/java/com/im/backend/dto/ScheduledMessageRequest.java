package com.im.backend.dto;

/**
 * 定时消息请求DTO
 */
public class ScheduledMessageRequest {

    private Long targetId;
    private String targetType; // USER, GROUP
    private String content;
    private String type; // TEXT, IMAGE, FILE
    private String attachmentUrl;
    private String attachmentName;
    private Long attachmentSize;
    private String scheduledTime; // ISO timestamp
    private Boolean recurring;
    private String recurrencePattern; // DAILY, WEEKLY, MONTHLY
    private Integer recurrenceInterval;
    private String recurrenceEndTime; // ISO timestamp

    // Getters and Setters
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

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

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public Boolean getRecurring() { return recurring; }
    public void setRecurring(Boolean recurring) { this.recurring = recurring; }

    public String getRecurrencePattern() { return recurrencePattern; }
    public void setRecurrencePattern(String recurrencePattern) { this.recurrencePattern = recurrencePattern; }

    public Integer getRecurrenceInterval() { return recurrenceInterval; }
    public void setRecurrenceInterval(Integer recurrenceInterval) { this.recurrenceInterval = recurrenceInterval; }

    public String getRecurrenceEndTime() { return recurrenceEndTime; }
    public void setRecurrenceEndTime(String recurrenceEndTime) { this.recurrenceEndTime = recurrenceEndTime; }
}
