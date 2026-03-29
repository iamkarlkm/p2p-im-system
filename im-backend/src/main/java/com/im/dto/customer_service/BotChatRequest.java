package com.im.dto.customer_service;

import lombok.Data;

/**
 * 机器人对话请求DTO
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class BotChatRequest {
    
    /** 会话ID */
    private Long sessionId;
    
    /** 用户ID */
    private Long userId;
    
    /** 用户消息 */
    private String message;
    
    /** 上下文会话ID */
    private String contextId;
    
    /** 商户ID */
    private Long merchantId;
}
