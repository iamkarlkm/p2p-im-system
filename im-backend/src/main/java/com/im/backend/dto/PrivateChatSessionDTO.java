package com.im.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * 单聊会话信息DTO
 */
@Schema(description = "单聊会话信息")
public class PrivateChatSessionDTO {

    @Schema(description = "会话ID", example = "1")
    private Long id;

    @Schema(description = "目标用户ID", example = "2")
    private Long targetUserId;

    @Schema(description = "目标用户名", example = "zhangsan")
    private String targetUsername;

    @Schema(description = "目标用户昵称", example = "张三")
    private String targetNickname;

    @Schema(description = "目标用户头像URL")
    private String targetAvatar;

    @Schema(description = "最后一条消息内容")
    private String lastMessage;

    @Schema(description = "最后一条消息时间")
    private LocalDateTime lastMessageTime;

    @Schema(description = "是否置顶", example = "false")
    private boolean pinned;

    @Schema(description = "是否免打扰", example = "false")
    private boolean muted;

    @Schema(description = "未读消息数", example = "0")
    private int unreadCount;

    @Schema(description = "会话创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "会话更新时间")
    private LocalDateTime updatedAt;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }

    public String getTargetUsername() {
        return targetUsername;
    }

    public void setTargetUsername(String targetUsername) {
        this.targetUsername = targetUsername;
    }

    public String getTargetNickname() {
        return targetNickname;
    }

    public void setTargetNickname(String targetNickname) {
        this.targetNickname = targetNickname;
    }

    public String getTargetAvatar() {
        return targetAvatar;
    }

    public void setTargetAvatar(String targetAvatar) {
        this.targetAvatar = targetAvatar;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isMuted() {
        return muted;
    }

    public void setMuted(boolean muted) {
        this.muted = muted;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
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
}
