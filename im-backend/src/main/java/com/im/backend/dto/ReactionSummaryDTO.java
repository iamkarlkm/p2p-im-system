package com.im.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息表情回应汇总统计DTO
 * 用于展示消息的所有表情回应统计信息
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-27
 */
public class ReactionSummaryDTO {

    private Long messageId;
    private Long conversationId;
    private Long totalReactions;
    private Integer uniqueEmojiCount;
    private List<EmojiCountDTO> emojiCounts;
    private List<ReactionUserDTO> recentUsers;
    private Boolean hasCurrentUserReacted;
    private String currentUserEmoji;

    // 构造函数
    public ReactionSummaryDTO() {
    }

    public ReactionSummaryDTO(Long messageId, Long conversationId) {
        this.messageId = messageId;
        this.conversationId = conversationId;
    }

    // Getters and Setters
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

    public Long getTotalReactions() {
        return totalReactions;
    }

    public void setTotalReactions(Long totalReactions) {
        this.totalReactions = totalReactions;
    }

    public Integer getUniqueEmojiCount() {
        return uniqueEmojiCount;
    }

    public void setUniqueEmojiCount(Integer uniqueEmojiCount) {
        this.uniqueEmojiCount = uniqueEmojiCount;
    }

    public List<EmojiCountDTO> getEmojiCounts() {
        return emojiCounts;
    }

    public void setEmojiCounts(List<EmojiCountDTO> emojiCounts) {
        this.emojiCounts = emojiCounts;
    }

    public List<ReactionUserDTO> getRecentUsers() {
        return recentUsers;
    }

    public void setRecentUsers(List<ReactionUserDTO> recentUsers) {
        this.recentUsers = recentUsers;
    }

    public Boolean getHasCurrentUserReacted() {
        return hasCurrentUserReacted;
    }

    public void setHasCurrentUserReacted(Boolean hasCurrentUserReacted) {
        this.hasCurrentUserReacted = hasCurrentUserReacted;
    }

    public String getCurrentUserEmoji() {
        return currentUserEmoji;
    }

    public void setCurrentUserEmoji(String currentUserEmoji) {
        this.currentUserEmoji = currentUserEmoji;
    }

    /**
     * 表情计数DTO
     */
    public static class EmojiCountDTO {
        private String emojiCode;
        private String emojiDescription;
        private Long count;
        private List<Long> userIds;
        private Boolean isCurrentUserIncluded;

        public EmojiCountDTO() {
        }

        public EmojiCountDTO(String emojiCode, Long count) {
            this.emojiCode = emojiCode;
            this.count = count;
        }

        // Getters and Setters
        public String getEmojiCode() {
            return emojiCode;
        }

        public void setEmojiCode(String emojiCode) {
            this.emojiCode = emojiCode;
        }

        public String getEmojiDescription() {
            return emojiDescription;
        }

        public void setEmojiDescription(String emojiDescription) {
            this.emojiDescription = emojiDescription;
        }

        public Long getCount() {
            return count;
        }

        public void setCount(Long count) {
            this.count = count;
        }

        public List<Long> getUserIds() {
            return userIds;
        }

        public void setUserIds(List<Long> userIds) {
            this.userIds = userIds;
        }

        public Boolean getIsCurrentUserIncluded() {
            return isCurrentUserIncluded;
        }

        public void setIsCurrentUserIncluded(Boolean isCurrentUserIncluded) {
            this.isCurrentUserIncluded = isCurrentUserIncluded;
        }
    }

    /**
     * 反应用户DTO
     */
    public static class ReactionUserDTO {
        private Long userId;
        private String userName;
        private String userAvatar;
        private String emojiCode;
        private LocalDateTime reactedAt;

        public ReactionUserDTO() {
        }

        // Getters and Setters
        public Long getUserId() {
            return userId;
        }

        public void setUserId(Long userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserAvatar() {
            return userAvatar;
        }

        public void setUserAvatar(String userAvatar) {
            this.userAvatar = userAvatar;
        }

        public String getEmojiCode() {
            return emojiCode;
        }

        public void setEmojiCode(String emojiCode) {
            this.emojiCode = emojiCode;
        }

        public LocalDateTime getReactedAt() {
            return reactedAt;
        }

        public void setReactedAt(LocalDateTime reactedAt) {
            this.reactedAt = reactedAt;
        }
    }

    @Override
    public String toString() {
        return "ReactionSummaryDTO{" +
                "messageId=" + messageId +
                ", totalReactions=" + totalReactions +
                ", uniqueEmojiCount=" + uniqueEmojiCount +
                '}';
    }
}
