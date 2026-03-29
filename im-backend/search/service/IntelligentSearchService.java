package com.im.search.service;

import com.im.search.dto.NaturalLanguageSearchRequestDTO;
import com.im.search.dto.SearchIntentDTO;
import com.im.search.dto.SearchResponseDTO;

import java.util.List;

/**
 * 智能搜索服务接口
 * 提供自然语言搜索、意图识别等功能
 */
public interface IntelligentSearchService {

    /**
     * 自然语言搜索
     * 支持口语化查询理解
     */
    SearchResponseDTO naturalLanguageSearch(NaturalLanguageSearchRequestDTO request);

    /**
     * 解析搜索意图
     */
    SearchIntentDTO parseSearchIntent(String query, String sessionId);

    /**
     * 获取搜索建议
     */
    List<String> getSearchSuggestions(String keyword, String cityCode);

    /**
     * 获取热门搜索
     */
    List<String> getHotSearches(String cityCode);

    /**
     * 语义搜索
     * 基于向量相似度的搜索
     */
    SearchResponseDTO semanticSearch(String query, Double longitude, Double latitude, Integer radius);

    /**
     * 多轮对话搜索
     */
    SearchResponseDTO dialogSearch(String query, String sessionId, Long userId);

    /**
     * 智能问答
     */
    String intelligentQA(String question, String poiId);
}
