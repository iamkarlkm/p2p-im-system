package com.im.backend.modules.local_life.review.controller;

import com.im.backend.common.PageResult;
import com.im.backend.common.Result;
import com.im.backend.modules.local_life.review.dto.*;
import com.im.backend.modules.local_life.review.service.MerchantReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商户评价控制器
 * 
 * @author IM Development Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "商户评价", description = "本地生活商户评价管理")
public class MerchantReviewController {
    
    private final MerchantReviewService reviewService;
    
    /**
     * 提交评价
     */
    @PostMapping
    @Operation(summary = "提交评价", description = "用户对商户进行多维度评价")
    public Result<Long> submitReview(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody SubmitReviewRequestDTO request) {
        Long reviewId = reviewService.submitReview(userId, request);
        return Result.success(reviewId);
    }
    
    /**
     * 获取评价详情
     */
    @GetMapping("/{reviewId}")
    @Operation(summary = "获取评价详情", description = "获取单条评价的详细信息")
    public Result<ReviewDetailDTO> getReviewDetail(
            @PathVariable Long reviewId,
            @RequestAttribute(value = "userId", required = false) Long currentUserId) {
        ReviewDetailDTO detail = reviewService.getReviewDetail(reviewId, currentUserId);
        return Result.success(detail);
    }
    
    /**
     * 搜索评价列表
     */
    @PostMapping("/search")
    @Operation(summary = "搜索评价", description = "分页搜索评价列表，支持多条件筛选")
    public Result<PageResult<ReviewDetailDTO>> searchReviews(
            @Valid @RequestBody ReviewSearchRequestDTO request,
            @RequestAttribute(value = "userId", required = false) Long currentUserId) {
        PageResult<ReviewDetailDTO> result = reviewService.searchReviews(request, currentUserId);
        return Result.success(result);
    }
    
    /**
     * 获取商户评价列表
     */
    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "获取商户评价", description = "获取指定商户的评价列表")
    public Result<PageResult<ReviewDetailDTO>> getMerchantReviews(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String reviewType,
            @RequestParam(required = false) String sortBy,
            @RequestAttribute(value = "userId", required = false) Long currentUserId) {
        ReviewSearchRequestDTO request = new ReviewSearchRequestDTO();
        request.setMerchantId(merchantId);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        request.setReviewType(reviewType);
        request.setSortBy(sortBy);
        PageResult<ReviewDetailDTO> result = reviewService.searchReviews(request, currentUserId);
        return Result.success(result);
    }
    
    /**
     * 点赞/取消点赞
     */
    @PostMapping("/like")
    @Operation(summary = "点赞评价", description = "对评价进行点赞或取消点赞")
    public Result<Integer> likeReview(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody LikeReviewRequestDTO request) {
        Integer likeCount = reviewService.likeReview(userId, request);
        return Result.success(likeCount);
    }
    
    /**
     * 回复评价
     */
    @PostMapping("/reply")
    @Operation(summary = "回复评价", description = "对评价进行回复")
    public Result<Long> replyReview(
            @RequestAttribute("userId") Long userId,
            @RequestAttribute("userType") String userType,
            @Valid @RequestBody ReplyReviewRequestDTO request) {
        Long replyId = reviewService.replyReview(userId, userType, request);
        return Result.success(replyId);
    }
    
    /**
     * 获取评价回复列表
     */
    @GetMapping("/{reviewId}/replies")
    @Operation(summary = "获取回复列表", description = "获取评价的所有回复")
    public Result<PageResult<ReviewReplyDTO>> getReviewReplies(
            @PathVariable Long reviewId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<ReviewReplyDTO> replies = reviewService.getReviewReplies(reviewId, pageNum, pageSize);
        return Result.success(replies);
    }
    
    /**
     * 删除评价
     */
    @DeleteMapping("/{reviewId}")
    @Operation(summary = "删除评价", description = "用户删除自己发布的评价")
    public Result<Void> deleteReview(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reviewId) {
        reviewService.deleteReview(userId, reviewId);
        return Result.success();
    }
    
    /**
     * 举报评价
     */
    @PostMapping("/{reviewId}/report")
    @Operation(summary = "举报评价", description = "举报违规评价")
    public Result<Void> reportReview(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long reviewId,
            @RequestParam String reason,
            @RequestParam(required = false) String description) {
        reviewService.reportReview(userId, reviewId, reason, description);
        return Result.success();
    }
    
    /**
     * 获取商户评价统计
     */
    @GetMapping("/merchant/{merchantId}/statistics")
    @Operation(summary = "评价统计", description = "获取商户的评价统计数据")
    public Result<ReviewStatisticsDTO> getReviewStatistics(
            @PathVariable Long merchantId) {
        ReviewStatisticsDTO statistics = reviewService.getReviewStatistics(merchantId);
        return Result.success(statistics);
    }
    
    /**
     * 获取我的评价
     */
    @GetMapping("/my")
    @Operation(summary = "我的评价", description = "获取当前用户发布的评价列表")
    public Result<PageResult<ReviewDetailDTO>> getMyReviews(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        PageResult<ReviewDetailDTO> reviews = reviewService.getUserReviews(userId, pageNum, pageSize);
        return Result.success(reviews);
    }
}
