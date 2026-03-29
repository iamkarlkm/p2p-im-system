package com.im.backend.modules.local.service;

import com.im.backend.modules.local.dto.IntelligentAssistantRequest;
import com.im.backend.modules.local.dto.IntelligentAssistantResponse;
import com.im.backend.modules.local.dto.POISemanticSearchRequest;
import com.im.backend.modules.local.dto.POISemanticSearchResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 智能对话助手服务接口
 * 提供自然语言POI搜索、智能问答、多轮对话等功能
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
public interface IntelligentAssistantService {
    
    /**
     * 处理智能对话请求
     * 核心入口方法，处理用户自然语言输入并返回智能响应
     * 
     * @param request 对话请求
     * @return 对话响应
     */
    IntelligentAssistantResponse processDialog(IntelligentAssistantRequest request);
    
    /**
     * 异步处理智能对话请求
     * 
     * @param request 对话请求
     * @return CompletableFuture包装的响应
     */
    CompletableFuture<IntelligentAssistantResponse> processDialogAsync(IntelligentAssistantRequest request);
    
    /**
     * 语义理解 - 识别用户意图
     * 
     * @param query 用户输入
     * @param context 上下文
     * @return 意图识别结果
     */
    IntentRecognitionResult recognizeIntent(String query, List<IntelligentAssistantRequest.DialogContext> context);
    
    /**
     * 实体提取
     * 
     * @param query 用户输入
     * @return 提取的实体列表
     */
    List<IntelligentAssistantResponse.ExtractedEntity> extractEntities(String query);
    
    /**
     * 生成自然语言回复
     * 
     * @param intent 意图
     * @param results 搜索结果
     * @return 自然语言回复文本
     */
    String generateNaturalReply(IntentRecognitionResult intent, List<IntelligentAssistantResponse.RecommendedPOI> results);
    
    /**
     * 多轮对话管理 - 更新对话状态
     * 
     * @param conversationId 会话ID
     * @param userQuery 用户输入
     * @param assistantReply 助手回复
     */
    void updateConversationContext(String conversationId, String userQuery, String assistantReply);
    
    /**
     * 语义POI搜索
     * 
     * @param request 搜索请求
     * @return 搜索结果
     */
    POISemanticSearchResponse semanticSearch(POISemanticSearchRequest request);
    
    /**
     * 智能POI问答
     * 
     * @param poiId POI ID
     * @param question 问题
     * @return 答案
     */
    String answerPOIQuestion(String poiId, String question);
    
    /**
     * 生成建议操作
     * 
     * @param poi 推荐的POI
     * @return 建议操作列表
     */
    List<IntelligentAssistantResponse.SuggestedAction> generateSuggestedActions(IntelligentAssistantResponse.RecommendedPOI poi);
    
    /**
     * 意图识别结果
     */
    class IntentRecognitionResult {
        private String type;
        private Double confidence;
        private String category;
        private IntelligentAssistantResponse.FilterConditions filters;
        
        // Getters and setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public IntelligentAssistantResponse.FilterConditions getFilters() { return filters; }
        public void setFilters(IntelligentAssistantResponse.FilterConditions filters) { this.filters = filters; }
    }
}
