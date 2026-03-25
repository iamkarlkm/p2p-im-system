package com.im.backend.enums;

import lombok.Getter;

/**
 * 摘要类型枚举
 */
@Getter
public enum SummaryType {
    
    /**
     * 单条消息摘要 - 对单条长消息进行摘要
     */
    SINGLE_MESSAGE("SINGLE_MESSAGE", "单条消息摘要"),
    
    /**
     * 会话摘要 - 对整个会话（群聊/私聊）进行摘要
     */
    CONVERSATION("CONVERSATION", "会话摘要"),
    
    /**
     * 群聊摘要 - 专门针对群聊的摘要
     */
    GROUP_CONVERSATION("GROUP_CONVERSATION", "群聊摘要"),
    
    /**
     * 私聊摘要 - 专门针对私聊的摘要
     */
    PRIVATE_CONVERSATION("PRIVATE_CONVERSATION", "私聊摘要"),
    
    /**
     * 主题摘要 - 基于主题/话题的摘要
     */
    TOPIC("TOPIC", "主题摘要"),
    
    /**
     * 时间范围摘要 - 特定时间段内的摘要
     */
    TIME_RANGE("TIME_RANGE", "时间范围摘要"),
    
    /**
     * 用户发言摘要 - 特定用户发言的摘要
     */
    USER_SPEECH("USER_SPEECH", "用户发言摘要"),
    
    /**
     * 关键决策摘要 - 对话中的关键决策点摘要
     */
    KEY_DECISIONS("KEY_DECISIONS", "关键决策摘要"),
    
    /**
     * 行动计划摘要 - 对话中的行动计划摘要
     */
    ACTION_PLAN("ACTION_PLAN", "行动计划摘要"),
    
    /**
     * 问题解答摘要 - 问题和答案的摘要
     */
    QNA("QNA", "问题解答摘要"),
    
    /**
     * 情感摘要 - 对话情感倾向的摘要
     */
    SENTIMENT("SENTIMENT", "情感摘要"),
    
    /**
     * 多语言摘要 - 跨语言对话的摘要
     */
    MULTILINGUAL("MULTILINGUAL", "多语言摘要"),
    
    /**
     * 自定义摘要 - 用户自定义类型的摘要
     */
    CUSTOM("CUSTOM", "自定义摘要");

    private final String code;
    private final String description;

    SummaryType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static SummaryType fromCode(String code) {
        for (SummaryType type : values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的摘要类型: " + code);
    }

    /**
     * 检查是否为会话相关摘要
     */
    public boolean isConversationType() {
        return this == CONVERSATION || this == GROUP_CONVERSATION || this == PRIVATE_CONVERSATION;
    }

    /**
     * 检查是否为消息相关摘要
     */
    public boolean isMessageType() {
        return this == SINGLE_MESSAGE || this == USER_SPEECH;
    }

    /**
     * 检查是否为时间相关摘要
     */
    public boolean isTimeRelatedType() {
        return this == TIME_RANGE;
    }

    /**
     * 检查是否为分析相关摘要
     */
    public boolean isAnalysisType() {
        return this == KEY_DECISIONS || this == ACTION_PLAN || this == QNA || this == SENTIMENT;
    }

    /**
     * 检查是否支持多语言
     */
    public boolean supportsMultilingual() {
        return this == MULTILINGUAL || this == CONVERSATION || this == GROUP_CONVERSATION;
    }

    /**
     * 获取所有会话类型摘要
     */
    public static SummaryType[] getConversationTypes() {
        return new SummaryType[] {CONVERSATION, GROUP_CONVERSATION, PRIVATE_CONVERSATION};
    }

    /**
     * 获取所有分析类型摘要
     */
    public static SummaryType[] getAnalysisTypes() {
        return new SummaryType[] {KEY_DECISIONS, ACTION_PLAN, QNA, SENTIMENT};
    }
}