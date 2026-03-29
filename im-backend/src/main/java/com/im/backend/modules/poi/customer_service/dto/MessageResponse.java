package com.im.backend.modules.poi.customer_service.dto;

import lombok.Data;

/**
 * 消息响应DTO
 */
@Data
public class MessageResponse {

    private String messageId;
    private String sessionId;
    private Long senderId;
    private String senderType;
    private String senderName;
    private String senderAvatar;
    private String messageType;
    private String content;
    private String contentExtra;
    private String quoteMessageId;
    private String status;
    private Boolean robotSent;
    private Boolean read;
    private String readTime;
    private Boolean recalled;
    private String createTime;
}
