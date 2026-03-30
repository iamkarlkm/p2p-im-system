package com.im.dto;

import java.time.LocalDateTime;

/**
 * 转发记录DTO
 * 功能#22: 消息转发
 */
public class ForwardRecordDTO {

    private Long id;
    private Long originalMessageId;
    private String originalMessagePreview;
    private Long forwarderId;
    private String forwarderName;
    private Long targetConversationId;
    private String targetConversationName;
    private String targetConversationType;
    private LocalDateTime forwardTime;
    private String forwardComment;
    private Boolean isMultiForward;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOriginalMessageId() {
        return originalMessageId;
    }

    public void setOriginalMessageId(Long originalMessageId) {
        this.originalMessageId = originalMessageId;
    }

    public String getOriginalMessagePreview() {
        return originalMessagePreview;
    }

    public void setOriginalMessagePreview(String originalMessagePreview) {
        this.originalMessagePreview = originalMessagePreview;
    }

    public Long getForwarderId() {
        return forwarderId;
    }

    public void setForwarderId(Long forwarderId) {
        this.forwarderId = forwarderId;
    }

    public String getForwarderName() {
        return forwarderName;
    }

    public void setForwarderName(String forwarderName) {
        this.forwarderName = forwarderName;
    }

    public Long getTargetConversationId() {
        return targetConversationId;
    }

    public void setTargetConversationId(Long targetConversationId) {
        this.targetConversationId = targetConversationId;
    }

    public String getTargetConversationName() {
        return targetConversationName;
    }

    public void setTargetConversationName(String targetConversationName) {
        this.targetConversationName = targetConversationName;
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

    public String getForwardComment() {
        return forwardComment;
    }

    public void setForwardComment(String forwardComment) {
        this.forwardComment = forwardComment;
    }

    public Boolean getIsMultiForward() {
        return isMultiForward;
    }

    public void setIsMultiForward(Boolean isMultiForward) {
        this.isMultiForward = isMultiForward;
    }
}
