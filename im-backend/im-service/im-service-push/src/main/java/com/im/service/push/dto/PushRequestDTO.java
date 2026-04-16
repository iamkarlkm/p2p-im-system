package com.im.service.push.dto;

import lombok.Data;

/**
 * 推送请求 DTO
 */
@Data
public class PushRequestDTO {
    private String userId;
    private String title;
    private String body;
    private String notificationType;
    private String senderId;
    private String senderName;
    private String conversationId;
    private String messageId;
    private String priority;
}
