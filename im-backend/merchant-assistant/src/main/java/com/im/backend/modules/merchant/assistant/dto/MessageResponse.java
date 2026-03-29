package com.im.backend.modules.merchant.assistant.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 消息响应
 */
@Data
public class MessageResponse {
    
    /**
     * 消息ID
     */
    private String messageId;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 发送者类型
     */
    private String senderType;
    
    /**
     * 发送者名称
     */
    private String senderName;
    
    /**
     * 消息类型
     */
    private String messageType;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 富媒体内容
     */
    private String mediaContent;
    
    /**
     * 是否已读
     */
    private Boolean isRead;
    
    /**
     * 意图识别结果
     */
    private String intentResult;
    
    /**
     * 置信度
     */
    private Double confidence;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
