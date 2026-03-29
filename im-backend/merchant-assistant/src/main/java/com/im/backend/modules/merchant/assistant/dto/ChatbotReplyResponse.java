package com.im.backend.modules.merchant.assistant.dto;

import lombok.Data;

/**
 * 智能客服回复响应
 */
@Data
public class ChatbotReplyResponse {
    
    /**
     * 回复内容
     */
    private String replyContent;
    
    /**
     * 匹配的知识库ID
     */
    private Long knowledgeId;
    
    /**
     * 意图标签
     */
    private String intentTag;
    
    /**
     * 置信度
     */
    private Double confidence;
    
    /**
     * 是否需要转人工
     */
    private Boolean needTransfer;
    
    /**
     * 推荐问题列表
     */
    private String[] suggestedQuestions;
}
