package com.im.modules.merchant.automation.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客服会话历史DTO
 */
@Data
@Builder
public class ChatSessionHistoryResponse {
    
    private String sessionId;
    
    private String merchantId;
    
    private String userId;
    
    private String userNickname;
    
    private String userAvatar;
    
    private Integer status;
    
    private String statusName;
    
    private String agentId;
    
    private String agentName;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer messageCount;
    
    private Integer unreadCount;
    
    private LocalDateTime lastMessageTime;
    
    private String lastMessagePreview;
    
    private List<ChatMessage> messages;
    
    @Data
    @Builder
    public static class ChatMessage {
        private String messageId;
        private String senderType;
        private String senderId;
        private String senderName;
        private String content;
        private String messageType;
        private LocalDateTime sendTime;
        private Boolean isRead;
    }
}
