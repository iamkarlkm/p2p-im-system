package com.im.ai.model;

/**
 * 意图类型枚举
 */
public enum IntentType {
    
    /**
     * 知识查询
     */
    KNOWLEDGE_QUERY,
    
    /**
     * 任务执行
     */
    TASK_EXECUTION,
    
    /**
     * 日常对话
     */
    CONVERSATIONAL,
    
    /**
     * 问候语
     */
    GREETING,
    
    /**
     * 告别语
     */
    GOODBYE,
    
    /**
     * 帮助请求
     */
    HELP,
    
    /**
     * 感谢
     */
    THANKS,
    
    /**
     * 道歉
     */
    APOLOGY,
    
    /**
     * 确认/肯定
     */
    CONFIRMATION,
    
    /**
     * 否定/拒绝
     */
    NEGATION,
    
    /**
     * 未知意图
     */
    UNKNOWN
}
