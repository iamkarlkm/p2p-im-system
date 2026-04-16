package com.im.service.push.dto;

import lombok.Data;
import java.util.List;

/**
 * 批量推送请求 DTO
 */
@Data
public class BatchPushRequestDTO {
    private List<String> userIds;
    private String title;
    private String body;
    private String notificationType;
    private String senderId;
    private String senderName;
    private String conversationId;
    private String messageId;
}
