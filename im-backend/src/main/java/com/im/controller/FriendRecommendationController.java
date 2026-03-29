package com.im.controller;

import com.im.dto.FriendRecommendationRequest;
import com.im.dto.FriendRecommendationResponse;
import com.im.service.FriendRecommendationService;
import com.im.common.Result;
import com.im.common.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

/**
 * 好友推荐控制器
 * 提供好友推荐相关的REST API接口
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/friends/recommendations")
@RequiredArgsConstructor
@Validated
@Tag(name = "好友推荐", description = "好友推荐相关接口")
public class FriendRecommendationController {

    private final FriendRecommendationService recommendationService;

    /**
     * 获取好友推荐列表
     */
    @GetMapping
    @Operation(summary = "获取好友推荐列表", description = "基于多种算法获取可能认识的人")
    public Result<PageResult<FriendRecommendationResponse>> getRecommendations(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") @Min(1) Integer pageNum,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer pageSize,
            @RequestParam(required = false) String algorithmType) {
        
        log.info("获取好友推荐列表, userId={}, pageNum={}, pageSize={}, algorithm={}", 
                userId, pageNum, pageSize, algorithmType);
        
        PageResult<FriendRecommendationResponse> result = 
                recommendationService.getRecommendations(userId, pageNum, pageSize, algorithmType);
        
        return Result.success(result);
    }

    /**
     * 基于共同好友获取推荐
     */
    @GetMapping("/mutual-friends")
    @Operation(summary = "基于共同好友推荐", description = "根据共同好友数量推荐可能认识的人")
    public Result<List<FriendRecommendationResponse>> getMutualFriendRecommendations(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer limit) {
        
        log.info("基于共同好友获取推荐, userId={}, limit={}", userId, limit);
        
        List<FriendRecommendationResponse> recommendations = 
                recommendationService.getRecommendationsByMutualFriends(userId, limit);
        
        return Result.success(recommendations);
    }

    /**
     * 基于兴趣标签获取推荐
     */
    @GetMapping("/interest-tags")
    @Operation(summary = "基于兴趣标签推荐", description = "根据兴趣标签匹配推荐可能认识的人")
    public Result<List<FriendRecommendationResponse>> getInterestTagRecommendations(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer limit) {
        
        log.info("基于兴趣标签获取推荐, userId={}, limit={}", userId, limit);
        
        List<FriendRecommendationResponse> recommendations = 
                recommendationService.getRecommendationsByInterestTags(userId, limit);
        
        return Result.success(recommendations);
    }

    /**
     * 基于群组关系获取推荐
     */
    @GetMapping("/group-relations")
    @Operation(summary = "基于群组关系推荐", description = "根据共同群组推荐可能认识的人")
    public Result<List<FriendRecommendationResponse>> getGroupRelationRecommendations(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "10") @Min(1) @Max(50) Integer limit) {
        
        log.info("基于群组关系获取推荐, userId={}, limit={}", userId, limit);
        
        List<FriendRecommendationResponse> recommendations = 
                recommendationService.getRecommendationsByGroupRelations(userId, limit);
        
        return Result.success(recommendations);
    }

    /**
     * 获取混合推荐结果
     */
    @GetMapping("/mixed")
    @Operation(summary = "获取混合推荐", description = "综合多种算法获取推荐结果")
    public Result<List<FriendRecommendationResponse>> getMixedRecommendations(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) Integer limit) {
        
        log.info("获取混合推荐, userId={}, limit={}", userId, limit);
        
        List<FriendRecommendationResponse> recommendations = 
                recommendationService.getMixedRecommendations(userId, limit);
        
        return Result.success(recommendations);
    }

    /**
     * 刷新推荐列表
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新推荐列表", description = "重新计算并刷新好友推荐")
    public Result<Void> refreshRecommendations(@RequestAttribute("userId") Long userId) {
        
        log.info("刷新推荐列表, userId={}", userId);
        
        recommendationService.refreshRecommendations(userId);
        
        return Result.success();
    }

    /**
     * 忽略推荐用户
     */
    @PostMapping("/{targetUserId}/ignore")
    @Operation(summary = "忽略推荐用户", description = "将指定用户从推荐列表中移除")
    public Result<Void> ignoreRecommendation(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long targetUserId) {
        
        log.info("忽略推荐用户, userId={}, targetUserId={}", userId, targetUserId);
        
        recommendationService.ignoreRecommendation(userId, targetUserId);
        
        return Result.success();
    }

    /**
     * 批量忽略推荐用户
     */
    @PostMapping("/ignore-batch")
    @Operation(summary = "批量忽略推荐", description = "批量忽略多个推荐用户")
    public Result<Void> ignoreRecommendationsBatch(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Valid FriendRecommendationRequest request) {
        
        log.info("批量忽略推荐, userId={}, count={}", userId, request.getTargetUserIds().size());
        
        recommendationService.ignoreRecommendationsBatch(userId, request.getTargetUserIds());
        
        return Result.success();
    }

    /**
     * 获取推荐原因
     */
    @GetMapping("/{targetUserId}/reason")
    @Operation(summary = "获取推荐原因", description = "获取为什么会推荐这个用户的详细原因")
    public Result<String> getRecommendationReason(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long targetUserId) {
        
        log.info("获取推荐原因, userId={}, targetUserId={}", userId, targetUserId);
        
        String reason = recommendationService.getRecommendationReason(userId, targetUserId);
        
        return Result.success(reason);
    }

    /**
     * 获取推荐统计
     */
    @GetMapping("/stats")
    @Operation(summary = "获取推荐统计", description = "获取推荐系统的统计数据")
    public Result<RecommendationStats> getRecommendationStats(
            @RequestAttribute("userId") Long userId) {
        
        log.info("获取推荐统计, userId={}", userId);
        
        RecommendationStats stats = recommendationService.getRecommendationStats(userId);
        
        return Result.success(stats);
    }

    /**
     * 反馈推荐结果
     */
    @PostMapping("/{targetUserId}/feedback")
    @Operation(summary = "反馈推荐结果", description = "对推荐结果进行反馈，用于优化算法")
    public Result<Void> feedbackRecommendation(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long targetUserId,
            @RequestParam Boolean isHelpful) {
        
        log.info("反馈推荐结果, userId={}, targetUserId={}, isHelpful={}", 
                userId, targetUserId, isHelpful);
        
        recommendationService.feedbackRecommendation(userId, targetUserId, isHelpful);
        
        return Result.success();
    }
}
