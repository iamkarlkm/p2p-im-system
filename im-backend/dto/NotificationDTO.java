package com.im.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private Long userId;
    private String type;
    private String title;
    private String content;
    private String refType;
    private Long refId;
    private Long conversationId;
    private Long senderId;
    private String senderNickname;
    private String senderAvatar;
    private Boolean isRead;
    private LocalDateTime readAt;
    private Boolean isHandled;
    private String handleResult;
    private String dndLevel;
    private LocalDateTime expiresAt;
    private String extraData;
    private LocalDateTime createdAt;
}
