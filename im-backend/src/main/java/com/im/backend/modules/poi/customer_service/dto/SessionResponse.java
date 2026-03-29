package com.im.backend.modules.poi.customer_service.dto;

import lombok.Data;

/**
 * 客服会话响应DTO
 */
@Data
public class SessionResponse {

    private String sessionId;
    private Long poiId;
    private String merchantName;
    private Long userId;
    private Long agentId;
    private String agentNickname;
    private String agentAvatar;
    private String status;
    private String inquiryType;
    private String subject;
    private String firstMessagePreview;
    private String lastMessagePreview;
    private String lastMessageTime;
    private Integer userUnreadCount;
    private Boolean robotHandled;
    private String createTime;
}
