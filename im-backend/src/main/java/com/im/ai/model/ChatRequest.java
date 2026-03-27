package com.im.ai.model;

import lombok.Data;

/**
 * 聊天请求
 */
@Data
public class ChatRequest {
    
    /**
     * 用户消息
     */
    private String message;
    
    /**
     * 可选: 指定人格
     */
    private String personality;
    
    /**
     * 可选: 上下文信息
     */
    private java.util.Map<String, Object> context;
}
