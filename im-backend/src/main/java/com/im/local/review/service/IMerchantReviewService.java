package com.im.local.review.service;

import com.im.local.review.dto.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 商户评价服务接口
 */
public interface IMerchantReviewService {

    /**
     * 创建评价
     */
    ReviewResponse createReview(Long userId, CreateReviewRequest request);

    /**
     * 查询评价列表
     */
    Page<ReviewResponse> queryReviews(ReviewQueryRequest request);

    /**
     * 获取评价详情
     */
    ReviewResponse getReviewDetail(Long reviewId, Long currentUserId);

    /**
     * 点赞/取消点赞
     */
    void likeReview(Long userId, LikeReviewRequest request);

    /**
     * 商家回复评价
     */
    void merchantReply(Long merchantId, MerchantReplyRequest request);

    /**
     * 删除评价（用户或商家）
     */
    void deleteReview(Long reviewId, Long operatorId, Integer operatorType);

    /**
     * 获取商户口碑统计
     */
    MerchantReputationResponse getMerchantReputation(Long merchantId);

    /**
     * 审核评价（AI或人工）
     */
    void auditReview(Long reviewId, Integer status, String reason);
}
