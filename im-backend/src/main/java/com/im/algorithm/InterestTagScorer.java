package com.im.algorithm;

import com.im.model.RecommendationScore;
import com.im.model.UserTag;
import com.im.repository.UserTagRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 兴趣标签评分器
 * 基于用户兴趣标签匹配度计算推荐分数
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InterestTagScorer {

    private final UserTagRepository userTagRepository;

    // 标签匹配权重
    private static final double BASE_SCORE = 0.25;
    private static final double TAG_MATCH_WEIGHT = 0.12;
    private static final double MAX_SCORE = 0.9;

    /**
     * 计算基于兴趣标签的推荐分数
     * 
     * @param userId 当前用户ID
     * @param limit 限制数量
     * @return 推荐分数列表
     */
    public List<RecommendationScore> calculateScores(Long userId, int limit) {
        log.debug("计算兴趣标签推荐分数, userId={}, limit={}", userId, limit);
        
        // 获取当前用户的兴趣标签
        List<UserTag> userTags = userTagRepository.findByUserId(userId);
        
        if (userTags.isEmpty()) {
            log.debug("用户没有设置兴趣标签，无法基于标签推荐");
            return Collections.emptyList();
        }
        
        Set<String> userTagNames = userTags.stream()
                .map(UserTag::getTagName)
                .collect(Collectors.toSet());
        
        // 获取有相同标签的其他用户
        Map<Long, Set<String>> candidateTags = new HashMap<>();
        
        for (String tagName : userTagNames) {
            List<UserTag> usersWithTag = userTagRepository.findByTagName(tagName);
            
            for (UserTag tag : usersWithTag) {
                Long candidateId = tag.getUserId();
                if (candidateId.equals(userId)) {
                    continue;
                }
                
                candidateTags.computeIfAbsent(candidateId, k -> new HashSet<>()).add(tagName);
            }
        }
        
        // 计算匹配分数
        List<RecommendationScore> scores = candidateTags.entrySet().stream()
                .map(entry -> {
                    Long targetUserId = entry.getKey();
                    Set<String> matchedTags = entry.getValue();
                    int matchCount = matchedTags.size();
                    
                    // 计算Jaccard相似度
                    List<UserTag> candidateAllTags = userTagRepository.findByUserId(targetUserId);
                    Set<String> candidateTagNames = candidateAllTags.stream()
                            .map(UserTag::getTagName)
                            .collect(Collectors.toSet());
                    
                    Set<String> union = new HashSet<>(userTagNames);
                    union.addAll(candidateTagNames);
                    
                    double jaccardSimilarity = union.isEmpty() ? 0 : (double) matchCount / union.size();
                    
                    // 综合分数：基础分 + 匹配数量 * 权重 + Jaccard相似度 * 0.3
                    double score = Math.min(
                            BASE_SCORE + matchCount * TAG_MATCH_WEIGHT + jaccardSimilarity * 0.3,
                            MAX_SCORE
                    );
                    
                    return RecommendationScore.builder()
                            .userId(userId)
                            .targetUserId(targetUserId)
                            .score(score)
                            .algorithmType("INTEREST_TAG")
                            .matchedTagCount(matchCount)
                            .build();
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
        
        log.debug("兴趣标签推荐计算完成, userId={}, 找到{}个推荐", userId, scores.size());
        return scores;
    }

    /**
     * 获取两个用户的共同兴趣标签
     */
    public List<String> getCommonInterestTags(Long userId1, Long userId2) {
        List<UserTag> tags1 = userTagRepository.findByUserId(userId1);
        List<UserTag> tags2 = userTagRepository.findByUserId(userId2);
        
        Set<String> tagNames1 = tags1.stream()
                .map(UserTag::getTagName)
                .collect(Collectors.toSet());
        
        return tags2.stream()
                .map(UserTag::getTagName)
                .filter(tagNames1::contains)
                .collect(Collectors.toList());
    }

    /**
     * 计算标签相似度
     */
    public double calculateTagSimilarity(Long userId1, Long userId2) {
        List<String> commonTags = getCommonInterestTags(userId1, userId2);
        
        List<UserTag> tags1 = userTagRepository.findByUserId(userId1);
        List<UserTag> tags2 = userTagRepository.findByUserId(userId2);
        
        Set<String> allTags = new HashSet<>();
        allTags.addAll(tags1.stream().map(UserTag::getTagName).collect(Collectors.toSet()));
        allTags.addAll(tags2.stream().map(UserTag::getTagName).collect(Collectors.toSet()));
        
        return allTags.isEmpty() ? 0 : (double) commonTags.size() / allTags.size();
    }

    /**
     * 根据特定标签推荐用户
     */
    public List<RecommendationScore> recommendBySpecificTag(Long userId, String tagName, int limit) {
        log.debug("根据特定标签推荐, userId={}, tag={}", userId, tagName);
        
        List<UserTag> usersWithTag = userTagRepository.findByTagName(tagName);
        
        return usersWithTag.stream()
                .filter(tag -> !tag.getUserId().equals(userId))
                .map(tag -> RecommendationScore.builder()
                        .userId(userId)
                        .targetUserId(tag.getUserId())
                        .score(0.7) // 特定标签匹配给予较高基础分
                        .algorithmType("SPECIFIC_TAG")
                        .build())
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * 获取热门标签
     */
    public List<String> getPopularTags(int limit) {
        return userTagRepository.findPopularTags(limit);
    }
}
