package com.im.backend.modules.merchant.assistant.dto;

import lombok.Data;
import java.util.List;

/**
 * 智能客服回复请求
 */
@Data
public class ChatbotReplyRequest {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 用户消息
     */
    private String userMessage;
    
    /**
     * 上下文消息列表
     */
    private List<String> contextMessages;
    
    /**
     * 商户ID
     */
    private Long merchantId;
}
