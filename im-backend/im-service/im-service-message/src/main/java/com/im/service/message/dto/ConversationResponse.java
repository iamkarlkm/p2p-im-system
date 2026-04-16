package com.im.service.message.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 会话响应 DTO
 */
@Data
public class ConversationResponse {
    private String id;
    private String type;
    private String name;
    private String avatar;
    private String creatorId;
    private String lastMessageId;
    private LocalDateTime lastMessageAt;
    private Integer memberCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
