package com.im.backend.modules.local.service;

import com.im.backend.modules.local.dto.PersonalizedRecommendationRequest;
import com.im.backend.modules.local.dto.PersonalizedRecommendationResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 个性化推荐服务接口
 * 提供多路召回推荐引擎和智能排序
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
public interface PersonalizedRecommendationService {
    
    /**
     * 获取个性化推荐
     * 
     * @param request 推荐请求
     * @return 推荐响应
     */
    PersonalizedRecommendationResponse getRecommendations(PersonalizedRecommendationRequest request);
    
    /**
     * 异步获取个性化推荐
     * 
     * @param request 推荐请求
     * @return CompletableFuture包装的响应
     */
    CompletableFuture<PersonalizedRecommendationResponse> getRecommendationsAsync(PersonalizedRecommendationRequest request);
    
    /**
     * Geo召回 - 基于地理位置的推荐
     * 
     * @param userId 用户ID
     * @param latitude 纬度
     * @param longitude 经度
     * @param radius 半径
     * @param limit 数量限制
     * @return 推荐项列表
     */
    List<PersonalizedRecommendationResponse.RecommendationItem> geoRecall(
            String userId, Double latitude, Double longitude, Integer radius, Integer limit);
    
    /**
     * 热门召回 - 基于区域热度的推荐
     * 
     * @param userId 用户ID
     * @param latitude 纬度
     * @param longitude 经度
     * @param limit 数量限制
     * @return 推荐项列表
     */
    List<PersonalizedRecommendationResponse.RecommendationItem> hotRecall(
            String userId, Double latitude, Double longitude, Integer limit);
    
    /**
     * 协同过滤召回 - 基于相似用户的推荐
     * 
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 推荐项列表
     */
    List<PersonalizedRecommendationResponse.RecommendationItem> collaborativeFilteringRecall(
            String userId, Integer limit);
    
    /**
     * 向量召回 - 基于Embedding相似度的推荐
     * 
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 推荐项列表
     */
    List<PersonalizedRecommendationResponse.RecommendationItem> vectorRecall(
            String userId, Integer limit);
    
    /**
     * 智能排序
     * 
     * @param items 召回的候选集
     * @param userId 用户ID
     * @param strategy 排序策略
     * @return 排序后的列表
     */
    List<PersonalizedRecommendationResponse.RecommendationItem> smartRank(
            List<PersonalizedRecommendationResponse.RecommendationItem> items, String userId, String strategy);
    
    /**
     * 去重处理
     * 
     * @param items 候选集
     * @param exposedItems 已曝光列表
     * @return 去重后的列表
     */
    List<PersonalizedRecommendationResponse.RecommendationItem> deduplicate(
            List<PersonalizedRecommendationResponse.RecommendationItem> items, List<String> exposedItems);
    
    /**
     * 记录用户反馈（点击/收藏/忽略）
     * 
     * @param userId 用户ID
     * @param itemId 内容ID
     * @param action 行为类型：click/favorite/skip
     * @param context 上下文信息
     */
    void recordFeedback(String userId, String itemId, String action, String context);
    
    /**
     * 刷新推荐
     * 
     * @param request 推荐请求
     * @param excludeIds 排除的内容ID
     * @return 新的推荐列表
     */
    PersonalizedRecommendationResponse refreshRecommendations(
            PersonalizedRecommendationRequest request, List<String> excludeIds);
}
