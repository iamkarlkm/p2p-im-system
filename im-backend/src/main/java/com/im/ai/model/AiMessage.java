package com.im.ai.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI消息
 */
@Data
@Builder
public class AiMessage {
    
    /**
     * 消息角色
     */
    private MessageRole role;
    
    /**
     * 消息内容
     */
    private String content;
    
    /**
     * 消息时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 消息元数据
     */
    private java.util.Map<String, Object> metadata;
}
