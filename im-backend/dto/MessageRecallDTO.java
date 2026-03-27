package com.im.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * 消息撤回DTO
 * 用于撤回请求和响应的数据传输对象
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageRecallDTO {

    /** 是否成功 */
    private boolean success;

    /** 消息ID */
    private Long messageId;

    /** 会话ID */
    private Long conversationId;

    /** 撤回者用户ID */
    private Long recalledBy;

    /** 撤回时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime recallTime;

    /** 撤回类型 (USER-用户撤回, ADMIN-管理员撤回, UNDO_RECALL-撤销撤回) */
    private String recallType;

    /** 原始消息内容（仅管理员可见） */
    private String originalContent;

    /** 错误代码 */
    private String errorCode;

    /** 错误信息 */
    private String errorMessage;

    // ========== 构造方法 ==========

    public MessageRecallDTO() {
    }

    public MessageRecallDTO(boolean success, Long messageId) {
        this.success = success;
        this.messageId = messageId;
    }

    // ========== Getters & Setters ==========

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public Long getRecalledBy() {
        return recalledBy;
    }

    public void setRecalledBy(Long recalledBy) {
        this.recalledBy = recalledBy;
    }

    public LocalDateTime getRecallTime() {
        return recallTime;
    }

    public void setRecallTime(LocalDateTime recallTime) {
        this.recallTime = recallTime;
    }

    public String getRecallType() {
        return recallType;
    }

    public void setRecallType(String recallType) {
        this.recallType = recallType;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    // ========== 便捷方法 ==========

    /**
     * 创建成功响应
     */
    public static MessageRecallDTO success(Long messageId) {
        MessageRecallDTO dto = new MessageRecallDTO();
        dto.setSuccess(true);
        dto.setMessageId(messageId);
        dto.setRecallTime(LocalDateTime.now());
        return dto;
    }

    /**
     * 创建失败响应
     */
    public static MessageRecallDTO error(String errorCode, String errorMessage) {
        MessageRecallDTO dto = new MessageRecallDTO();
        dto.setSuccess(false);
        dto.setErrorCode(errorCode);
        dto.setErrorMessage(errorMessage);
        return dto;
    }

    @Override
    public String toString() {
        return "MessageRecallDTO{" +
            "success=" + success +
            ", messageId=" + messageId +
            ", conversationId=" + conversationId +
            ", recalledBy=" + recalledBy +
            ", recallTime=" + recallTime +
            ", recallType='" + recallType + '\'' +
            ", errorCode='" + errorCode + '\'' +
            ", errorMessage='" + errorMessage + '\'' +
            '}';
    }
}
