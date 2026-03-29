package com.im.local.review.controller;

import com.im.local.review.dto.*;
import com.im.local.review.service.IMerchantReviewService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 商户评价控制器
 * 提供评价发布、查询、点赞、回复等API
 */
@RestController
@RequestMapping("/api/v1/merchant/review")
@RequiredArgsConstructor
public class MerchantReviewController {

    private final IMerchantReviewService reviewService;

    /**
     * 发布评价
     */
    @PostMapping
    public ReviewResponse createReview(@RequestAttribute("userId") Long userId,
                                       @Valid @RequestBody CreateReviewRequest request) {
        return reviewService.createReview(userId, request);
    }

    /**
     * 查询评价列表
     */
    @GetMapping("/list")
    public Page<ReviewResponse> queryReviews(ReviewQueryRequest request) {
        return reviewService.queryReviews(request);
    }

    /**
     * 获取评价详情
     */
    @GetMapping("/{reviewId}")
    public ReviewResponse getReviewDetail(@PathVariable Long reviewId,
                                          @RequestAttribute(value = "userId", required = false) Long userId) {
        return reviewService.getReviewDetail(reviewId, userId);
    }

    /**
     * 点赞/取消点赞
     */
    @PostMapping("/{reviewId}/like")
    public void likeReview(@RequestAttribute("userId") Long userId,
                           @PathVariable Long reviewId,
                           @RequestBody LikeReviewRequest request) {
        request.setReviewId(reviewId);
        reviewService.likeReview(userId, request);
    }

    /**
     * 商家回复评价
     */
    @PostMapping("/{reviewId}/reply")
    public void merchantReply(@RequestAttribute("merchantId") Long merchantId,
                              @PathVariable Long reviewId,
                              @RequestBody MerchantReplyRequest request) {
        request.setReviewId(reviewId);
        reviewService.merchantReply(merchantId, request);
    }

    /**
     * 删除评价
     */
    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable Long reviewId,
                             @RequestAttribute("userId") Long userId) {
        reviewService.deleteReview(reviewId, userId, 1);
    }

    /**
     * 获取商户口碑统计
     */
    @GetMapping("/reputation/{merchantId}")
    public MerchantReputationResponse getMerchantReputation(@PathVariable Long merchantId) {
        return reviewService.getMerchantReputation(merchantId);
    }

    /**
     * 获取我的评价列表
     */
    @GetMapping("/my")
    public Page<ReviewResponse> getMyReviews(@RequestAttribute("userId") Long userId,
                                             @RequestParam(defaultValue = "1") Integer pageNum,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        ReviewQueryRequest request = new ReviewQueryRequest();
        request.setUserId(userId);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        return reviewService.queryReviews(request);
    }
}
