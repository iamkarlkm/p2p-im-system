package com.im.backend.modules.local.search.service;

import com.im.backend.modules.local.search.dto.*;
import com.im.backend.modules.local.search.entity.*;

import java.util.List;
import java.util.Map;

/**
 * 本地生活智能搜索服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface LocalSmartSearchService {
    
    /**
     * 执行智能搜索
     * 
     * @param request 搜索请求
     * @return 搜索结果
     */
    SmartSearchResponse smartSearch(SmartSearchRequest request);
    
    /**
     * 执行语义搜索
     * 
     * @param request 语义搜索请求
     * @return 语义搜索结果
     */
    SemanticSearchResponse semanticSearch(SemanticSearchRequest request);
    
    /**
     * 解析搜索意图
     * 
     * @param query 搜索查询
     * @param userId 用户ID
     * @return 搜索意图
     */
    SearchIntent parseSearchIntent(String query, Long userId);
    
    /**
     * 多轮对话搜索
     * 
     * @param request 搜索请求
     * @param conversationId 对话ID
     * @return 搜索结果
     */
    SmartSearchResponse multiTurnSearch(SmartSearchRequest request, String conversationId);
    
    /**
     * 获取搜索建议
     * 
     * @param prefix 输入前缀
     * @param longitude 经度
     * @param latitude 纬度
     * @param limit 返回数量
     * @return 建议列表
     */
    List<String> getSearchSuggestions(String prefix, Double longitude, Double latitude, Integer limit);
    
    /**
     * 获取热门搜索
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @param limit 返回数量
     * @return 热门搜索列表
     */
    List<String> getHotSearches(Double longitude, Double latitude, Integer limit);
    
    /**
     * 获取知识图谱推荐
     * 
     * @param poiId POI ID
     * @param limit 推荐数量
     * @return 知识图谱推荐
     */
    List<SmartSearchResponse.KnowledgeGraphRecommendationDTO> getKgRecommendations(Long poiId, Integer limit);
    
    /**
     * 记录搜索点击
     * 
     * @param queryId 查询ID
     * @param poiId 点击的POI ID
     * @param index 点击的位置
     */
    void recordSearchClick(Long queryId, Long poiId, Integer index);
    
    /**
     * 获取搜索历史
     * 
     * @param userId 用户ID
     * @param limit 返回数量
     * @return 搜索历史列表
     */
    List<LocalSearchQuery> getSearchHistory(Long userId, Integer limit);
    
    /**
     * 清空搜索历史
     * 
     * @param userId 用户ID
     */
    void clearSearchHistory(Long userId);
    
    /**
     * 删除单条搜索历史
     * 
     * @param userId 用户ID
     * @param queryId 查询ID
     */
    void deleteSearchHistory(Long userId, Long queryId);
    
    /**
     * 执行语音搜索
     * 
     * @param audioData 语音数据
     * @param longitude 经度
     * @param latitude 纬度
     * @param dialect 方言类型
     * @return 搜索结果
     */
    SmartSearchResponse voiceSearch(byte[] audioData, Double longitude, Double latitude, String dialect);
    
    /**
     * 获取搜索统计
     * 
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 统计数据
     */
    Map<String, Object> getSearchStatistics(String startTime, String endTime);
    
    /**
     * 重新排序搜索结果
     * 
     * @param results 原始结果
     * @param sortBy 排序方式
     * @param userId 用户ID
     * @return 排序后的结果
     */
    List<SmartSearchResponse.SearchResultItemDTO> reorderResults(
            List<SmartSearchResponse.SearchResultItemDTO> results, 
            String sortBy, 
            Long userId);
}
