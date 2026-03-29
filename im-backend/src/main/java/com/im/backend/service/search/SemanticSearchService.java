package com.im.backend.service.search;

import com.im.backend.dto.search.SemanticSearchRequestDTO;
import com.im.backend.dto.search.SemanticSearchResponseDTO;
import com.im.backend.entity.search.ConversationSession;
import com.im.backend.entity.search.SearchIntent;
import com.im.backend.entity.search.SemanticQuery;

import java.util.List;
import java.util.Map;

/**
 * 语义搜索服务接口
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
public interface SemanticSearchService {
    
    /**
     * 执行语义搜索
     * 
     * @param request 搜索请求
     * @param userId 用户ID
     * @return 搜索响应
     */
    SemanticSearchResponseDTO semanticSearch(SemanticSearchRequestDTO request, Long userId);
    
    /**
     * 解析查询意图
     * 
     * @param query 用户查询
     * @param context 上下文信息
     * @return 搜索意图
     */
    SearchIntent parseIntent(String query, Map<String, Object> context);
    
    /**
     * 理解自然语言查询
     * 
     * @param query 原始查询
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 语义查询对象
     */
    SemanticQuery understandQuery(String query, Long userId, String sessionId);
    
    /**
     * 构建Elasticsearch查询
     * 
     * @param semanticQuery 语义查询
     * @return ES查询条件
     */
    Map<String, Object> buildElasticsearchQuery(SemanticQuery semanticQuery);
    
    /**
     * 执行多轮对话搜索
     * 
     * @param request 搜索请求
     * @param session 对话会话
     * @param userId 用户ID
     * @return 搜索响应
     */
    SemanticSearchResponseDTO multiTurnSearch(SemanticSearchRequestDTO request, ConversationSession session, Long userId);
    
    /**
     * 获取搜索建议
     * 
     * @param query 部分查询
     * @param userId 用户ID
     * @return 建议列表
     */
    List<String> getSearchSuggestions(String query, Long userId);
    
    /**
     * 识别语音查询
     * 
     * @param voiceData 语音数据
     * @return 识别文本
     */
    String recognizeVoiceQuery(String voiceData);
    
    /**
     * 生成澄清问题
     * 
     * @param intent 搜索意图
     * @return 澄清问题
     */
    String generateClarificationQuestion(SearchIntent intent);
    
    /**
     * 智能排序结果
     * 
     * @param results 原始结果
     * @param semanticQuery 语义查询
     * @param userId 用户ID
     * @return 排序后结果
     */
    List<SemanticSearchResponseDTO.SearchResultDTO> smartRankResults(
            List<SemanticSearchResponseDTO.SearchResultDTO> results, 
            SemanticQuery semanticQuery, 
            Long userId);
    
    /**
     * 记录搜索历史
     * 
     * @param query 查询
     * @param userId 用户ID
     * @param resultCount 结果数
     */
    void recordSearchHistory(String query, Long userId, int resultCount);
    
    /**
     * 获取热门搜索
     * 
     * @param cityCode 城市代码
     * @param limit 数量限制
     * @return 热门搜索列表
     */
    List<String> getHotSearches(String cityCode, int limit);
    
    /**
     * 获取个性化推荐查询
     * 
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 推荐查询列表
     */
    List<String> getPersonalizedSuggestions(Long userId, int limit);
}
