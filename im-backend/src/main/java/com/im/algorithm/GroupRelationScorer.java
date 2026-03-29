package com.im.algorithm;

import com.im.model.RecommendationScore;
import com.im.repository.GroupMemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 群组关系评分器
 * 基于共同群组计算推荐分数
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GroupRelationScorer {

    private final GroupMemberRepository groupMemberRepository;

    // 群组关系权重
    private static final double BASE_SCORE = 0.2;
    private static final double COMMON_GROUP_WEIGHT = 0.18;
    private static final double MAX_SCORE = 0.85;

    /**
     * 计算基于群组关系的推荐分数
     * 
     * @param userId 当前用户ID
     * @param limit 限制数量
     * @return 推荐分数列表
     */
    public List<RecommendationScore> calculateScores(Long userId, int limit) {
        log.debug("计算群组关系推荐分数, userId={}, limit={}", userId, limit);
        
        // 获取用户加入的所有群组
        List<Long> userGroups = groupMemberRepository.findGroupIdsByUserId(userId);
        
        if (userGroups.isEmpty()) {
            log.debug("用户没有加入任何群组，无法基于群组推荐");
            return Collections.emptyList();
        }
        
        // 统计每个候选用户与当前用户的共同群组
        Map<Long, Integer> commonGroupCounts = new HashMap<>();
        Map<Long, Set<Long>> candidateGroups = new HashMap<>();
        
        for (Long groupId : userGroups) {
            List<Long> groupMembers = groupMemberRepository.findUserIdsByGroupId(groupId);
            
            for (Long memberId : groupMembers) {
                if (memberId.equals(userId)) {
                    continue;
                }
                
                commonGroupCounts.merge(memberId, 1, Integer::sum);
                candidateGroups.computeIfAbsent(memberId, k -> new HashSet<>()).add(groupId);
            }
        }
        
        // 转换为推荐分数
        List<RecommendationScore> scores = commonGroupCounts.entrySet().stream()
                .map(entry -> {
                    Long targetUserId = entry.getKey();
                    Integer commonCount = entry.getValue();
                    Set<Long> sharedGroups = candidateGroups.get(targetUserId);
                    
                    // 计算活跃度因子（基于共同群组数量）
                    double activityFactor = Math.min(commonCount * 0.1, 0.3);
                    
                    // 综合分数
                    double score = Math.min(
                            BASE_SCORE + commonCount * COMMON_GROUP_WEIGHT + activityFactor,
                            MAX_SCORE
                    );
                    
                    return RecommendationScore.builder()
                            .userId(userId)
                            .targetUserId(targetUserId)
                            .score(score)
                            .algorithmType("GROUP_RELATION")
                            .commonGroupCount(commonCount)
                            .build();
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
        
        log.debug("群组关系推荐计算完成, userId={}, 找到{}个推荐", userId, scores.size());
        return scores;
    }

    /**
     * 获取两个用户的共同群组数量
     */
    public int getCommonGroupCount(Long userId1, Long userId2) {
        List<Long> groups1 = groupMemberRepository.findGroupIdsByUserId(userId1);
        List<Long> groups2 = groupMemberRepository.findGroupIdsByUserId(userId2);
        
        Set<Long> common = new HashSet<>(groups1);
        common.retainAll(groups2);
        
        return common.size();
    }

    /**
     * 获取共同群组列表
     */
    public List<Long> getCommonGroups(Long userId1, Long userId2) {
        List<Long> groups1 = groupMemberRepository.findGroupIdsByUserId(userId1);
        List<Long> groups2 = groupMemberRepository.findGroupIdsByUserId(userId2);
        
        return groups1.stream()
                .filter(groups2::contains)
                .collect(Collectors.toList());
    }

    /**
     * 获取用户在群组中的活跃度分数
     */
    public double getGroupActivityScore(Long userId, Long groupId) {
        return groupMemberRepository.calculateActivityScore(userId, groupId);
    }

    /**
     * 获取活跃用户列表（基于群组发言）
     */
    public List<Long> getActiveUsersInGroups(List<Long> groupIds, int limit) {
        return groupMemberRepository.findActiveUsersInGroups(groupIds, limit);
    }

    /**
     * 根据群组推荐用户（针对特定群组的新成员推荐）
     */
    public List<RecommendationScore> recommendBySpecificGroup(Long userId, Long groupId, int limit) {
        log.debug("根据特定群组推荐, userId={}, groupId={}", userId, groupId);
        
        List<Long> groupMembers = groupMemberRepository.findUserIdsByGroupId(groupId);
        
        return groupMembers.stream()
                .filter(memberId -> !memberId.equals(userId))
                .map(memberId -> {
                    double activityScore = getGroupActivityScore(memberId, groupId);
                    
                    return RecommendationScore.builder()
                            .userId(userId)
                            .targetUserId(memberId)
                            .score(BASE_SCORE + activityScore * 0.5)
                            .algorithmType("SPECIFIC_GROUP")
                            .build();
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }
}
