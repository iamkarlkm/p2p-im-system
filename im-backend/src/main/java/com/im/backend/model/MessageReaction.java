package com.im.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 消息表情回应实体类
 * 存储用户对消息的表情符号回应
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-27
 */
@Entity
@Table(name = "message_reactions", indexes = {
    @Index(name = "idx_message_id", columnList = "messageId"),
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_conversation_id", columnList = "conversationId"),
    @Index(name = "idx_emoji_code", columnList = "emojiCode"),
    @Index(name = "idx_message_emoji", columnList = "messageId, emojiCode"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class MessageReaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "消息ID不能为空")
    @Column(name = "message_id", nullable = false)
    private Long messageId;

    @NotNull(message = "用户ID不能为空")
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @NotNull(message = "会话ID不能为空")
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;

    @NotBlank(message = "表情符号代码不能为空")
    @Size(max = 50, message = "表情符号代码长度不能超过50")
    @Column(name = "emoji_code", nullable = false, length = 50)
    private String emojiCode;

    @Size(max = 100, message = "表情符号描述长度不能超过100")
    @Column(name = "emoji_description", length = 100)
    private String emojiDescription;

    @Column(name = "skin_tone")
    private Integer skinTone;

    @NotNull(message = "反应类型不能为空")
    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", nullable = false, length = 20)
    private ReactionType reactionType;

    @Column(name = "is_anonymous")
    private Boolean isAnonymous = false;

    @Column(name = "client_message_id", length = 100)
    private String clientMessageId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Version
    @Column(name = "version")
    private Long version;

    /**
     * 反应类型枚举
     */
    public enum ReactionType {
        EMOJI,           // 标准表情符号
        CUSTOM_EMOJI,    // 自定义表情
        STICKER,         // 贴纸
        SHORTCUT         // 快捷反应
    }

    // 构造函数
    public MessageReaction() {
    }

    public MessageReaction(Long messageId, Long userId, Long conversationId, 
                          String emojiCode, ReactionType reactionType) {
        this.messageId = messageId;
        this.userId = userId;
        this.conversationId = conversationId;
        this.emojiCode = emojiCode;
        this.reactionType = reactionType;
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

    public ReactionType getReactionType() {
        return reactionType;
    }

    public void setReactionType(ReactionType reactionType) {
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

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * 软删除
     */
    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * 恢复删除
     */
    public void restore() {
        this.isDeleted = false;
        this.deletedAt = null;
    }

    /**
     * 检查是否有效（未删除）
     */
    public boolean isValid() {
        return !Boolean.TRUE.equals(this.isDeleted);
    }

    @Override
    public String toString() {
        return "MessageReaction{" +
                "id=" + id +
                ", messageId=" + messageId +
                ", userId=" + userId +
                ", emojiCode='" + emojiCode + '\'' +
                ", reactionType=" + reactionType +
                ", createdAt=" + createdAt +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageReaction that = (MessageReaction) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
