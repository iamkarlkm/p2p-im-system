package com.im.backend.modules.local_life.service;

import com.im.backend.modules.local_life.dto.*;

import java.util.List;

/**
 * 语义搜索服务接口
 * 处理自然语言POI搜索请求
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface SemanticSearchService {

    /**
     * 执行自然语言搜索
     *
     * @param request 搜索请求
     * @param userId 用户ID
     * @return 搜索结果列表
     */
    List<SemanticSearchResultDTO> search(NaturalLanguageSearchRequestDTO request, Long userId);

    /**
     * 执行语义搜索（基于向量的相似度搜索）
     *
     * @param query 查询文本
     * @param semanticVector 语义向量
     * @param limit 返回数量
     * @return 搜索结果列表
     */
    List<SemanticSearchResultDTO> semanticSearch(String query, String semanticVector, int limit);

    /**
     * 获取搜索建议
     *
     * @param query 部分输入
     * @param latitude 纬度
     * @param longitude 经度
     * @return 建议列表
     */
    List<String> getSearchSuggestions(String query, Double latitude, Double longitude);

    /**
     * 获取热门搜索
     *
     * @param latitude 纬度
     * @param longitude 经度
     * @param limit 数量限制
     * @return 热门搜索词列表
     */
    List<String> getHotSearches(Double latitude, Double longitude, int limit);

    /**
     * 获取搜索历史
     *
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 搜索历史列表
     */
    List<String> getSearchHistory(Long userId, int limit);

    /**
     * 清除搜索历史
     *
     * @param userId 用户ID
     */
    void clearSearchHistory(Long userId);

    /**
     * 基于会话上下文进行搜索
     *
     * @param sessionId 会话ID
     * @param query 当前查询
     * @return 搜索结果
     */
    List<SemanticSearchResultDTO> searchWithContext(String sessionId, String query);

    /**
     * 获取零结果时的推荐
     *
     * @param query 原查询
     * @return 推荐结果
     */
    List<SemanticSearchResultDTO> getZeroResultRecommendations(String query);
}
