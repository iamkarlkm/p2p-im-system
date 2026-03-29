package com.im.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息编辑历史DTO
 * 用于展示消息的完整编辑历史
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */
public class MessageEditHistoryDTO {

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 当前内容
     */
    private String currentContent;

    /**
     * 原始内容（首次发送的内容）
     */
    private String originalContent;

    /**
     * 编辑总次数
     */
    private Integer totalEditCount;

    /**
     * 最后编辑时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastEditedAt;

    /**
     * 最后编辑用户
     */
    private UserSummaryDTO lastEditedBy;

    /**
     * 是否还能编辑
     */
    private Boolean canEdit;

    /**
     * 不能编辑的原因
     */
    private String cannotEditReason;

    /**
     * 编辑历史列表
     */
    private List<EditHistoryItem> editHistory;

    /**
     * 编辑时间窗口（分钟）
     */
    private Integer editTimeWindowMinutes;

    /**
     * 最大编辑次数
     */
    private Integer maxEditCount;

    /**
     * 编辑统计信息
     */
    private EditStatistics statistics;

    /**
     * 用户简要信息DTO
     */
    public static class UserSummaryDTO {
        private Long id;
        private String nickname;
        private String avatar;

        public UserSummaryDTO() {
        }

        public UserSummaryDTO(Long id, String nickname, String avatar) {
            this.id = id;
            this.nickname = nickname;
            this.avatar = avatar;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }

    /**
     * 编辑历史项
     */
    public static class EditHistoryItem {
        private Long editId;
        private Integer sequence;
        private String beforeContent;
        private String afterContent;
        private String editReason;
        private String editType;
        
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime editedAt;
        
        private UserSummaryDTO editedBy;
        private Long editTimeMillis;
        private ContentDiff contentDiff;

        public EditHistoryItem() {
        }

        public Long getEditId() {
            return editId;
        }

        public void setEditId(Long editId) {
            this.editId = editId;
        }

        public Integer getSequence() {
            return sequence;
        }

        public void setSequence(Integer sequence) {
            this.sequence = sequence;
        }

        public String getBeforeContent() {
            return beforeContent;
        }

        public void setBeforeContent(String beforeContent) {
            this.beforeContent = beforeContent;
        }

        public String getAfterContent() {
            return afterContent;
        }

        public void setAfterContent(String afterContent) {
            this.afterContent = afterContent;
        }

        public String getEditReason() {
            return editReason;
        }

        public void setEditReason(String editReason) {
            this.editReason = editReason;
        }

        public String getEditType() {
            return editType;
        }

        public void setEditType(String editType) {
            this.editType = editType;
        }

        public LocalDateTime getEditedAt() {
            return editedAt;
        }

        public void setEditedAt(LocalDateTime editedAt) {
            this.editedAt = editedAt;
        }

        public UserSummaryDTO getEditedBy() {
            return editedBy;
        }

        public void setEditedBy(UserSummaryDTO editedBy) {
            this.editedBy = editedBy;
        }

        public Long getEditTimeMillis() {
            return editTimeMillis;
        }

        public void setEditTimeMillis(Long editTimeMillis) {
            this.editTimeMillis = editTimeMillis;
        }

        public ContentDiff getContentDiff() {
            return contentDiff;
        }

        public void setContentDiff(ContentDiff contentDiff) {
            this.contentDiff = contentDiff;
        }
    }

    /**
     * 内容差异
     */
    public static class ContentDiff {
        private List<DiffSegment> segments;
        private int addedCount;
        private int removedCount;
        private int unchangedCount;

        public ContentDiff() {
        }

        public List<DiffSegment> getSegments() {
            return segments;
        }

        public void setSegments(List<DiffSegment> segments) {
            this.segments = segments;
        }

        public int getAddedCount() {
            return addedCount;
        }

        public void setAddedCount(int addedCount) {
            this.addedCount = addedCount;
        }

        public int getRemovedCount() {
            return removedCount;
        }

        public void setRemovedCount(int removedCount) {
            this.removedCount = removedCount;
        }

        public int getUnchangedCount() {
            return unchangedCount;
        }

        public void setUnchangedCount(int unchangedCount) {
            this.unchangedCount = unchangedCount;
        }
    }

    /**
     * 差异段
     */
    public static class DiffSegment {
        public enum DiffType {
            ADDED, REMOVED, UNCHANGED
        }

