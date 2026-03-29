package com.im.service.recommendation;

import com.im.dto.recommendation.RecommendationFeedRequestDTO;
import com.im.dto.recommendation.RecommendationFeedResponseDTO;
import com.im.entity.recommendation.RecommendationItem;
import com.im.entity.recommendation.UserRecommendationFeed;
import com.im.entity.recommendation.RecallCandidate;
import com.im.entity.recommendation.RankingFeature;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 推荐信息流服务接口
 * 提供个性化推荐信息流的核心能力
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
public interface RecommendationFeedService {
    
    /**
     * 获取个性化推荐信息流
     * 
     * @param request 推荐请求参数
     * @return 推荐信息流响应
     */
    RecommendationFeedResponseDTO getRecommendationFeed(RecommendationFeedRequestDTO request);
    
    /**
     * 异步获取推荐信息流
     * 
     * @param request 推荐请求参数
     * @return CompletableFuture包装的响应
     */
    CompletableFuture<RecommendationFeedResponseDTO> getRecommendationFeedAsync(RecommendationFeedRequestDTO request);
    
    /**
     * 获取附近推荐
     * 
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 搜索半径（米）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 推荐列表
     */
    List<RecommendationItem> getNearbyRecommendations(Double longitude, Double latitude, 
                                                       Integer radius, Integer pageNum, Integer pageSize);
    
    /**
     * 获取首页信息流
     * 
     * @param userId 用户ID
     * @param longitude 经度
     * @param latitude 纬度
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 推荐列表
     */
    List<RecommendationItem> getHomeFeed(String userId, Double longitude, Double latitude,
                                          Integer pageNum, Integer pageSize);
    
    /**
     * 获取发现页推荐
     * 
     * @param userId 用户ID
     * @param longitude 经度
     * @param latitude 纬度
     * @param category 分类（可选）
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 推荐列表
     */
    List<RecommendationItem> getDiscoverFeed(String userId, Double longitude, Double latitude,
                                              String category, Integer pageNum, Integer pageSize);
    
    /**
     * 获取猜你喜欢
     * 
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 推荐列表
     */
    List<RecommendationItem> getGuessYouLike(String userId, Integer limit);
    
    /**
     * 获取场景化推荐
     * 
     * @param userId 用户ID
     * @param sceneTag 场景标签
     * @param longitude 经度
     * @param latitude 纬度
     * @param limit 数量限制
     * @return 推荐列表
     */
    List<RecommendationItem> getSceneBasedRecommendations(String userId, String sceneTag,
                                                           Double longitude, Double latitude, Integer limit);
    
    /**
     * 获取相似推荐
     * 
     * @param itemId 参考物品ID
     * @param itemType 物品类型
     * @param limit 数量限制
     * @return 相似物品推荐列表
     */
    List<RecommendationItem> getSimilarRecommendations(String itemId, String itemType, Integer limit);
    
    /**
     * 获取相关推荐
     * 
     * @param itemId 参考物品ID
     * @param itemType 物品类型
     * @param userId 用户ID（可选）
     * @param limit 数量限制
     * @return 相关物品推荐列表
     */
    List<RecommendationItem> getRelatedRecommendations(String itemId, String itemType, 
                                                        String userId, Integer limit);
    
    /**
     * 执行多路召回
     * 
     * @param userId 用户ID
     * @param scene 场景
     * @param longitude 经度
     * @param latitude 纬度
     * @param params 额外参数
     * @return 各路召回的候选集
     */
    Map<String, List<RecallCandidate>> executeMultiChannelRecall(String userId, String scene,
                                                                  Double longitude, Double latitude,
                                                                  Map<String, Object> params);
    
    /**
     * 执行排序
     * 
     * @param candidates 候选集
     * @param userId 用户ID
     * @param scene 场景
     * @param limit 返回数量限制
     * @return 排序后的推荐项
     */
    List<RecommendationItem> executeRanking(List<RecallCandidate> candidates, String userId, 
                                             String scene, Integer limit);
    
    /**
     * 构建排序特征
     * 
     * @param candidate 候选
     * @param userId 用户ID
     * @param scene 场景
     * @return 排序特征
     */
    RankingFeature buildRankingFeatures(RecallCandidate candidate, String userId, String scene);
    
    /**
     * 上报曝光
     * 
     * @param userId 用户ID
     * @param itemId 物品ID
     * @param scene 场景
     * @param position 位置
     * @param extraParams 额外参数
     */
    void reportImpression(String userId, String itemId, String scene, Integer position, 
                          Map<String, Object> extraParams);
    
    /**
     * 上报点击
     * 
     * @param userId 用户ID
     * @param itemId 物品ID
     * @param scene 场景
     * @param position 位置
     * @param extraParams 额外参数
     */
    void reportClick(String userId, String itemId, String scene, Integer position,
                     Map<String, Object> extraParams);
    
    /**
     * 上报转化
     * 
     * @param userId 用户ID
     * @param itemId 物品ID
     * @param scene 场景
     * @param conversionType 转化类型
     * @param extraParams 额外参数
     */
    void reportConversion(String userId, String itemId, String scene, String conversionType,
                          Map<String, Object> extraParams);
    
    /**
     * 刷新推荐缓存
     * 
     * @param userId 用户ID
     * @param scene 场景
     */
    void refreshRecommendationCache(String userId, String scene);
    
    /**
     * 获取用户推荐历史
     * 
     * @param userId 用户ID
     * @param limit 数量限制
     * @return 历史推荐列表
     */
    List<UserRecommendationFeed> getUserRecommendationHistory(String userId, Integer limit);
    
    /**
     * 批量获取推荐
     * 
     * @param requests 批量请求
     * @return 批量响应
     */
    List<RecommendationFeedResponseDTO> batchGetRecommendations(List<RecommendationFeedRequestDTO> requests);
}
