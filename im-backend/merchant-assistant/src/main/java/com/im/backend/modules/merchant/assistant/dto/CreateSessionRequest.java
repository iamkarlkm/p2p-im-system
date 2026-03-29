package com.im.backend.modules.merchant.assistant.dto;

import lombok.Data;

/**
 * 创建客服会话请求
 */
@Data
public class CreateSessionRequest {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话来源
     */
    private String source;
    
    /**
     * 用户初始消息
     */
    private String initialMessage;
}