        private DiffType type;
        private String content;
        private int startIndex;
        private int endIndex;

        public DiffSegment() {
        }

        public DiffSegment(DiffType type, String content) {
            this.type = type;
            this.content = content;
        }

        public DiffType getType() {
            return type;
        }

        public void setType(DiffType type) {
            this.type = type;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public void setStartIndex(int startIndex) {
            this.startIndex = startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

        public void setEndIndex(int endIndex) {
            this.endIndex = endIndex;
        }
    }

    /**
     * 编辑统计
     */
    public static class EditStatistics {
        private int totalEdits;
        private int editsByOwner;
        private int editsByAdmin;
        private double averageEditIntervalMinutes;
        private int totalContentAdded;
        private int totalContentRemoved;
        private String mostActiveEditHour;
        private List<String> commonEditReasons;

        public EditStatistics() {
        }

        public int getTotalEdits() {
            return totalEdits;
        }

        public void setTotalEdits(int totalEdits) {
            this.totalEdits = totalEdits;
        }

        public int getEditsByOwner() {
            return editsByOwner;
        }

        public void setEditsByOwner(int editsByOwner) {
            this.editsByOwner = editsByOwner;
        }

        public int getEditsByAdmin() {
            return editsByAdmin;
        }

        public void setEditsByAdmin(int editsByAdmin) {
            this.editsByAdmin = editsByAdmin;
        }

        public double getAverageEditIntervalMinutes() {
            return averageEditIntervalMinutes;
        }

        public void setAverageEditIntervalMinutes(double averageEditIntervalMinutes) {
            this.averageEditIntervalMinutes = averageEditIntervalMinutes;
        }

        public int getTotalContentAdded() {
            return totalContentAdded;
        }

        public void setTotalContentAdded(int totalContentAdded) {
            this.totalContentAdded = totalContentAdded;
        }

        public int getTotalContentRemoved() {
            return totalContentRemoved;
        }

        public void setTotalContentRemoved(int totalContentRemoved) {
            this.totalContentRemoved = totalContentRemoved;
        }

        public String getMostActiveEditHour() {
            return mostActiveEditHour;
        }

        public void setMostActiveEditHour(String mostActiveEditHour) {
            this.mostActiveEditHour = mostActiveEditHour;
        }

        public List<String> getCommonEditReasons() {
            return commonEditReasons;
        }

        public void setCommonEditReasons(List<String> commonEditReasons) {
            this.commonEditReasons = commonEditReasons;
        }
    }

    // ==================== Getter & Setter ====================

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getCurrentContent() {
        return currentContent;
    }

    public void setCurrentContent(String currentContent) {
        this.currentContent = currentContent;
    }

    public String getOriginalContent() {
        return originalContent;
    }

    public void setOriginalContent(String originalContent) {
        this.originalContent = originalContent;
    }

    public Integer getTotalEditCount() {
        return totalEditCount;
    }

    public void setTotalEditCount(Integer totalEditCount) {
        this.totalEditCount = totalEditCount;
    }

    public LocalDateTime getLastEditedAt() {
        return lastEditedAt;
    }

    public void setLastEditedAt(LocalDateTime lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
    }

    public UserSummaryDTO getLastEditedBy() {
        return lastEditedBy;
    }

    public void setLastEditedBy(UserSummaryDTO lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    public Boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(Boolean canEdit) {
        this.canEdit = canEdit;
    }

    public String getCannotEditReason() {
        return cannotEditReason;
    }

    public void setCannotEditReason(String cannotEditReason) {
        this.cannotEditReason = cannotEditReason;
    }

    public List<EditHistoryItem> getEditHistory() {
        return editHistory;
    }

    public void setEditHistory(List<EditHistoryItem> editHistory) {
        this.editHistory = editHistory;
    }

    public Integer getEditTimeWindowMinutes() {
        return editTimeWindowMinutes;
    }

    public void setEditTimeWindowMinutes(Integer editTimeWindowMinutes) {
        this.editTimeWindowMinutes = editTimeWindowMinutes;
    }

    public Integer getMaxEditCount() {
        return maxEditCount;
    }

    public void setMaxEditCount(Integer maxEditCount) {
        this.maxEditCount = maxEditCount;
    }

    public EditStatistics getStatistics() {
        return statistics;
    }

    public void setStatistics(EditStatistics statistics) {
        this.statistics = statistics;
    }
}
