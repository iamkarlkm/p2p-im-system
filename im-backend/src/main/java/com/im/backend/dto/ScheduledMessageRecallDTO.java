package com.im.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.im.backend.model.ScheduledMessageRecall;

import java.time.LocalDateTime;

/**
 * 消息定时撤回数据传输对象
 */
public class ScheduledMessageRecallDTO {
    
    private Long id;
    private Long userId;
    private Long messageId;
    private Long conversationId;
    private String conversationType;
    private String messageContent;
    private String messageContentPreview;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime scheduledRecallTime;
    
    private Integer scheduledSeconds;
    private String status;
    private String statusDisplay;
    private String recallReason;
    private Boolean notifyReceivers;
    private String customNotifyMessage;
    private Boolean isCancelable;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime cancelDeadline;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime executedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
    
    // 扩展字段
    private Long remainingSeconds;
    private Boolean canCancel;
    private String senderName;
    private String senderAvatar;
    private String conversationName;
    
    // 构造函数
    public ScheduledMessageRecallDTO() {}
    
    public ScheduledMessageRecallDTO(ScheduledMessageRecall entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.messageId = entity.getMessageId();
        this.conversationId = entity.getConversationId();
        this.conversationType = entity.getConversationType() != null 
            ? entity.getConversationType().name() : null;
        this.messageContent = entity.getMessageContent();
        this.messageContentPreview = generatePreview(entity.getMessageContent());
        this.scheduledRecallTime = entity.getScheduledRecallTime();
        this.scheduledSeconds = entity.getScheduledSeconds();
        this.status = entity.getStatus() != null ? entity.getStatus().name() : null;
        this.statusDisplay = getStatusDisplay(entity.getStatus());
        this.recallReason = entity.getRecallReason();
        this.notifyReceivers = entity.getNotifyReceivers();
        this.customNotifyMessage = entity.getCustomNotifyMessage();
        this.isCancelable = entity.getIsCancelable();
        this.cancelDeadline = entity.getCancelDeadline();
        this.executedAt = entity.getExecutedAt();
        this.createdAt = entity.getCreatedAt();
        this.updatedAt = entity.getUpdatedAt();
        this.remainingSeconds = entity.getRemainingSeconds();
        this.canCancel = entity.canCancel();
    }
    
    // 生成内容预览
    private String generatePreview(String content) {
        if (content == null) return "";
        if (content.length() <= 50) return content;
        return content.substring(0, 50) + "...";
    }
    
    // 获取状态显示文本
    private String getStatusDisplay(ScheduledMessageRecall.RecallStatus status) {
        if (status == null) return "未知";
        switch (status) {
            case PENDING:
                return "待执行";
            case EXECUTED:
                return "已撤回";
            case CANCELLED:
                return "已取消";
            case FAILED:
                return "执行失败";
            case EXPIRED:
                return "已过期";
            default:
                return "未知";
        }
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getMessageId() {
        return messageId;
    }
    
    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }
    
    public Long getConversationId() {
        return conversationId;
    }
    
    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }
    
    public String getConversationType() {
        return conversationType;
    }
    
    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }
    
    public String getMessageContent() {
        return messageContent;
    }
    
    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }
    
    public String getMessageContentPreview() {
        return messageContentPreview;
    }
    
    public void setMessageContentPreview(String messageContentPreview) {
        this.messageContentPreview = messageContentPreview;
    }
    
    public LocalDateTime getScheduledRecallTime() {
        return scheduledRecallTime;
    }
    
    public void setScheduledRecallTime(LocalDateTime scheduledRecallTime) {
        this.scheduledRecallTime = scheduledRecallTime;
    }
    
    public Integer getScheduledSeconds() {
        return scheduledSeconds;
    }
    
    public void setScheduledSeconds(Integer scheduledSeconds) {
        this.scheduledSeconds = scheduledSeconds;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getStatusDisplay() {
        return statusDisplay;
    }
    
    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }
    
    public String getRecallReason() {
        return recallReason;
    }
    
    public void setRecallReason(String recallReason) {
        this.recallReason = recallReason;
    }
    
    public Boolean getNotifyReceivers() {
        return notifyReceivers;
    }
    
    public void setNotifyReceivers(Boolean notifyReceivers) {
        this.notifyReceivers = notifyReceivers;
    }
    
    public String getCustomNotifyMessage() {
        return customNotifyMessage;
    }
    
    public void setCustomNotifyMessage(String customNotifyMessage) {
        this.customNotifyMessage = customNotifyMessage;
    }
    
    public Boolean getIsCancelable() {
        return isCancelable;
    }
    
    public void setIsCancelable(Boolean isCancelable) {
        this.isCancelable = isCancelable;
    }
    
    public LocalDateTime getCancelDeadline() {
        return cancelDeadline;
    }
    
    public void setCancelDeadline(LocalDateTime cancelDeadline) {
        this.cancelDeadline = cancelDeadline;
    }
    
    public LocalDateTime getExecutedAt() {
        return executedAt;
    }
    
    public void setExecutedAt(LocalDateTime executedAt) {
        this.executedAt = executedAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public Long getRemainingSeconds() {
        return remainingSeconds;
    }
    
    public void setRemainingSeconds(Long remainingSeconds) {
        this.remainingSeconds = remainingSeconds;
    }
    
    public Boolean getCanCancel() {
        return canCancel;
    }
    
    public void setCanCancel(Boolean canCancel) {
        this.canCancel = canCancel;
    }
    
    public String getSenderName() {
        return senderName;
    }
    
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
    
    public String getSenderAvatar() {
        return senderAvatar;
    }
    
    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }
    
    public String getConversationName() {
        return conversationName;
    }
    
    public void setConversationName(String conversationName) {
        this.conversationName = conversationName;
    }
    
    @Override
    public String toString() {
        return "ScheduledMessageRecallDTO{" +
            "id=" + id +
            ", messageId=" + messageId +
            ", status='" + status + '\'' +
            ", scheduledRecallTime=" + scheduledRecallTime +
            '}';
    }
}
