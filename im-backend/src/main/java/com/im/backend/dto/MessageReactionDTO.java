package com.im.backend.dto;

import com.im.backend.model.MessageReaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 消息表情回应数据传输对象
 * 用于前后端数据交互
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-27
 */
public class MessageReactionDTO {

    private Long id;

    @NotNull(message = "消息ID不能为空")
    private Long messageId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    private String userName;
    private String userAvatar;

    @NotNull(message = "会话ID不能为空")
    private Long conversationId;

    @NotBlank(message = "表情符号代码不能为空")
    @Size(max = 50, message = "表情符号代码长度不能超过50")
    private String emojiCode;

    @Size(max = 100, message = "表情符号描述长度不能超过100")
    private String emojiDescription;

    private Integer skinTone;

    @NotNull(message = "反应类型不能为空")
    private MessageReaction.ReactionType reactionType;

    private Boolean isAnonymous;

    private String clientMessageId;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 额外字段（用于展示）
    private Long reactionCount;
    private Boolean isCurrentUserReaction;

    // 构造函数
    public MessageReactionDTO() {
    }

    // Getters and Setters
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

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

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

    public Integer getSkinTone() {
        return skinTone;
    }

    public void setSkinTone(Integer skinTone) {
        this.skinTone = skinTone;
    }

    public MessageReaction.ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(MessageReaction.ReactionType reactionType) {
        this.reactionType = reactionType;
    }

    public Boolean getIsAnonymous() {
        return isAnonymous;
    }

    public void setIsAnonymous(Boolean isAnonymous) {
        this.isAnonymous = isAnonymous;
    }

    public String getClientMessageId() {
        return clientMessageId;
    }

    public void setClientMessageId(String clientMessageId) {
        this.clientMessageId = clientMessageId;
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

    public Long getReactionCount() {
        return reactionCount;
    }

    public void setReactionCount(Long reactionCount) {
        this.reactionCount = reactionCount;
    }

    public Boolean getIsCurrentUserReaction() {
        return isCurrentUserReaction;
    }

    public void setIsCurrentUserReaction(Boolean isCurrentUserReaction) {
        this.isCurrentUserReaction = isCurrentUserReaction;
    }

    /**
     * 从实体转换为DTO
     */
    public static MessageReactionDTO fromEntity(MessageReaction reaction) {
        if (reaction == null) {
            return null;
        }
        MessageReactionDTO dto = new MessageReactionDTO();
        dto.setId(reaction.getId());
        dto.setMessageId(reaction.getMessageId());
        dto.setUserId(reaction.getUserId());
        dto.setConversationId(reaction.getConversationId());
        dto.setEmojiCode(reaction.getEmojiCode());
        dto.setEmojiDescription(reaction.getEmojiDescription());
        dto.setSkinTone(reaction.getSkinTone());
        dto.setReactionType(reaction.getReactionType());
        dto.setIsAnonymous(reaction.getIsAnonymous());
        dto.setClientMessageId(reaction.getClientMessageId());
        dto.setCreatedAt(reaction.getCreatedAt());
        dto.setUpdatedAt(reaction.getUpdatedAt());
        return dto;
    }

    /**
     * 转换为实体
     */
    public MessageReaction toEntity() {
        MessageReaction reaction = new MessageReaction();
        reaction.setId(this.id);
        reaction.setMessageId(this.messageId);
        reaction.setUserId(this.userId);
        reaction.setConversationId(this.conversationId);
        reaction.setEmojiCode(this.emojiCode);
        reaction.setEmojiDescription(this.emojiDescription);
        reaction.setSkinTone(this.skinTone);
        reaction.setReactionType(this.reactionType);
        reaction.setIsAnonymous(this.isAnonymous);
        reaction.setClientMessageId(this.clientMessageId);
        return reaction;
    }

    @Override
    public String toString() {
        return "MessageReactionDTO{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", userId=" + userId +
                ", emojiCode='" + emojiCode + '\'' +
                ", reactionType=" + reactionType +
                '}';
    }
}
