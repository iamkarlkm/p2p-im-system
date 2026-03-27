package com.im.service.impl;

import com.im.dto.FriendRecommendationRequest;
import com.im.dto.FriendRecommendationResponse;
import com.im.service.FriendRecommendationService;
import com.im.algorithm.RecommendationEngine;
import com.im.algorithm.MutualFriendScorer;
import com.im.algorithm.InterestTagScorer;
import com.im.algorithm.GroupRelationScorer;
import com.im.model.RecommendationScore;
import com.im.model.User;
import com.im.model.IgnoredRecommendation;
import com.im.repository.RecommendationRepository;
import com.im.repository.UserRepository;
import com.im.repository.IgnoredRecommendationRepository;
import com.im.repository.FriendRelationRepository;
import com.im.repository.FriendRequestRepository;
import com.im.controller.RecommendationStats;
import com.im.common.PageResult;
import com.im.common.BusinessException;
import com.im.common.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 好友推荐服务实现
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FriendRecommendationServiceImpl implements FriendRecommendationService {

    private final RecommendationEngine recommendationEngine;
    private final MutualFriendScorer mutualFriendScorer;
    private final InterestTagScorer interestTagScorer;
    private final GroupRelationScorer groupRelationScorer;
    
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final IgnoredRecommendationRepository ignoredRecommendationRepository;
    private final FriendRelationRepository friendRelationRepository;
    private final FriendRequestRepository friendRequestRepository;

    private static final double MIN_RECOMMENDATION_SCORE = 0.3;
    private static final int MAX_RECOMMENDATIONS = 100;
    private static final int CACHE_DURATION_HOURS = 24;

    @Override
    public PageResult<FriendRecommendationResponse> getRecommendations(
            Long userId, Integer pageNum, Integer pageSize, String algorithmType) {
        
        log.debug("获取好友推荐列表, userId={}, algorithm={}", userId, algorithmType);
        
        // 获取已忽略的用户ID列表
        Set<Long> ignoredUserIds = getIgnoredUserIds(userId);
        
        // 获取已是好友的用户ID列表
        Set<Long> existingFriendIds = getExistingFriendIds(userId);
        
        // 获取已发送好友请求的用户ID列表
        Set<Long> pendingRequestIds = getPendingRequestIds(userId);
        
        // 使用推荐引擎计算推荐结果
        List<RecommendationScore> scores = recommendationEngine.calculateScores(
                userId, algorithmType, MAX_RECOMMENDATIONS);
        
        // 过滤已忽略、已是好友、已发送请求的用户
        List<RecommendationScore> filteredScores = scores.stream()
                .filter(score -> !ignoredUserIds.contains(score.getTargetUserId()))
                .filter(score -> !existingFriendIds.contains(score.getTargetUserId()))
                .filter(score -> !pendingRequestIds.contains(score.getTargetUserId()))
                .filter(score -> score.getScore() >= MIN_RECOMMENDATION_SCORE)
                .collect(Collectors.toList());
        
        // 转换为响应DTO
        List<FriendRecommendationResponse> responses = filteredScores.stream()
                .map(score -> convertToResponse(userId, score))
                .collect(Collectors.toList());
        
        // 分页处理
        int total = responses.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);
        
        if (start > total) {
            return PageResult.empty(pageNum, pageSize);
        }
        
        List<FriendRecommendationResponse> pageData = responses.subList(start, end);
        
        return PageResult.of(pageData, total, pageNum, pageSize);
    }

    @Override
    public List<FriendRecommendationResponse> getRecommendationsByMutualFriends(Long userId, Integer limit) {
        log.debug("基于共同好友获取推荐, userId={}", userId);
        
        Set<Long> ignoredUserIds = getIgnoredUserIds(userId);
        Set<Long> existingFriendIds = getExistingFriendIds(userId);
        
        List<RecommendationScore> scores = mutualFriendScorer.calculateScores(userId, limit * 2);
        
        return scores.stream()
                .filter(score -> !ignoredUserIds.contains(score.getTargetUserId()))
                .filter(score -> !existingFriendIds.contains(score.getTargetUserId()))
                .limit(limit)
                .map(score -> convertToResponse(userId, score))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRecommendationResponse> getRecommendationsByInterestTags(Long userId, Integer limit) {
        log.debug("基于兴趣标签获取推荐, userId={}", userId);
        
        Set<Long> ignoredUserIds = getIgnoredUserIds(userId);
        Set<Long> existingFriendIds = getExistingFriendIds(userId);
        
        List<RecommendationScore> scores = interestTagScorer.calculateScores(userId, limit * 2);
        
        return scores.stream()
                .filter(score -> !ignoredUserIds.contains(score.getTargetUserId()))
                .filter(score -> !existingFriendIds.contains(score.getTargetUserId()))
                .limit(limit)
                .map(score -> convertToResponse(userId, score))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRecommendationResponse> getRecommendationsByGroupRelations(Long userId, Integer limit) {
        log.debug("基于群组关系获取推荐, userId={}", userId);
        
        Set<Long> ignoredUserIds = getIgnoredUserIds(userId);
        Set<Long> existingFriendIds = getExistingFriendIds(userId);
        
        List<RecommendationScore> scores = groupRelationScorer.calculateScores(userId, limit * 2);
        
        return scores.stream()
                .filter(score -> !ignoredUserIds.contains(score.getTargetUserId()))
                .filter(score -> !existingFriendIds.contains(score.getTargetUserId()))
                .limit(limit)
                .map(score -> convertToResponse(userId, score))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRecommendationResponse> getMixedRecommendations(Long userId, Integer limit) {
        log.debug("获取混合推荐, userId={}", userId);
        
        // 从多个算法获取推荐
        List<FriendRecommendationResponse> mutualFriendRecs = 
                getRecommendationsByMutualFriends(userId, limit / 3);
        List<FriendRecommendationResponse> interestTagRecs = 
                getRecommendationsByInterestTags(userId, limit / 3);
        List<FriendRecommendationResponse> groupRelationRecs = 
                getRecommendationsByGroupRelations(userId, limit / 3);
        
        // 合并并去重
        Set<Long> seenUserIds = new HashSet<>();
        List<FriendRecommendationResponse> mixed = new ArrayList<>();
        
        // 轮流从各个算法取推荐，保证多样性
        int maxSize = Math.max(mutualFriendRecs.size(), 
                Math.max(interestTagRecs.size(), groupRelationRecs.size()));
        
        for (int i = 0; i < maxSize && mixed.size() < limit; i++) {
            if (i < mutualFriendRecs.size()) {
                FriendRecommendationResponse rec = mutualFriendRecs.get(i);
                if (seenUserIds.add(rec.getUserId())) {
                    mixed.add(rec);
                }
            }
            if (i < interestTagRecs.size() && mixed.size() < limit) {
                FriendRecommendationResponse rec = interestTagRecs.get(i);
                if (seenUserIds.add(rec.getUserId())) {
                    mixed.add(rec);
                }
            }
            if (i < groupRelationRecs.size() && mixed.size() < limit) {
                FriendRecommendationResponse rec = groupRelationRecs.get(i);
                if (seenUserIds.add(rec.getUserId())) {
                    mixed.add(rec);
                }
            }
        }
        
        // 按推荐分数排序
        mixed.sort((a, b) -> Double.compare(b.getRecommendationScore(), a.getRecommendationScore()));
        
        return mixed;
    }

    @Override
    @Transactional
    public void refreshRecommendations(Long userId) {
        log.info("刷新推荐列表, userId={}", userId);
        
        // 清除旧的推荐缓存
        recommendationRepository.deleteByUserId(userId);
        
        // 重新计算推荐
        List<RecommendationScore> newScores = recommendationEngine.calculateScores(userId, null, MAX_RECOMMENDATIONS);
        
        // 保存新的推荐结果
        newScores.forEach(score -> {
            score.setCreatedAt(LocalDateTime.now());
            score.setExpiresAt(LocalDateTime.now().plusHours(CACHE_DURATION_HOURS));
            recommendationRepository.save(score);
        });
        
        log.info("推荐列表刷新完成, userId={}, count={}", userId, newScores.size());
    }

    @Override
    @Transactional
    public void ignoreRecommendation(Long userId, Long targetUserId) {
        log.info("忽略推荐用户, userId={}, targetUserId={}", userId, targetUserId);
        
        if (userId.equals(targetUserId)) {
            throw new BusinessException(ErrorCode.INVALID_PARAM, "不能忽略自己");
        }
        
        IgnoredRecommendation ignored = IgnoredRecommendation.builder()
                .userId(userId)
                .targetUserId(targetUserId)
                .ignoredAt(LocalDateTime.now())
                .build();
        
        ignoredRecommendationRepository.save(ignored);
    }

    @Override
    @Transactional
    public void ignoreRecommendationsBatch(Long userId, List<Long> targetUserIds) {
        log.info("批量忽略推荐, userId={}, count={}", userId, targetUserIds.size());
        
        List<IgnoredRecommendation> ignoredList = targetUserIds.stream()
                .filter(targetId -> !userId.equals(targetId))
                .map(targetId -> IgnoredRecommendation.builder()
                        .userId(userId)
                        .targetUserId(targetId)
                        .ignoredAt(LocalDateTime.now())
                        .build())
                .collect(Collectors.toList());
        
        ignoredRecommendationRepository.saveAll(ignoredList);
    }

    @Override
    public String getRecommendationReason(Long userId, Long targetUserId) {
        log.debug("获取推荐原因, userId={}, targetUserId={}", userId, targetUserId);
        
        // 获取共同好友数量
        long mutualFriendCount = mutualFriendScorer.getMutualFriendCount(userId, targetUserId);
        
        // 获取共同群组数量
        int commonGroupCount = groupRelationScorer.getCommonGroupCount(userId, targetUserId);
        
        // 获取匹配的兴趣标签
        List<String> commonTags = interestTagScorer.getCommonInterestTags(userId, targetUserId);
        
        // 构建推荐原因
        StringBuilder reason = new StringBuilder();
        
        if (mutualFriendCount > 0) {
            reason.append(String.format("你们有%d个共同好友", mutualFriendCount));
        }
        
        if (commonGroupCount > 0) {
            if (reason.length() > 0) {
                reason.append("，");
            }
            reason.append(String.format("同在一个%d人群组中", commonGroupCount));
        }
        
        if (!commonTags.isEmpty()) {
            if (reason.length() > 0) {
                reason.append("，");
            }
            reason.append(String.format("共同兴趣：%s", String.join("、", commonTags.subList(0, Math.min(3, commonTags.size())))));
        }
        
        if (reason.length() == 0) {
            reason.append("系统根据你的社交行为推荐");
        }
        
        return reason.toString();
    }

    @Override
    public RecommendationStats getRecommendationStats(Long userId) {
        log.debug("获取推荐统计, userId={}", userId);
        
        // 获取各类推荐数量
        long mutualFriendCount = recommendationRepository.countByUserIdAndAlgorithmType(userId, "MUTUAL_FRIEND");
        long interestTagCount = recommendationRepository.countByUserIdAndAlgorithmType(userId, "INTEREST_TAG");
        long groupRelationCount = recommendationRepository.countByUserIdAndAlgorithmType(userId, "GROUP_RELATION");
        long totalCount = recommendationRepository.countByUserId(userId);
        
        // 获取忽略数量
        long ignoredCount = ignoredRecommendationRepository.countByUserId(userId);
        
        return RecommendationStats.builder()
                .totalRecommendations(totalCount)
                .mutualFriendRecommendations(mutualFriendCount)
                .interestTagRecommendations(interestTagCount)
                .groupRelationRecommendations(groupRelationCount)
                .ignoredRecommendations(ignoredCount)
                .build();
    }

    @Override
    @Transactional
    public void feedbackRecommendation(Long userId, Long targetUserId, Boolean isHelpful) {
        log.info("反馈推荐结果, userId={}, targetUserId={}, isHelpful={}", userId, targetUserId, isHelpful);
        
        // 记录反馈用于算法优化
        recommendationRepository.updateFeedback(userId, targetUserId, isHelpful);
    }

    // ==================== 私有方法 ====================

    private Set<Long> getIgnoredUserIds(Long userId) {
        return ignoredRecommendationRepository.findByUserId(userId).stream()
                .map(IgnoredRecommendation::getTargetUserId)
                .collect(Collectors.toSet());
    }

    private Set<Long> getExistingFriendIds(Long userId) {
        return friendRelationRepository.findFriendIdsByUserId(userId);
    }

    private Set<Long> getPendingRequestIds(Long userId) {
        return friendRequestRepository.findPendingRequestTargetIdsByUserId(userId);
    }

    private FriendRecommendationResponse convertToResponse(Long userId, RecommendationScore score) {
        Long targetUserId = score.getTargetUserId();
        
        // 获取用户信息
        User user = userRepository.findById(targetUserId).orElse(null);
        if (user == null) {
            return null;
        }
        
        // 获取共同好友信息
        List<FriendRecommendationResponse.MutualFriendInfo> mutualFriends = 
                mutualFriendScorer.getMutualFriends(userId, targetUserId, 3);
        
        // 获取共同兴趣标签
        List<String> commonTags = interestTagScorer.getCommonInterestTags(userId, targetUserId);
        
        // 构建响应
        return FriendRecommendationResponse.builder()
                .userId(targetUserId)
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .signature(user.getSignature())
                .recommendationScore(score.getScore())
                .recommendationReason(getRecommendationReason(userId, targetUserId))
                .algorithmType(score.getAlgorithmType())
                .mutualFriendCount(mutualFriends.size())
                .mutualFriends(mutualFriends)
                .commonGroupCount(groupRelationScorer.getCommonGroupCount(userId, targetUserId))
                .commonInterestTags(commonTags)
                .matchedTagCount(commonTags.size())
                .isOnline(user.getIsOnline())
                .userLevel(user.getLevel())
                .registerTime(user.getCreatedAt())
                .hasPendingRequest(false)
                .recommendedAt(LocalDateTime.now())
                .build();
    }
}
