package com.im.backend.modules.merchant.review.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.result.Result;
import com.im.backend.modules.merchant.review.dto.CreateReviewRequest;
import com.im.backend.modules.merchant.review.dto.ReviewResponse;
import com.im.backend.modules.merchant.review.entity.MerchantReview;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 商户评价服务接口
 * @author IM Development Team
 * @version 1.0.0
 */
public interface MerchantReviewService extends IService<MerchantReview> {

    /**
     * 创建评价
     */
    Result<ReviewResponse> createReview(Long userId, CreateReviewRequest request);

    /**
     * 获取评价详情
     */
    ReviewResponse getReviewDetail(Long reviewId, Long currentUserId);

    /**
     * 分页查询商户评价列表
     */
    IPage<ReviewResponse> getMerchantReviews(Long merchantId, Integer page, Integer size,
                                             BigDecimal ratingMin, BigDecimal ratingMax,
                                             Integer reviewType, Integer sortType);

    /**
     * 分页查询POI评价列表
     */
    IPage<ReviewResponse> getPoiReviews(Long poiId, Integer page, Integer size, Integer sortType);

    /**
     * 查询用户评价列表
     */
    List<ReviewResponse> getUserReviews(Long userId, Integer limit);

    /**
     * 点赞评价
     */
    Result<Void> likeReview(Long userId, Long reviewId);

    /**
     * 取消点赞
     */
    Result<Void> unlikeReview(Long userId, Long reviewId);

    /**
     * 回复评价
     */
    Result<ReviewResponse.ReplyResponse> replyReview(Long userId, Integer userType,
                                                     Long reviewId, String content, Long parentId);

    /**
     * 删除评价
     */
    Result<Void> deleteReview(Long userId, Long reviewId);

    /**
     * 置顶评价
     */
    Result<Void> topReview(Long reviewId, Integer weight, Long operatorId);

    /**
     * 取消置顶
     */
    Result<Void> cancelTopReview(Long reviewId, Long operatorId);

    /**
     * 审核评价
     */
    Result<Void> auditReview(Long reviewId, Integer status, Long auditorId, String remark);

    /**
     * 批量审核评价
     */
    Result<Void> batchAuditReviews(List<Long> reviewIds, Integer status, Long auditorId);

    /**
     * 申诉评价
     */
    Result<Void> appealReview(Long userId, Long reviewId, String reason);

    /**
     * 处理申诉
     */
    Result<Void> processAppeal(Long reviewId, Boolean approved, String remark, Long operatorId);

    /**
     * 获取商户评价统计
     */
    Map<String, Object> getMerchantReviewStats(Long merchantId);

    /**
     * 获取商户评分分布
     */
    Map<String, Integer> getMerchantRatingDistribution(Long merchantId);

    /**
     * 获取推荐评价（优质+高互动）
     */
    List<ReviewResponse> getRecommendedReviews(Long merchantId, Integer limit);

    /**
     * 获取置顶评价
     */
    List<ReviewResponse> getTopReviews(Long merchantId, Integer limit);

    /**
     * 搜索评价
     */
    List<ReviewResponse> searchReviews(String keyword, Long merchantId, Integer limit);

    /**
     * 获取待审核评价列表
     */
    List<ReviewResponse> getPendingReviews(Integer limit);

    /**
     * 获取疑似虚假评价
     */
    List<ReviewResponse> getSuspectedFakeReviews(BigDecimal confidenceMin, Integer limit);

    /**
     * 标记虚假评价
     */
    Result<Void> markFakeReview(Long reviewId, Integer fakeFlag, Long operatorId);

    /**
     * 增加浏览量
     */
    void incrementViewCount(Long reviewId);

    /**
     * 检查用户是否已评价过订单
     */
    Boolean hasReviewedOrder(Long userId, Long orderId);

    /**
     * 获取POI评价统计
     */
    Map<String, Object> getPoiReviewStats(Long poiId);
}
