package com.im.dto.customer_service;

import lombok.Data;
import java.util.List;

/**
 * 机器人对话响应DTO
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class BotChatResponse {
    
    /** 回复消息 */
    private String reply;
    
    /** 意图类型 */
    private String intentType;
    
    /** 意图名称 */
    private String intentName;
    
    /** 置信度 */
    private Double confidence;
    
    /** 是否命中知识库 */
    private Boolean hitKnowledge;
    
    /** 相关知识点 */
    private List<KnowledgeItem> relatedKnowledges;
    
    /** 是否需要转人工 */
    private Boolean needTransfer;
    
    /** 转人工原因 */
    private String transferReason;
    
    /** 推荐操作 */
    private List<String> suggestedActions;
    
    /** 会话ID */
    private Long sessionId;
    
    /** 上下文ID */
    private String contextId;
    
    @Data
    public static class KnowledgeItem {
        private Long knowledgeId;
        private String question;
        private String answer;
        private Double relevance;
    }
}
