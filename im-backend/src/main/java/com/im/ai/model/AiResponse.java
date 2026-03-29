package com.im.ai.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * AI响应对象
 */
@Data
@Builder
public class AiResponse {
    
    /**
     * 响应类型
     */
    private ResponseType type;
    
    /**
     * 响应内容
     */
    private String content;
    
    /**
     * 置信度(0-1)
     */
    private Double confidence;
    
    /**
     * 响应时间(毫秒)
     */
    private Long responseTimeMs;
    
    /**
     * 是否需要澄清
     */
    private Boolean needClarification;
    
    /**
     * 是否低置信度
     */
    private Boolean lowConfidence;
    
    /**
     * 是否结束会话
     */
    private Boolean endSession;
    
    /**
     * 是否完成操作
     */
    private Boolean actionCompleted;
    
    /**
     * 推荐选项
     */
    private List<String> suggestions;
    
    /**
     * 知识来源
     */
    private List<KnowledgeEntry> sourceKnowledge;
    
    /**
     * 相关用户结果
     */
    private List<UserSearchResult> userResults;
    
    /**
     * 确认数据
     */
    private Map<String, Object> confirmationData;
    
    /**
     * 人格类型
     */
    private String personality;
}
