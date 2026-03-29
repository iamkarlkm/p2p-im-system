package com.im.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 消息信息DTO
 */
@Schema(description = "消息信息")
public class MessageDTO {

    @Schema(description = "消息ID", example = "1")
    private Long id;

    @Schema(description = "发送者ID", example = "1")
    private Long senderId;

    @Schema(description = "发送者用户名")
    private String senderUsername;

    @Schema(description = "发送者昵称")
    private String senderNickname;

    @Schema(description = "发送者头像")
    private String senderAvatar;

    @Schema(description = "接收者ID", example = "2")
    private Long receiverId;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "消息类型", example = "TEXT")
    private String type;

    @Schema(description = "是否已读", example = "false")
    private boolean isRead;

    @Schema(description = "消息创建时间")
    private LocalDateTime createdAt;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderUsername() {
        return senderUsername;
    }

    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
