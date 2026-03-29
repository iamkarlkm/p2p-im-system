package com.im.backend.modules.merchant.review.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.merchant.review.dto.MerchantReviewDTO;
import com.im.backend.modules.merchant.review.entity.MerchantReview;
import com.im.backend.modules.merchant.review.vo.ReviewStatisticsVO;

import java.util.List;

/**
 * 商户评价服务接口
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface MerchantReviewService extends IService<MerchantReview> {

    /**
     * 创建评价
     */
    MerchantReviewDTO createReview(MerchantReviewDTO reviewDTO, Long userId);

    /**
     * 更新评价
     */
    MerchantReviewDTO updateReview(Long reviewId, MerchantReviewDTO reviewDTO, Long userId);

    /**
     * 删除评价
     */
    boolean deleteReview(Long reviewId, Long userId);

    /**
     * 获取评价详情
     */
    MerchantReviewDTO getReviewDetail(Long reviewId, Long currentUserId);

    /**
     * 分页查询商户评价
     */
    IPage<MerchantReviewDTO> getMerchantReviews(Page<MerchantReview> page, Long merchantId, 
                                                 Integer status, String sortType);

    /**
     * 获取商户评价统计
     */
    ReviewStatisticsVO getMerchantStatistics(Long merchantId);

    /**
     * 获取用户的评价列表
     */
    List<MerchantReviewDTO> getUserReviews(Long userId);

    /**
     * 点赞评价
     */
    boolean likeReview(Long reviewId, Long userId);

    /**
     * 取消点赞
     */
    boolean unlikeReview(Long reviewId, Long userId);

    /**
     * 商家回复评价
     */
    boolean replyReview(Long reviewId, String replyContent, Long merchantUserId);

    /**
     * 审核评价
     */
    boolean auditReview(Long reviewId, Integer status, String remark, Long auditorId);

    /**
     * 批量审核评价
     */
    boolean batchAuditReviews(List<Long> reviewIds, Integer status, Long auditorId);

    /**
     * 获取待审核评价列表
     */
    List<MerchantReviewDTO> getPendingReviews();

    /**
     * 获取优质评价列表
     */
    List<MerchantReviewDTO> getHighQualityReviews(Long merchantId, Integer limit);

    /**
     * AI检测虚假评价
     */
    boolean detectFakeReview(Long reviewId);

    /**
     * 获取商户口碑榜单
     */
    List<MerchantReviewDTO> getReputationRanking(String category, String areaCode, Integer limit);

    /**
     * 分享评价
     */
    boolean shareReview(Long reviewId);

    /**
     * 增加浏览量
     */
    boolean incrementViewCount(Long reviewId);
}
