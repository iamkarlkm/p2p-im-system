package com.im.dto;

import java.util.List;

/**
 * 消息转发请求DTO
 * 功能#22: 消息转发
 */
public class ForwardMessageRequest {

    private Long originalMessageId;
    private List<Long> targetConversationIds;
    private List<String> targetConversationTypes;
    private String forwardComment;
    private Boolean isMultiForward;

    public Long getOriginalMessageId() {
        return originalMessageId;
    }

    public void setOriginalMessageId(Long originalMessageId) {
        this.originalMessageId = originalMessageId;
    }

    public List<Long> getTargetConversationIds() {
        return targetConversationIds;
    }

    public void setTargetConversationIds(List<Long> targetConversationIds) {
        this.targetConversationIds = targetConversationIds;
    }

    public List<String> getTargetConversationTypes() {
        return targetConversationTypes;
    }

    public void setTargetConversationTypes(List<String> targetConversationTypes) {
        this.targetConversationTypes = targetConversationTypes;
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
