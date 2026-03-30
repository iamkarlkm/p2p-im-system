package com.im.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 表情消息实体
 * 功能#23: 表情消息
 */
@Entity
@Table(name = "emoji_message")
public class EmojiMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    private Long messageId;

    @Column(name = "emoji_code", nullable = false, length = 100)
    private String emojiCode;

    @Column(name = "emoji_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private EmojiType emojiType;

    @Column(name = "emoji_url", length = 500)
    private String emojiUrl;

    @Column(name = "emoji_name", length = 100)
    private String emojiName;

    @Column(name = "emoji_category", length = 50)
    private String emojiCategory;

    @Column(name = "is_custom")
    private Boolean isCustom = false;

    @Column(name = "is_animated")
    private Boolean isAnimated = false;

    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    @Column(name = "receiver_id")
    private Long receiverId;

    @Column(name = "group_id")
    private Long groupId;

    @Column(name = "conversation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConversationType conversationType;

    @Column(name = "send_time", nullable = false)
    private LocalDateTime sendTime;

    @Column(name = "is_read")
    private Boolean isRead = false;

    @PrePersist
    protected void onCreate() {
        sendTime = LocalDateTime.now();
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

    public String getEmojiCode() {
        return emojiCode;
    }

    public void setEmojiCode(String emojiCode) {
        this.emojiCode = emojiCode;
    }

    public EmojiType getEmojiType() {
        return emojiType;
    }

    public void setEmojiType(EmojiType emojiType) {
        this.emojiType = emojiType;
    }

    public String getEmojiUrl() {
        return emojiUrl;
    }

    public void setEmojiUrl(String emojiUrl) {
        this.emojiUrl = emojiUrl;
    }

    public String getEmojiName() {
        return emojiName;
    }

    public void setEmojiName(String emojiName) {
        this.emojiName = emojiName;
    }

    public String getEmojiCategory() {
        return emojiCategory;
    }

    public void setEmojiCategory(String emojiCategory) {
        this.emojiCategory = emojiCategory;
    }

    public Boolean getIsCustom() {
        return isCustom;
    }

    public void setIsCustom(Boolean isCustom) {
        this.isCustom = isCustom;
    }

    public Boolean getIsAnimated() {
        return isAnimated;
    }

    public void setIsAnimated(Boolean isAnimated) {
        this.isAnimated = isAnimated;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public ConversationType getConversationType() {
        return conversationType;
    }

    public void setConversationType(ConversationType conversationType) {
        this.conversationType = conversationType;
    }

    public LocalDateTime getSendTime() {
        return sendTime;
    }

    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }
}
