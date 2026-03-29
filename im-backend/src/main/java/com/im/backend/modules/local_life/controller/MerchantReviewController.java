package com.im.backend.modules.local_life.controller;

import com.im.backend.common.PageResult;
import com.im.backend.common.Result;
import com.im.backend.modules.local_life.dto.*;
import com.im.backend.modules.local_life.service.MerchantReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 商户评价 Controller
 */
@Tag(name = "商户评价", description = "商户评价与口碑相关接口")
@RestController
@RequestMapping("/api/v1/review")
@RequiredArgsConstructor
public class MerchantReviewController {

    private final MerchantReviewService reviewService;

    @Operation(summary = "创建评价")
    @PostMapping("/reviews")
    public Result<MerchantReviewDTO> createReview(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody CreateReviewRequestDTO request) {
        MerchantReviewDTO review = reviewService.createReview(userId, request);
        return Result.success(review);
    }

    @Operation(summary = "获取评价详情")
    @GetMapping("/reviews/{reviewId}")
    public Result<MerchantReviewDTO> getReviewDetail(
            @PathVariable Long reviewId,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        MerchantReviewDTO review = reviewService.getReviewDetail(reviewId, userId);
        return Result.success(review);
    }

    @Operation(summary = "获取商户评价列表")
    @GetMapping("/merchant/{merchantId}/reviews")
    public Result<PageResult<MerchantReviewDTO>> getMerchantReviews(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<MerchantReviewDTO> result = reviewService.getMerchantReviews(
                merchantId, sort, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取用户的评价列表")
    @GetMapping("/user/reviews")
    public Result<PageResult<MerchantReviewDTO>> getUserReviews(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<MerchantReviewDTO> result = reviewService.getUserReviews(userId, page, size);
        return Result.success(result);
    }

    @Operation(summary = "点赞评价")
    @PostMapping("/reviews/{reviewId}/like")
    public Result<Void> likeReview(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reviewId) {
        reviewService.likeReview(reviewId, userId);
        return Result.success();
    }

    @Operation(summary = "取消点赞")
    @DeleteMapping("/reviews/{reviewId}/like")
    public Result<Void> unlikeReview(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reviewId) {
        reviewService.unlikeReview(reviewId, userId);
        return Result.success();
    }

    @Operation(summary = "删除评价")
    @DeleteMapping("/reviews/{reviewId}")
    public Result<Void> deleteReview(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId, userId);
        return Result.success();
    }

    @Operation(summary = "获取商户评价统计")
    @GetMapping("/merchant/{merchantId}/statistic")
    public Result<MerchantReviewStatisticDTO> getMerchantStatistic(
            @PathVariable Long merchantId) {
        MerchantReviewStatisticDTO statistic = reviewService.getMerchantStatistic(merchantId);
        return Result.success(statistic);
    }

    @Operation(summary = "获取有图评价")
    @GetMapping("/merchant/{merchantId}/reviews/with-image")
    public Result<PageResult<MerchantReviewDTO>> getReviewsWithImage(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<MerchantReviewDTO> result = reviewService.getReviewsWithMedia(
                merchantId, false, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取视频评价")
    @GetMapping("/merchant/{merchantId}/reviews/with-video")
    public Result<PageResult<MerchantReviewDTO>> getReviewsWithVideo(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        PageResult<MerchantReviewDTO> result = reviewService.getReviewsWithMedia(
                merchantId, true, page, size);
        return Result.success(result);
    }

    @Operation(summary = "获取优质推荐评价")
    @GetMapping("/merchant/{merchantId}/reviews/recommended")
    public Result<List<MerchantReviewDTO>> getRecommendedReviews(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "5") Integer limit) {
        List<MerchantReviewDTO> reviews = reviewService.getRecommendedReviews(merchantId, limit);
        return Result.success(reviews);
    }

    @Operation(summary = "增加浏览数")
    @PostMapping("/reviews/{reviewId}/view")
    public Result<Void> incrementViewCount(@PathVariable Long reviewId) {
        reviewService.incrementViewCount(reviewId);
        return Result.success();
    }
}
