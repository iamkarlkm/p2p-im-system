package com.im.backend.modules.merchant.review.service;

import com.im.backend.common.core.result.PageResult;
import com.im.backend.modules.merchant.review.dto.*;

/**
 * 商户评价服务接口
 */
public interface IMerchantReviewService {

    /**
     * 提交评价
     */
    String submitReview(Long userId, SubmitReviewRequest request);

    /**
     * 获取评价详情
     */
    ReviewDetailResponse getReviewDetail(String reviewId, Long currentUserId);

    /**
     * 获取商户评价列表
     */
    ReviewListResponse getMerchantReviews(Long merchantId, ReviewListRequest request);

    /**
     * 删除评价
     */
    void deleteReview(String reviewId, Long userId);

    /**
     * 点赞评价
     */
    void likeReview(Long userId, LikeReviewRequest request);

    /**
     * 评价是否已点赞
     */
    boolean hasLiked(String reviewId, Long userId);

    /**
     * 审核评价
     */
    void auditReview(Long auditorId, AuditReviewRequest request);

    /**
     * 举报评价
     */
    void reportReview(Long reporterId, ReportReviewRequest request);

    /**
     * 获取待审核评价列表
     */
    PageResult<ReviewDetailResponse> getPendingReviews(Integer page, Integer size);

    /**
     * 获取用户发布的评价
     */
    ReviewListResponse getUserReviews(Long userId, Integer page, Integer size);
}
