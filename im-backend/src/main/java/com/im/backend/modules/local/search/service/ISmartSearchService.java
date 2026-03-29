package com.im.backend.modules.local.search.service;

import com.im.backend.modules.local.search.dto.*;

import java.util.List;

/**
 * 智能搜索服务接口
 */
public interface ISmartSearchService {

    /**
     * 语义搜索
     */
    SemanticSearchResponse semanticSearch(SemanticSearchRequest request);

    /**
     * 自然语言理解解析
     */
    NluParseResponse parseNaturalLanguage(NluParseRequest request);

    /**
     * 获取搜索建议
     */
    List<String> getSearchSuggestions(SearchSuggestionRequest request);

    /**
     * 获取热门搜索词
     */
    HotKeywordResponse getHotKeywords(String category, Integer limit);

    /**
     * 知识图谱查询
     */
    KnowledgeGraphResponse queryKnowledgeGraph(String entityName);

    /**
     * 搜索纠错
     */
    String correctSearchQuery(String query);

    /**
     * 扩展同义词
     */
    List<String> expandSynonyms(String keyword);
}
