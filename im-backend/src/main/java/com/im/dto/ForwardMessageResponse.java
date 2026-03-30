package com.im.dto;

import java.time.LocalDateTime;

/**
 * 消息转发响应DTO
 * 功能#22: 消息转发
 */
public class ForwardMessageResponse {

    private Long forwardId;
    private Long originalMessageId;
    private Long newMessageId;
    private Long targetConversationId;
    private String targetConversationType;
    private LocalDateTime forwardTime;
    private Boolean success;
    private String errorMessage;

    public Long getForwardId() {
        return forwardId;
    }

    public void setForwardId(Long forwardId) {
        this.forwardId = forwardId;
    }

    public Long getOriginalMessageId() {
        return originalMessageId;
    }

    public void setOriginalMessageId(Long originalMessageId) {
        this.originalMessageId = originalMessageId;
    }

    public Long getNewMessageId() {
        return newMessageId;
    }

    public void setNewMessageId(Long newMessageId) {
        this.newMessageId = newMessageId;
    }

    public Long getTargetConversationId() {
        return targetConversationId;
    }

    public void setTargetConversationId(Long targetConversationId) {
        this.targetConversationId = targetConversationId;
    }

    public String getTargetConversationType() {
        return targetConversationType;
    }

    public void setTargetConversationType(String targetConversationType) {
        this.targetConversationType = targetConversationType;
    }

    public LocalDateTime getForwardTime() {
        return forwardTime;
    }

    public void setForwardTime(LocalDateTime forwardTime) {
        this.forwardTime = forwardTime;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
