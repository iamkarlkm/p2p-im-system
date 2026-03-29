package com.im.service.customer_service;

import com.im.dto.customer_service.BotChatRequest;
import com.im.dto.customer_service.BotChatResponse;
import java.util.List;

/**
 * 智能客服机器人服务接口
 * 功能 #319 - 智能客服与工单管理系统
 */
public interface CustomerServiceBotService {
    
    /**
     * 机器人对话
     */
    BotChatResponse chat(BotChatRequest request);
    
    /**
     * 意图识别
     */
    String recognizeIntent(String message);
    
    /**
     * 查询知识库
     */
    List<BotChatResponse.KnowledgeItem> searchKnowledge(String query, Integer limit);
    
    /**
     * 是否需要转人工
     */
    Boolean needTransferToHuman(String message, String intentType);
    
    /**
     * 获取推荐回复
     */
    List<String> getSuggestedReplies(String message, String intentType);
    
    /**
     * 反馈机器人回答
     */
    void feedbackKnowledge(Long knowledgeId, Boolean isHelpful, Long userId);
}
