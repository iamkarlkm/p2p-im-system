package com.im.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.im.backend.model.MessageEditHistory.EditType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 消息编辑请求/响应DTO
 * 用于前后端数据传输
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */
public class MessageEditDTO {

    /**
     * 编辑记录ID
     */
    private Long id;

    /**
     * 消息ID
     */
    @NotNull(message = "消息ID不能为空")
    private Long messageId;

    /**
     * 编辑用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 原消息内容
     */
    @NotBlank(message = "原内容不能为空")
    @Size(max = 10000, message = "原内容不能超过10000字符")
    private String originalContent;

    /**
     * 编辑后的内容
     */
    @NotBlank(message = "新内容不能为空")
    @Size(max = 10000, message = "新内容不能超过10000字符")
    private String editedContent;

    /**
     * 编辑原因
     */
    @Size(max = 500, message = "编辑原因不能超过500字符")
    private String editReason;

    /**
     * 编辑序号
     */
    private Integer editSequence;

    /**
     * 编辑类型
     */
    private EditType editType = EditType.NORMAL;

    /**
     * 编辑时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime editedAt;

    /**
     * 是否可以继续编辑（在时间窗口内）
     */
    private Boolean canEditFurther;

    /**
     * 剩余可编辑次数
     */
    private Integer remainingEditCount;

    /**
     * 编辑时间限制（分钟）
     */
    private Integer editTimeLimitMinutes;

    /**
     * 是否显示编辑标记
     */
    private Boolean showEditMark = true;

    /**
     * 编辑标记文本
     */
    private String editMarkText;

    /**
     * 内容变化统计
     */
    private ContentChangeStats contentChangeStats;

    /**
     * 内容变化统计内部类
     */
    public static class ContentChangeStats {
        private int originalLength;
        private int editedLength;
        private int changeLength;
        private double changePercentage;
        private boolean increased;
        private boolean decreased;

        public ContentChangeStats() {
        }

        public ContentChangeStats(int originalLength, int editedLength) {
            this.originalLength = originalLength;
            this.editedLength = editedLength;
            this.changeLength = editedLength - originalLength;
            this.changePercentage = originalLength > 0 
                ? (double) changeLength / originalLength * 100 
                : 0;
            this.increased = changeLength > 0;
            this.decreased = changeLength < 0;
        }

        // Getters and Setters
        public int getOriginalLength() {
            return originalLength;
        }

        public void setOriginalLength(int originalLength) {
            this.originalLength = originalLength;
        }

        public int getEditedLength() {
            return editedLength;
        }

        public void setEditedLength(int editedLength) {
            this.editedLength = editedLength;
        }

        public int getChangeLength() {
            return changeLength;
        }

        public void setChangeLength(int changeLength) {
            this.changeLength = changeLength;
        }

        public double getChangePercentage() {
            return changePercentage;
        }

        public void setChangePercentage(double changePercentage) {
            this.changePercentage = changePercentage;
        }

        public boolean isIncreased() {
            return increased;
        }

        public void setIncreased(boolean increased) {
            this.increased = increased;
        }

        public boolean isDecreased() {
            return decreased;
        }

        public void setDecreased(boolean decreased) {
            this.decreased = decreased;
        }
    }

    // ==================== 构造方法 ====================

    public MessageEditDTO() {
    }

    public MessageEditDTO(Long messageId, String editedContent) {
        this.messageId = messageId;
        this.editedContent = editedContent;
    }

    // ==================== Getter & Setter ====================

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public String getEditedContent() {
        return editedContent;
    }

    public void setEditedContent(String editedContent) {
        this.editedContent = editedContent;
    }

    public String getEditReason() {
        return editReason;
    }

    public void setEditReason(String editReason) {
        this.editReason = editReason;
    }

    public Integer getEditSequence() {
        return editSequence;
    }

    public void setEditSequence(Integer editSequence) {
        this.editSequence = editSequence;
    }

    public EditType getEditType() {
        return editType;
    }

    public void setEditType(EditType editType) {
        this.editType = editType;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
    }

    public Boolean getCanEditFurther() {
        return canEditFurther;
    }

    public void setCanEditFurther(Boolean canEditFurther) {
        this.canEditFurther = canEditFurther;
    }

    public Integer getRemainingEditCount() {
        return remainingEditCount;
    }

    public void setRemainingEditCount(Integer remainingEditCount) {
        this.remainingEditCount = remainingEditCount;
    }

    public Integer getEditTimeLimitMinutes() {
        return editTimeLimitMinutes;
    }

    public void setEditTimeLimitMinutes(Integer editTimeLimitMinutes) {
        this.editTimeLimitMinutes = editTimeLimitMinutes;
    }

    public Boolean getShowEditMark() {
        return showEditMark;
    }

    public void setShowEditMark(Boolean showEditMark) {
        this.showEditMark = showEditMark;
    }

    public String getEditMarkText() {
        return editMarkText;
    }

    public void setEditMarkText(String editMarkText) {
        this.editMarkText = editMarkText;
    }

    public ContentChangeStats getContentChangeStats() {
        return contentChangeStats;
    }

    public void setContentChangeStats(ContentChangeStats contentChangeStats) {
        this.contentChangeStats = contentChangeStats;
    }

    // ==================== 业务方法 ====================

    /**
     * 计算内容变化统计
     */
    public void calculateChangeStats() {
        if (originalContent != null && editedContent != null) {
            this.contentChangeStats = new ContentChangeStats(
                originalContent.length(), 
                editedContent.length()
            );
        }
    }

    /**
     * 生成编辑标记文本
     */
    public void generateEditMarkText() {
        if (editedAt != null && editSequence != null) {
            this.editMarkText = String.format("已编辑（第%d次）· %s", 
                editSequence, 
                formatTime(editedAt));
        }
    }

    private String formatTime(LocalDateTime time) {
        if (time == null) return "";
        return time.toLocalDate().equals(LocalDateTime.now().toLocalDate())
            ? time.toLocalTime().toString().substring(0, 5)
            : time.toLocalDate().toString();
    }

    // ==================== toString ====================

    @Override
    public String toString() {
        return "MessageEditDTO{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", userId=" + userId +
                ", editSequence=" + editSequence +
                ", editType=" + editType +
                ", editedAt=" + editedAt +
                '}';
    }
}
