package com.im.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 单聊会话DTO
 * 功能ID: #6
 */
@Data
public class PrivateChatSessionDTO {
    private String id;
    private String user1Id;
    private String user2Id;
    private String otherUserId; // 对方用户ID
    private String otherUsername;
    private String otherNickname;
    private String otherAvatar;
    private String lastMessageContent;
    private LocalDateTime lastMessageTime;
    private Integer unreadCount;
    private Boolean pinned;
    private Boolean muted;
    private LocalDateTime createdAt;
}
