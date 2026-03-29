package com.im.backend.modules.merchant.review.controller;

import com.im.backend.common.core.result.CommonResult;
import com.im.backend.common.core.result.PageResult;
import com.im.backend.modules.merchant.review.dto.*;
import com.im.backend.modules.merchant.review.service.IMerchantReviewReplyService;
import com.im.backend.modules.merchant.review.service.IMerchantReviewService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商户评价控制器
 * 提供评价提交、查询、点赞、回复等功能
 */
@RestController
@RequestMapping("/api/v1/merchant-reviews")
@RequiredArgsConstructor
@Api(tags = "商户评价管理")
public class MerchantReviewController {

    private final IMerchantReviewService reviewService;
    private final IMerchantReviewReplyService replyService;

    @PostMapping("/submit")
    @ApiOperation("提交评价")
    public CommonResult<String> submitReview(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody SubmitReviewRequest request) {
        String reviewId = reviewService.submitReview(userId, request);
        return CommonResult.success(reviewId);
    }

    @GetMapping("/{reviewId}")
    @ApiOperation("获取评价详情")
    public CommonResult<ReviewDetailResponse> getReviewDetail(
            @PathVariable String reviewId,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        ReviewDetailResponse detail = reviewService.getReviewDetail(reviewId, userId);
        return CommonResult.success(detail);
    }

    @GetMapping("/merchant/{merchantId}")
    @ApiOperation("获取商户评价列表")
    public CommonResult<ReviewListResponse> getMerchantReviews(
            @PathVariable Long merchantId,
            ReviewListRequest request) {
        request.setMerchantId(merchantId);
        ReviewListResponse reviews = reviewService.getMerchantReviews(merchantId, request);
        return CommonResult.success(reviews);
    }

    @DeleteMapping("/{reviewId}")
    @ApiOperation("删除评价")
    public CommonResult<Void> deleteReview(
            @RequestAttribute("userId") Long userId,
            @PathVariable String reviewId) {
        reviewService.deleteReview(reviewId, userId);
        return CommonResult.success();
    }

    @PostMapping("/like")
    @ApiOperation("点赞/取消点赞评价")
    public CommonResult<Void> likeReview(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody LikeReviewRequest request) {
        reviewService.likeReview(userId, request);
        return CommonResult.success();
    }

    @GetMapping("/{reviewId}/has-liked")
    @ApiOperation("检查是否已点赞")
    public CommonResult<Boolean> hasLiked(
            @RequestAttribute("userId") Long userId,
            @PathVariable String reviewId) {
        boolean hasLiked = reviewService.hasLiked(reviewId, userId);
        return CommonResult.success(hasLiked);
    }

    @PostMapping("/reply")
    @ApiOperation("回复评价")
    public CommonResult<String> replyReview(
            @RequestAttribute("userId") Long userId,
            @RequestAttribute("userType") Integer userType,
            @Valid @RequestBody ReplyReviewRequest request) {
        String replyId = replyService.replyReview(userId, userType, request);
        return CommonResult.success(replyId);
    }

    @GetMapping("/{reviewId}/replies")
    @ApiOperation("获取评价回复列表")
    public CommonResult<java.util.List<ReviewReplyDTO>> getReviewReplies(
            @PathVariable String reviewId) {
        java.util.List<ReviewReplyDTO> replies = replyService.getReviewReplies(reviewId);
        return CommonResult.success(replies);
    }

    @PostMapping("/report")
    @ApiOperation("举报评价")
    public CommonResult<Void> reportReview(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody ReportReviewRequest request) {
        reviewService.reportReview(userId, request);
        return CommonResult.success();
    }

    @GetMapping("/user/my-reviews")
    @ApiOperation("获取我的评价列表")
    public CommonResult<ReviewListResponse> getMyReviews(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        ReviewListResponse reviews = reviewService.getUserReviews(userId, page, size);
        return CommonResult.success(reviews);
    }

    // ============= 管理员接口 =============

    @PostMapping("/admin/audit")
    @ApiOperation("【管理员】审核评价")
    public CommonResult<Void> auditReview(
            @RequestAttribute("userId") Long auditorId,
            @Valid @RequestBody AuditReviewRequest request) {
        reviewService.auditReview(auditorId, request);
        return CommonResult.success();
    }

    @GetMapping("/admin/pending")
    @ApiOperation("【管理员】获取待审核评价列表")
    public CommonResult<PageResult<ReviewDetailResponse>> getPendingReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {
        PageResult<ReviewDetailResponse> reviews = reviewService.getPendingReviews(page, size);
        return CommonResult.success(reviews);
    }
}
