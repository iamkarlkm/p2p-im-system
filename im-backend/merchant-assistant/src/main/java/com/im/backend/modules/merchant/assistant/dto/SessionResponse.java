package com.im.backend.modules.merchant.assistant.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 会话响应
 */
@Data
public class SessionResponse {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 会话状态
     */
    private String sessionStatus;
    
    /**
     * 当前服务者类型
     */
    private String currentHandler;
    
    /**
     * 欢迎消息
     */
    private String welcomeMessage;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
