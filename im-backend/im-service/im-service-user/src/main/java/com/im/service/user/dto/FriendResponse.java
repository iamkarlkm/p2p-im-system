package com.im.service.user.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 好友响应DTO
 */
@Data
public class FriendResponse {

    private Long id;
    private Long userId;
    private Long friendId;
    private String friendUsername;
    private String friendNickname;
    private String friendAvatarUrl;
    private String status;
    private String source;
    private String applyMessage;
    private String rejectReason;
    private String remark;
    private List<String> tags;
    private Boolean starred;
    private LocalDateTime starredAt;
    private Boolean pinned;
    private LocalDateTime pinnedAt;
    private Boolean muteNotifications;
    private Boolean blocked;
    private LocalDateTime blockedAt;
    private LocalDateTime becameFriendsAt;
    private LocalDateTime lastChatAt;
    private LocalDateTime createdAt;
    private String friendOnlineStatus;
    private LocalDateTime friendLastOnlineAt;
}
