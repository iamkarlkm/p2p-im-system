package com.im.service;

import com.im.dto.FriendRecommendationRequest;
import com.im.dto.FriendRecommendationResponse;
import com.im.controller.RecommendationStats;
import com.im.common.PageResult;

import java.util.List;

/**
 * 好友推荐服务接口
 * 
 * @author IM Team
 * @since 2026-03-27
 */
public interface FriendRecommendationService {

    /**
     * 获取好友推荐列表
     * 
     * @param userId 当前用户ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param algorithmType 算法类型
     * @return 分页推荐结果
     */
    PageResult<FriendRecommendationResponse> getRecommendations(
            Long userId, Integer pageNum, Integer pageSize, String algorithmType);

    /**
     * 基于共同好友获取推荐
     * 
     * @param userId 当前用户ID
     * @param limit 限制数量
     * @return 推荐列表
     */
    List<FriendRecommendationResponse> getRecommendationsByMutualFriends(Long userId, Integer limit);

    /**
     * 基于兴趣标签获取推荐
     * 
     * @param userId 当前用户ID
     * @param limit 限制数量
     * @return 推荐列表
     */
    List<FriendRecommendationResponse> getRecommendationsByInterestTags(Long userId, Integer limit);

    /**
     * 基于群组关系获取推荐
     * 
     * @param userId 当前用户ID
     * @param limit 限制数量
     * @return 推荐列表
     */
    List<FriendRecommendationResponse> getRecommendationsByGroupRelations(Long userId, Integer limit);

    /**
     * 获取混合推荐结果
     * 
     * @param userId 当前用户ID
     * @param limit 限制数量
     * @return 推荐列表
     */
    List<FriendRecommendationResponse> getMixedRecommendations(Long userId, Integer limit);

    /**
     * 刷新推荐列表
     * 
     * @param userId 当前用户ID
     */
    void refreshRecommendations(Long userId);

    /**
     * 忽略推荐用户
     * 
     * @param userId 当前用户ID
     * @param targetUserId 目标用户ID
     */
    void ignoreRecommendation(Long userId, Long targetUserId);

    /**
     * 批量忽略推荐用户
     * 
     * @param userId 当前用户ID
     * @param targetUserIds 目标用户ID列表
     */
    void ignoreRecommendationsBatch(Long userId, List<Long> targetUserIds);

    /**
     * 获取推荐原因
     * 
     * @param userId 当前用户ID
     * @param targetUserId 目标用户ID
     * @return 推荐原因描述
     */
    String getRecommendationReason(Long userId, Long targetUserId);

    /**
     * 获取推荐统计
     * 
     * @param userId 当前用户ID
     * @return 统计数据
     */
    RecommendationStats getRecommendationStats(Long userId);

    /**
     * 反馈推荐结果
     * 
     * @param userId 当前用户ID
     * @param targetUserId 目标用户ID
     * @param isHelpful 是否有帮助
     */
    void feedbackRecommendation(Long userId, Long targetUserId, Boolean isHelpful);
}
