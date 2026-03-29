package com.im.algorithm;

import com.im.model.RecommendationScore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐算法引擎
 * 综合多种推荐算法，提供统一的推荐计算入口
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RecommendationEngine {

    private final MutualFriendScorer mutualFriendScorer;
    private final InterestTagScorer interestTagScorer;
    private final GroupRelationScorer groupRelationScorer;

    // 算法权重配置
    private static final double MUTUAL_FRIEND_WEIGHT = 0.4;
    private static final double INTEREST_TAG_WEIGHT = 0.3;
    private static final double GROUP_RELATION_WEIGHT = 0.3;

    /**
     * 计算推荐分数
     * 
     * @param userId 当前用户ID
     * @param algorithmType 指定算法类型（可选）
     * @param limit 限制数量
     * @return 推荐分数列表
     */
    public List<RecommendationScore> calculateScores(Long userId, String algorithmType, int limit) {
        log.debug("计算推荐分数, userId={}, algorithm={}, limit={}", userId, algorithmType, limit);
        
        if (algorithmType != null) {
            // 使用指定算法
            return calculateBySpecificAlgorithm(userId, algorithmType, limit);
        } else {
            // 使用混合算法
            return calculateMixedScores(userId, limit);
        }
    }

    /**
     * 使用特定算法计算推荐分数
     */
    private List<RecommendationScore> calculateBySpecificAlgorithm(Long userId, String algorithmType, int limit) {
        switch (algorithmType.toUpperCase()) {
            case "MUTUAL_FRIEND":
                return mutualFriendScorer.calculateScores(userId, limit);
            case "INTEREST_TAG":
                return interestTagScorer.calculateScores(userId, limit);
            case "GROUP_RELATION":
                return groupRelationScorer.calculateScores(userId, limit);
            default:
                log.warn("未知的算法类型: {}, 使用混合算法", algorithmType);
                return calculateMixedScores(userId, limit);
        }
    }

    /**
     * 计算混合推荐分数
     * 综合多种算法的分数，加权平均
     */
    private List<RecommendationScore> calculateMixedScores(Long userId, int limit) {
        // 获取各算法的推荐结果
        List<RecommendationScore> mutualFriendScores = mutualFriendScorer.calculateScores(userId, limit * 2);
        List<RecommendationScore> interestTagScores = interestTagScorer.calculateScores(userId, limit * 2);
        List<RecommendationScore> groupRelationScores = groupRelationScorer.calculateScores(userId, limit * 2);
        
        // 合并所有候选用户
        Map<Long, RecommendationScore> candidateMap = new HashMap<>();
        
        // 归一化并加权各算法分数
        normalizeAndMerge(candidateMap, mutualFriendScores, "MUTUAL_FRIEND", MUTUAL_FRIEND_WEIGHT);
        normalizeAndMerge(candidateMap, interestTagScores, "INTEREST_TAG", INTEREST_TAG_WEIGHT);
        normalizeAndMerge(candidateMap, groupRelationScores, "GROUP_RELATION", GROUP_RELATION_WEIGHT);
        
        // 转换为列表并按分数排序
        List<RecommendationScore> result = new ArrayList<>(candidateMap.values());
        result.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
        
        // 限制数量
        return result.stream()
                .limit(limit)
                .peek(score -> score.setAlgorithmType("MIXED"))
                .collect(Collectors.toList());
    }

    /**
     * 归一化并合并分数
     */
    private void normalizeAndMerge(Map<Long, RecommendationScore> candidateMap, 
                                    List<RecommendationScore> scores, 
                                    String algorithmType, 
                                    double weight) {
        if (scores.isEmpty()) {
            return;
        }
        
        // 找出最大和最小分数用于归一化
        double maxScore = scores.stream().mapToDouble(RecommendationScore::getScore).max().orElse(1.0);
        double minScore = scores.stream().mapToDouble(RecommendationScore::getScore).min().orElse(0.0);
        double range = maxScore - minScore;
        
        if (range == 0) {
            range = 1.0;
        }
        
        for (RecommendationScore score : scores) {
            Long targetUserId = score.getTargetUserId();
            
            // 归一化分数并加权
            double normalizedScore = (score.getScore() - minScore) / range;
            double weightedScore = normalizedScore * weight;
            
            RecommendationScore existing = candidateMap.get(targetUserId);
            if (existing == null) {
                // 创建新的综合分数
                RecommendationScore merged = RecommendationScore.builder()
                        .userId(score.getUserId())
                        .targetUserId(targetUserId)
                        .score(weightedScore)
                        .algorithmType(algorithmType)
                        .build();
                candidateMap.put(targetUserId, merged);
            } else {
                // 累加分数
                existing.setScore(existing.getScore() + weightedScore);
            }
        }
    }

    /**
     * 冷启动推荐
     * 针对新用户，使用热门用户或相似特征用户推荐
     */
    public List<RecommendationScore> coldStartRecommendations(Long userId, int limit) {
        log.debug("冷启动推荐, userId={}, limit={}", userId, limit);
        
        // 优先使用群组关系（新用户可能已加入群组）
        List<RecommendationScore> groupScores = groupRelationScorer.calculateScores(userId, limit);
        
        if (groupScores.size() >= limit) {
            return groupScores;
        }
        
        // 补充兴趣标签推荐
        int remaining = limit - groupScores.size();
        List<RecommendationScore> tagScores = interestTagScorer.calculateScores(userId, remaining);
        
        // 合并结果
        Set<Long> seenUserIds = groupScores.stream()
                .map(RecommendationScore::getTargetUserId)
                .collect(Collectors.toSet());
        
        List<RecommendationScore> result = new ArrayList<>(groupScores);
        for (RecommendationScore score : tagScores) {
            if (!seenUserIds.contains(score.getTargetUserId())) {
                result.add(score);
                if (result.size() >= limit) {
                    break;
                }
            }
        }
        
        return result;
    }

    /**
     * 实时推荐
     * 基于用户实时行为进行推荐
     */
    public List<RecommendationScore> realTimeRecommendations(Long userId, int limit) {
        log.debug("实时推荐, userId={}, limit={}", userId, limit);
        
        // 实时推荐更侧重于共同好友和群组关系
        List<RecommendationScore> mutualScores = mutualFriendScorer.calculateScores(userId, limit);
        List<RecommendationScore> groupScores = groupRelationScorer.calculateScores(userId, limit / 2);
        
        // 合并并去重，优先共同好友
        Set<Long> seenUserIds = new HashSet<>();
        List<RecommendationScore> result = new ArrayList<>();
        
        for (RecommendationScore score : mutualScores) {
            if (seenUserIds.add(score.getTargetUserId())) {
                result.add(score);
                if (result.size() >= limit) {
                    break;
                }
            }
        }
        
        for (RecommendationScore score : groupScores) {
            if (seenUserIds.add(score.getTargetUserId())) {
                result.add(score);
                if (result.size() >= limit) {
                    break;
                }
            }
        }
        
        return result;
    }
}
