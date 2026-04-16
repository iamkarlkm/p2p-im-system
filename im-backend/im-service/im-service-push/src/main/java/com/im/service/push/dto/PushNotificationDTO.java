package com.im.service.push.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 推送通知 DTO
 */
@Data
public class PushNotificationDTO {
    private String notificationId;
    private String userId;
    private String deviceId;
    private String deviceToken;
    private String pushType;
    private String notificationType;
    private String status;
    private String priority;
    private String title;
    private String body;
    private String messageId;
    private String conversationId;
    private String senderId;
    private String senderName;
    private Boolean isSilent;
    private String silentType;
    private Integer badge;
    private String customData;
    private LocalDateTime expiresAt;
    private LocalDateTime scheduledAt;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime createdAt;
}
