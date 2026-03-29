package com.im.algorithm;

import com.im.model.RecommendationScore;
import com.im.dto.FriendRecommendationResponse;
import com.im.repository.FriendRelationRepository;
import com.im.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 共同好友评分器
 * 基于共同好友数量计算推荐分数
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MutualFriendScorer {

    private final FriendRelationRepository friendRelationRepository;
    private final UserRepository userRepository;

    // 共同好友数量权重系数
    private static final double BASE_SCORE = 0.3;
    private static final double MUTUAL_FRIEND_WEIGHT = 0.15;
    private static final double MAX_SCORE = 0.95;

    /**
     * 计算基于共同好友的推荐分数
     * 
     * @param userId 当前用户ID
     * @param limit 限制数量
     * @return 推荐分数列表
     */
    public List<RecommendationScore> calculateScores(Long userId, int limit) {
        log.debug("计算共同好友推荐分数, userId={}, limit={}", userId, limit);
        
        // 获取用户的所有好友ID
        Set<Long> userFriends = friendRelationRepository.findFriendIdsByUserId(userId);
        
        if (userFriends.isEmpty()) {
            log.debug("用户没有好友，无法基于共同好友推荐");
            return Collections.emptyList();
        }
        
        // 计算候选用户的共同好友数量
        Map<Long, Integer> mutualFriendCounts = new HashMap<>();
        
        for (Long friendId : userFriends) {
            // 获取该好友的所有好友
            Set<Long> friendsOfFriend = friendRelationRepository.findFriendIdsByUserId(friendId);
            
            for (Long candidateId : friendsOfFriend) {
                // 跳过用户自己和已经是好友的用户
                if (candidateId.equals(userId) || userFriends.contains(candidateId)) {
                    continue;
                }
                
                mutualFriendCounts.merge(candidateId, 1, Integer::sum);
            }
        }
        
        // 转换为推荐分数
        List<RecommendationScore> scores = mutualFriendCounts.entrySet().stream()
                .map(entry -> {
                    Long targetUserId = entry.getKey();
                    Integer mutualCount = entry.getValue();
                    
                    // 计算分数：基础分 + 共同好友数量 * 权重
                    double score = Math.min(BASE_SCORE + mutualCount * MUTUAL_FRIEND_WEIGHT, MAX_SCORE);
                    
                    return RecommendationScore.builder()
                            .userId(userId)
                            .targetUserId(targetUserId)
                            .score(score)
                            .algorithmType("MUTUAL_FRIEND")
                            .mutualFriendCount(mutualCount)
                            .build();
                })
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
        
        log.debug("共同好友推荐计算完成, userId={}, 找到{}个推荐", userId, scores.size());
        return scores;
    }

    /**
     * 获取两个用户的共同好友数量
     */
    public long getMutualFriendCount(Long userId1, Long userId2) {
        Set<Long> friends1 = friendRelationRepository.findFriendIdsByUserId(userId1);
        Set<Long> friends2 = friendRelationRepository.findFriendIdsByUserId(userId2);
        
        friends1.retainAll(friends2);
        return friends1.size();
    }

    /**
     * 获取共同好友列表
     */
    public List<FriendRecommendationResponse.MutualFriendInfo> getMutualFriends(
            Long userId1, Long userId2, int limit) {
        
        Set<Long> friends1 = friendRelationRepository.findFriendIdsByUserId(userId1);
        Set<Long> friends2 = friendRelationRepository.findFriendIdsByUserId(userId2);
        
        friends1.retainAll(friends2);
        
        return friends1.stream()
                .limit(limit)
                .map(friendId -> {
                    return userRepository.findById(friendId)
                            .map(user -> FriendRecommendationResponse.MutualFriendInfo.builder()
                                    .friendId(user.getId())
                                    .friendNickname(user.getNickname())
                                    .friendAvatar(user.getAvatar())
                                    .build())
                            .orElse(null);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * 获取二度好友（好友的好友）
     */
    public List<Long> getSecondDegreeConnections(Long userId, int limit) {
        Set<Long> directFriends = friendRelationRepository.findFriendIdsByUserId(userId);
        Set<Long> secondDegree = new HashSet<>();
        
        for (Long friendId : directFriends) {
            Set<Long> friendsOfFriend = friendRelationRepository.findFriendIdsByUserId(friendId);
            for (Long candidate : friendsOfFriend) {
                if (!candidate.equals(userId) && !directFriends.contains(candidate)) {
                    secondDegree.add(candidate);
                    if (secondDegree.size() >= limit) {
                        break;
                    }
                }
            }
            if (secondDegree.size() >= limit) {
                break;
            }
        }
        
        return new ArrayList<>(secondDegree);
    }
}
