package com.im.backend.modules.merchant.review.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.im.backend.common.result.Result;
import com.im.backend.modules.merchant.review.dto.CreateReviewRequest;
import com.im.backend.modules.merchant.review.dto.ReviewResponse;
import com.im.backend.modules.merchant.review.service.MerchantReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商户评价控制器
 * @author IM Development Team
 * @version 1.0.0
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Validated
@Tag(name = "商户评价管理", description = "商户评价、点赞、回复、审核等接口")
public class MerchantReviewController {

    private final MerchantReviewService reviewService;

    // ============ 评价查询接口 ============

    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "获取商户评价列表", description = "分页查询指定商户的评价列表，支持评分筛选和排序")
    public Result<IPage<ReviewResponse>> getMerchantReviews(
            @Parameter(description = "商户ID") @PathVariable Long merchantId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "最低评分") @RequestParam(required = false) BigDecimal ratingMin,
            @Parameter(description = "最高评分") @RequestParam(required = false) BigDecimal ratingMax,
            @Parameter(description = "评价类型(1-文字 2-图文 3-视频)") @RequestParam(required = false) Integer reviewType,
            @Parameter(description = "排序类型(1-最新 2-最热 3-评分高)") @RequestParam(defaultValue = "1") Integer sortType) {
        return Result.success(reviewService.getMerchantReviews(merchantId, page, size, ratingMin, ratingMax, reviewType, sortType));
    }

    @GetMapping("/poi/{poiId}")
    @Operation(summary = "获取POI评价列表", description = "分页查询指定POI的评价列表")
    public Result<IPage<ReviewResponse>> getPoiReviews(
            @Parameter(description = "POI ID") @PathVariable Long poiId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "排序类型") @RequestParam(defaultValue = "1") Integer sortType) {
        return Result.success(reviewService.getPoiReviews(poiId, page, size, sortType));
    }

    @GetMapping("/{reviewId}")
    @Operation(summary = "获取评价详情", description = "获取指定评价的详细信息")
    public Result<ReviewResponse> getReviewDetail(
            @Parameter(description = "评价ID") @PathVariable Long reviewId,
            @Parameter(hidden = true) @RequestAttribute("userId") Long currentUserId) {
        return Result.success(reviewService.getReviewDetail(reviewId, currentUserId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "获取用户评价列表", description = "查询指定用户的所有评价")
    public Result<List<ReviewResponse>> getUserReviews(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(reviewService.getUserReviews(userId, limit));
    }

    // ============ 评价操作接口 ============

    @PostMapping
    @Operation(summary = "创建评价", description = "用户创建商户评价，支持多维度评分和多媒体上传")
    public Result<ReviewResponse> createReview(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Valid @RequestBody CreateReviewRequest request) {
        return reviewService.createReview(userId, request);
    }

    @DeleteMapping("/{reviewId}")
    @Operation(summary = "删除评价", description = "用户删除自己的评价")
    public Result<Void> deleteReview(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId) {
        return reviewService.deleteReview(userId, reviewId);
    }

    @PostMapping("/{reviewId}/like")
    @Operation(summary = "点赞评价", description = "对评价进行点赞")
    public Result<Void> likeReview(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId) {
        return reviewService.likeReview(userId, reviewId);
    }

    @DeleteMapping("/{reviewId}/like")
    @Operation(summary = "取消点赞", description = "取消对评价的点赞")
    public Result<Void> unlikeReview(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId) {
        return reviewService.unlikeReview(userId, reviewId);
    }

    @PostMapping("/{reviewId}/replies")
    @Operation(summary = "回复评价", description = "对评价进行回复或追评")
    public Result<ReviewResponse.ReplyResponse> replyReview(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId,
            @Parameter(description = "回复内容") @RequestParam String content,
            @Parameter(description = "父回复ID(回复的回复)") @RequestParam(required = false) Long parentId) {
        return reviewService.replyReview(userId, 1, reviewId, content, parentId);
    }

    @PostMapping("/{reviewId}/appeal")
    @Operation(summary = "申诉评价", description = "用户对被拒绝或标记为虚假的评价进行申诉")
    public Result<Void> appealReview(
            @Parameter(hidden = true) @RequestAttribute("userId") Long userId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId,
            @Parameter(description = "申诉理由") @RequestParam String reason) {
        return reviewService.appealReview(userId, reviewId, reason);
    }

    // ============ 商户/管理端接口 ============

    @PostMapping("/{reviewId}/merchant-reply")
    @Operation(summary = "商家回复评价", description = "商户官方回复用户评价")
    public Result<ReviewResponse.ReplyResponse> merchantReply(
            @Parameter(hidden = true) @RequestAttribute("merchantId") Long merchantId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId,
            @Parameter(description = "回复内容") @RequestParam String content) {
        return reviewService.replyReview(merchantId, 2, reviewId, content, null);
    }

    @PutMapping("/{reviewId}/top")
    @Operation(summary = "置顶评价", description = "置顶指定评价")
    public Result<Void> topReview(
            @Parameter(hidden = true) @RequestAttribute("operatorId") Long operatorId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId,
            @Parameter(description = "置顶权重") @RequestParam(defaultValue = "100") Integer weight) {
        return reviewService.topReview(reviewId, weight, operatorId);
    }

    @DeleteMapping("/{reviewId}/top")
    @Operation(summary = "取消置顶", description = "取消评价置顶")
    public Result<Void> cancelTopReview(
            @Parameter(hidden = true) @RequestAttribute("operatorId") Long operatorId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId) {
        return reviewService.cancelTopReview(reviewId, operatorId);
    }

    // ============ 审核管理接口 ============

    @PutMapping("/{reviewId}/audit")
    @Operation(summary = "审核评价", description = "审核指定评价（通过/拒绝）")
    public Result<Void> auditReview(
            @Parameter(hidden = true) @RequestAttribute("auditorId") Long auditorId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId,
            @Parameter(description = "审核状态(1-通过 2-拒绝)") @RequestParam Integer status,
            @Parameter(description = "审核备注") @RequestParam(required = false) String remark) {
        return reviewService.auditReview(reviewId, status, auditorId, remark);
    }

    @PutMapping("/batch-audit")
    @Operation(summary = "批量审核评价", description = "批量审核多个评价")
    public Result<Void> batchAuditReviews(
            @Parameter(hidden = true) @RequestAttribute("auditorId") Long auditorId,
            @Parameter(description = "评价ID列表") @RequestParam List<Long> reviewIds,
            @Parameter(description = "审核状态") @RequestParam Integer status) {
        return reviewService.batchAuditReviews(reviewIds, status, auditorId);
    }

    @PutMapping("/{reviewId}/appeal-process")
    @Operation(summary = "处理申诉", description = "处理用户对评价的申诉")
    public Result<Void> processAppeal(
            @Parameter(hidden = true) @RequestAttribute("operatorId") Long operatorId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId,
            @Parameter(description = "是否通过") @RequestParam Boolean approved,
            @Parameter(description = "处理备注") @RequestParam(required = false) String remark) {
        return reviewService.processAppeal(reviewId, approved, remark, operatorId);
    }

    @PutMapping("/{reviewId}/fake-flag")
    @Operation(summary = "标记虚假评价", description = "标记评价为虚假/正常")
    public Result<Void> markFakeReview(
            @Parameter(hidden = true) @RequestAttribute("operatorId") Long operatorId,
            @Parameter(description = "评价ID") @PathVariable Long reviewId,
            @Parameter(description = "虚假标记(0-正常 1-疑似 2-确认)") @RequestParam Integer fakeFlag) {
        return reviewService.markFakeReview(reviewId, fakeFlag, operatorId);
    }

    @GetMapping("/pending-audit")
    @Operation(summary = "获取待审核评价", description = "获取待审核的评价列表")
    public Result<List<ReviewResponse>> getPendingReviews(
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "50") Integer limit) {
        return Result.success(reviewService.getPendingReviews(limit));
    }

    @GetMapping("/suspected-fake")
    @Operation(summary = "获取疑似虚假评价", description = "获取疑似虚假的评价列表")
    public Result<List<ReviewResponse>> getSuspectedFakeReviews(
            @Parameter(description = "最小置信度") @RequestParam(defaultValue = "30") BigDecimal confidenceMin,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "50") Integer limit) {
        return Result.success(reviewService.getSuspectedFakeReviews(confidenceMin, limit));
    }

    // ============ 统计查询接口 ============

    @GetMapping("/merchant/{merchantId}/stats")
    @Operation(summary = "获取商户评价统计", description = "获取商户的评价统计数据")
    public Result<Map<String, Object>> getMerchantReviewStats(
            @Parameter(description = "商户ID") @PathVariable Long merchantId) {
        return Result.success(reviewService.getMerchantReviewStats(merchantId));
    }

    @GetMapping("/merchant/{merchantId}/rating-distribution")
    @Operation(summary = "获取商户评分分布", description = "获取商户各星级评分的分布情况")
    public Result<Map<String, Integer>> getMerchantRatingDistribution(
            @Parameter(description = "商户ID") @PathVariable Long merchantId) {
        return Result.success(reviewService.getMerchantRatingDistribution(merchantId));
    }

    @GetMapping("/merchant/{merchantId}/recommended")
    @Operation(summary = "获取推荐评价", description = "获取商户的优质推荐评价")
    public Result<List<ReviewResponse>> getRecommendedReviews(
            @Parameter(description = "商户ID") @PathVariable Long merchantId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(reviewService.getRecommendedReviews(merchantId, limit));
    }

    @GetMapping("/merchant/{merchantId}/top-reviews")
    @Operation(summary = "获取置顶评价", description = "获取商户的置顶评价")
    public Result<List<ReviewResponse>> getTopReviews(
            @Parameter(description = "商户ID") @PathVariable Long merchantId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(reviewService.getTopReviews(merchantId, limit));
    }

    @GetMapping("/poi/{poiId}/stats")
    @Operation(summary = "获取POI评价统计", description = "获取POI的评价统计数据")
    public Result<Map<String, Object>> getPoiReviewStats(
            @Parameter(description = "POI ID") @PathVariable Long poiId) {
        return Result.success(reviewService.getPoiReviewStats(poiId));
    }

    @GetMapping("/search")
    @Operation(summary = "搜索评价", description = "根据关键词搜索评价内容")
    public Result<List<ReviewResponse>> searchReviews(
            @Parameter(description = "搜索关键词") @RequestParam String keyword,
            @Parameter(description = "商户ID") @RequestParam(required = false) Long merchantId,
            @Parameter(description = "数量限制") @RequestParam(defaultValue = "20") Integer limit) {
        return Result.success(reviewService.searchReviews(keyword, merchantId, limit));
    }
}
