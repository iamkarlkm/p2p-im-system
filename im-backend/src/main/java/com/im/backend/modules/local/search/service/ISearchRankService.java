package com.im.backend.modules.local.search.service;

import com.im.backend.modules.local.search.dto.*;

/**
 * 搜索排名服务接口
 */
public interface ISearchRankService {

    /**
     * 计算搜索结果的综合排序分数
     * @param distance 距离（米）
     * @param rating 评分
     * @param hotScore 热度分数
     * @param relevanceScore 相关性分数
     * @param userPreference 用户偏好分数
     * @return 综合排序分数
     */
    Double calculateRankScore(Double distance, Double rating, Double hotScore,
                               Double relevanceScore, Double userPreference);

    /**
     * 根据意图类型调整排序权重
     */
    Double adjustWeightByIntent(Double baseScore, String intentType);

    /**
     * 个性化排序
     */
    SearchResult personalizeRank(SearchResult result, Long userId);

    /**
     * 多样性控制 - 确保结果多样性
     */
    SearchResult ensureDiversity(SearchResult result);

    /**
     * A/B测试排序策略
     */
    String getRankStrategy(Long userId);
}
