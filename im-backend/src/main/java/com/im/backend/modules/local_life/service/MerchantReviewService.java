package com.im.backend.modules.local_life.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.common.PageResult;
import com.im.backend.modules.local_life.dto.*;
import com.im.backend.modules.local_life.entity.MerchantReview;

import java.util.List;

/**
 * 商户评价 Service
 */
public interface MerchantReviewService extends IService<MerchantReview> {

    /**
     * 创建评价
     */
    MerchantReviewDTO createReview(Long userId, CreateReviewRequestDTO request);

    /**
     * 获取评价详情
     */
    MerchantReviewDTO getReviewDetail(Long reviewId, Long currentUserId);

    /**
     * 获取商户评价列表
     */
    PageResult<MerchantReviewDTO> getMerchantReviews(Long merchantId, String sortType, Integer page, Integer size);

    /**
     * 获取用户的评价列表
     */
    PageResult<MerchantReviewDTO> getUserReviews(Long userId, Integer page, Integer size);

    /**
     * 点赞评价
     */
    void likeReview(Long reviewId, Long userId);

    /**
     * 取消点赞
     */
    void unlikeReview(Long reviewId, Long userId);

    /**
     * 删除评价
     */
    void deleteReview(Long reviewId, Long userId);

    /**
     * 审核评价
     */
    void auditReview(Long reviewId, Integer status, String remark, Long auditBy);

    /**
     * 获取商户评价统计
     */
    MerchantReviewStatisticDTO getMerchantStatistic(Long merchantId);

    /**
     * 获取有图/视频评价
     */
    PageResult<MerchantReviewDTO> getReviewsWithMedia(Long merchantId, Boolean hasVideo, Integer page, Integer size);

    /**
     * 获取优质推荐评价
     */
    List<MerchantReviewDTO> getRecommendedReviews(Long merchantId, Integer limit);

    /**
     * 增加浏览数
     */
    void incrementViewCount(Long reviewId);
}
