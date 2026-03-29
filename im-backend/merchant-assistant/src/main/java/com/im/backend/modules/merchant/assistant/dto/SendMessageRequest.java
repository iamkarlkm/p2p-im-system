package com.im.backend.modules.merchant.assistant.dto;

import lombok.Data;

/**
 * 发送消息请求
 */
@Data
public class SendMessageRequest {
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 发送者ID
     */
    private Long senderId;
    
    /**
     * 发送者类型
     */
    private String senderType;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 富媒体内容JSON
     */
    private String mediaContent;
}
