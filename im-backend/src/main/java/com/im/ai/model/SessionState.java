package com.im.ai.model;

/**
 * 会话状态枚举
 */
public enum SessionState {
    
    /**
     * 活跃状态
     */
    ACTIVE,
    
    /**
     * 等待用户确认
     */
    AWAITING_CONFIRMATION,
    
    /**
     * 等待澄清
     */
    AWAITING_CLARIFICATION,
    
    /**
     * 槽位填充中
     */
    SLOT_FILLING,
    
    /**
     * 已结束
     */
    ENDED,
    
    /**
     * 超时
     */
    TIMEOUT
}
