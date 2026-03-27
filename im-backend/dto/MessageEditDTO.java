package com.im.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

/**
 * 消息编辑DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageEditDTO {

    /** 是否成功 */
    private boolean success;

    /** 消息ID */
    private Long messageId;

    /** 会话ID */
    private Long conversationId;

    /** 新内容 */
    private String newContent;

    /** 原始内容 */
    private String originalContent;

    /** 编辑者用户ID */
    private Long editedBy;

    /** 编辑时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime editTime;

    /** 编辑版本号 */
    private int editVersion;

    /** 编辑次数 */
    private int editCount;

    /** 编辑原因 */
    private String editReason;

    /** 错误代码 */
    private String errorCode;

    /** 错误信息 */
    private String errorMessage;

    // ========== 构造方法 ==========

    public MessageEditDTO() {
    }

    public MessageEditDTO(boolean success, Long messageId) {
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

    public String getNewContent() {
        return newContent;
    }

    public void setNewContent(String newContent) {
        this.newContent = newContent;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public Long getEditedBy() {
        return editedBy;
    }

    public void setEditedBy(Long editedBy) {
        this.editedBy = editedBy;
    }

    public LocalDateTime getEditTime() {
        return editTime;
    }

    public void setEditTime(LocalDateTime editTime) {
        this.editTime = editTime;
    }

    public int getEditVersion() {
        return editVersion;
    }

    public void setEditVersion(int editVersion) {
        this.editVersion = editVersion;
    }

    public int getEditCount() {
        return editCount;
    }

    public void setEditCount(int editCount) {
        this.editCount = editCount;
    }

    public String getEditReason() {
        return editReason;
    }

    public void setEditReason(String editReason) {
        this.editReason = editReason;
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

    public static MessageEditDTO success(Long messageId, String newContent) {
        MessageEditDTO dto = new MessageEditDTO();
        dto.setSuccess(true);
        dto.setMessageId(messageId);
        dto.setNewContent(newContent);
        dto.setEditTime(LocalDateTime.now());
        return dto;
    }

    public static MessageEditDTO error(String errorCode, String errorMessage) {
        MessageEditDTO dto = new MessageEditDTO();
        dto.setSuccess(false);
        dto.setErrorCode(errorCode);
        dto.setErrorMessage(errorMessage);
        return dto;
    }

    // ========== 内部配置类 ==========

    public static class EditConfig {
        private int timeLimitSeconds;
        private int maxEditCount;
        private boolean saveHistory;
        private boolean showEditMark;

        public int getTimeLimitSeconds() {
            return timeLimitSeconds;
        }

        public void setTimeLimitSeconds(int timeLimitSeconds) {
            this.timeLimitSeconds = timeLimitSeconds;
        }

        public int getMaxEditCount() {
            return maxEditCount;
        }

        public void setMaxEditCount(int maxEditCount) {
            this.maxEditCount = maxEditCount;
        }

        public boolean isSaveHistory() {
            return saveHistory;
        }

        public void setSaveHistory(boolean saveHistory) {
            this.saveHistory = saveHistory;
        }

        public boolean isShowEditMark() {
            return showEditMark;
        }

        public void setShowEditMark(boolean showEditMark) {
            this.showEditMark = showEditMark;
        }
    }

    @Override
    public String toString() {
        return "MessageEditDTO{" +
            "success=" + success +
            ", messageId=" + messageId +
            ", editVersion=" + editVersion +
            ", editCount=" + editCount +
            '}';
    }
}
