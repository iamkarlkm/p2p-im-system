package com.im.ai.model;

/**
 * 响应类型枚举
 */
public enum ResponseType {
    
    /**
     * 文本回复
     */
    TEXT,
    
    /**
     * 富文本(支持Markdown)
     */
    RICH_TEXT,
    
    /**
     * 卡片形式
     */
    CARD,
    
    /**
     * 列表形式
     */
    LIST,
    
    /**
     * 用户列表
     */
    USER_LIST,
    
    /**
     * 需要确认
     */
    CONFIRMATION,
    
    /**
     * 错误
     */
    ERROR,
    
    /**
     * 需要澄清
     */
    CLARIFICATION
}
