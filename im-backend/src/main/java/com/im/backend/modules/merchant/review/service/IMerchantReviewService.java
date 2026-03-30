package com.im.backend.modules.merchant.review.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.Result;
import com.im.backend.modules.merchant.review.dto.ReviewSubmitRequest;
import com.im.backend.modules.merchant.review.dto.ReviewResponse;
import com.im.backend.modules.merchant.review.dto.ReviewStatsResponse;
import com.im.backend.modules.merchant.review.entity.MerchantReview;

import java.util.List;

/**
 * 商户评价服务接口 - 功能#310: 本地商户评价口碑
 */
public interface IMerchantReviewService {

    /**
     * 提交评价
     */
    Result<ReviewResponse> submitReview(Long userId, ReviewSubmitRequest request);

    /**
     * 分页查询商户评价
     */
    IPage<ReviewResponse> getMerchantReviews(Long merchantId, Integer rating, Boolean hasImage, Page<MerchantReview> page);

    /**
     * 获取评价详情
     */
    ReviewResponse getReviewDetail(Long reviewId, Long currentUserId);

    /**
     * 获取商户评分统计
     */
    ReviewStatsResponse getReviewStats(Long merchantId);

    /**
     * 商家回复评价
     */
    Result<Void> merchantReply(Long merchantId, Long reviewId, String reply);

    /**
     * 点赞/取消点赞评价
     */
    Result<Boolean> toggleLike(Long userId, Long reviewId);

    /**
     * 获取用户的评价列表
     */
    IPage<ReviewResponse> getUserReviews(Long userId, Page<MerchantReview> page);

    /**
     * 获取推荐评价
     */
    List<ReviewResponse> getRecommendedReviews(Long merchantId, Integer limit);

    /**
     * 删除评价 (用户自己删除)
     */
    Result<Void> deleteReview(Long userId, Long reviewId);

    /**
     * 审核评价 (管理员)
     */
    Result<Void> auditReview(Long reviewId, Integer status, String rejectReason);
}
