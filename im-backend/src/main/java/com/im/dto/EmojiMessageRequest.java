package com.im.dto;

/**
 * 发送表情消息请求DTO
 * 功能#23: 表情消息
 */
public class EmojiMessageRequest {

    private String emojiCode;
    private String emojiType;
    private String emojiUrl;
    private String emojiName;
    private String emojiCategory;
    private Boolean isCustom;
    private Boolean isAnimated;
    private Long receiverId;
    private Long groupId;
    private String conversationType;

    public String getEmojiCode() {
        return emojiCode;
    }

    public void setEmojiCode(String emojiCode) {
        this.emojiCode = emojiCode;
    }

    public String getEmojiType() {
        return emojiType;
    }

    public void setEmojiType(String emojiType) {
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

    public String getConversationType() {
        return conversationType;
    }

    public void setConversationType(String conversationType) {
        this.conversationType = conversationType;
    }
}
