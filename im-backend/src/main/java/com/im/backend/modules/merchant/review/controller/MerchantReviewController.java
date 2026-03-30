package com.im.backend.modules.merchant.review.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.Result;
import com.im.backend.modules.merchant.review.dto.ReviewSubmitRequest;
import com.im.backend.modules.merchant.review.dto.ReviewResponse;
import com.im.backend.modules.merchant.review.dto.ReviewStatsResponse;
import com.im.backend.modules.merchant.review.entity.MerchantReview;
import com.im.backend.modules.merchant.review.service.IMerchantReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 商户评价控制器 - 功能#310: 本地商户评价口碑
 */
@Tag(name = "商户评价", description = "本地商户评价口碑相关接口")
@RestController
@RequestMapping("/api/merchant/review")
@RequiredArgsConstructor
public class MerchantReviewController {

    private final IMerchantReviewService reviewService;

    @Operation(summary = "提交评价")
    @PostMapping("/submit")
    public Result<ReviewResponse> submitReview(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Validated ReviewSubmitRequest request) {
        return reviewService.submitReview(userId, request);
    }

    @Operation(summary = "获取商户评价列表")
    @GetMapping("/list/{merchantId}")
    public Result<IPage<ReviewResponse>> getMerchantReviews(
            @Parameter(description = "商户ID") @PathVariable Long merchantId,
            @Parameter(description = "评分筛选") @RequestParam(required = false) Integer rating,
            @Parameter(description = "是否有图") @RequestParam(required = false) Boolean hasImage,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.getMerchantReviews(merchantId, rating, hasImage, new Page<>(page, size)));
    }

    @Operation(summary = "获取评价详情")
    @GetMapping("/detail/{reviewId}")
    public Result<ReviewResponse> getReviewDetail(
            @PathVariable Long reviewId,
            @RequestAttribute(value = "userId", required = false) Long currentUserId) {
        return Result.success(reviewService.getReviewDetail(reviewId, currentUserId));
    }

    @Operation(summary = "获取商户评分统计")
    @GetMapping("/stats/{merchantId}")
    public Result<ReviewStatsResponse> getReviewStats(
            @Parameter(description = "商户ID") @PathVariable Long merchantId) {
        return Result.success(reviewService.getReviewStats(merchantId));
    }

    @Operation(summary = "商家回复评价")
    @PostMapping("/reply/{reviewId}")
    public Result<Void> merchantReply(
            @RequestAttribute("merchantId") Long merchantId,
            @PathVariable Long reviewId,
            @RequestParam String reply) {
        return reviewService.merchantReply(merchantId, reviewId, reply);
    }

    @Operation(summary = "点赞/取消点赞评价")
    @PostMapping("/like/{reviewId}")
    public Result<Boolean> toggleLike(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reviewId) {
        return reviewService.toggleLike(userId, reviewId);
    }

    @Operation(summary = "获取我的评价列表")
    @GetMapping("/my-reviews")
    public Result<IPage<ReviewResponse>> getMyReviews(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(reviewService.getUserReviews(userId, new Page<>(page, size)));
    }

    @Operation(summary = "获取推荐评价")
    @GetMapping("/recommended/{merchantId}")
    public Result<List<ReviewResponse>> getRecommendedReviews(
            @Parameter(description = "商户ID") @PathVariable Long merchantId,
            @RequestParam(defaultValue = "5") Integer limit) {
        return Result.success(reviewService.getRecommendedReviews(merchantId, limit));
    }

    @Operation(summary = "删除我的评价")
    @DeleteMapping("/delete/{reviewId}")
    public Result<Void> deleteReview(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reviewId) {
        return reviewService.deleteReview(userId, reviewId);
    }

    @Operation(summary = "审核评价 (管理员)")
    @PostMapping("/audit/{reviewId}")
    public Result<Void> auditReview(
            @PathVariable Long reviewId,
            @RequestParam Integer status,
            @RequestParam(required = false) String rejectReason) {
        return reviewService.auditReview(reviewId, status, rejectReason);
    }
}
