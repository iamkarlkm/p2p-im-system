package com.im.backend.modules.local_life.search.service;

import com.im.backend.modules.local_life.search.dto.*;

import java.util.List;

/**
 * 智能搜索服务接口
 */
public interface IntelligentSearchService {

    /**
     * 执行智能搜索
     *
     * @param userId  用户ID
     * @param request 搜索请求
     * @return 搜索结果
     */
    IntelligentSearchResultDTO search(Long userId, IntelligentSearchRequestDTO request);

    /**
     * 语义理解
     *
     * @param request 语义理解请求
     * @return 语义理解结果
     */
    SemanticUnderstandingResultDTO understand(SemanticUnderstandingRequestDTO request);

    /**
     * 获取搜索建议
     *
     * @param userId  用户ID
     * @param request 建议请求
     * @return 建议结果
     */
    SearchSuggestionResultDTO getSuggestions(Long userId, SearchSuggestionRequestDTO request);

    /**
     * 获取热门搜索
     *
     * @param cityCode 城市编码
     * @param limit    数量限制
     * @return 热门搜索列表
     */
    List<SearchTrendDTO> getHotSearches(String cityCode, Integer limit);

    /**
     * 获取搜索历史
     *
     * @param userId 用户ID
     * @param limit  数量限制
     * @return 搜索历史列表
     */
    List<String> getSearchHistory(Long userId, Integer limit);

    /**
     * 清除搜索历史
     *
     * @param userId 用户ID
     */
    void clearSearchHistory(Long userId);

    /**
     * 删除单条搜索历史
     *
     * @param userId   用户ID
     * @param keyword  关键词
     */
    void deleteSearchHistory(Long userId, String keyword);

    /**
     * 获取搜索发现
     *
     * @param userId   用户ID
     * @param cityCode 城市编码
     * @return 发现关键词列表
     */
    List<String> getSearchDiscovery(Long userId, String cityCode);
}
