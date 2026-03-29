package com.im.modules.merchant.automation.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 智能客服消息响应DTO
 * 用于返回AI客服机器人的回复
 */
@Data
@Builder
public class ChatbotMessageResponse {
    
    private String messageId;
    
    private String sessionId;
    
    private String merchantId;
    
    private String userId;
    
    private String replyContent;
    
    private String replyType;
    
    private List<String> suggestedQuestions;
    
    private List<QuickAction> quickActions;
    
    private Boolean needTransferToHuman;
    
    private String transferReason;
    
    private Double confidence;
    
    private String intent;
    
    private LocalDateTime replyTime;
    
    private Map<String, Object> extraData;
    
    @Data
    @Builder
    public static class QuickAction {
        private String action;
        private String label;
        private String value;
        private String icon;
    }
}
