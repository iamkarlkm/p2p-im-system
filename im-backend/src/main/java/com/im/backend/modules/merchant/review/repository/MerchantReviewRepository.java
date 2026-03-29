package com.im.backend.modules.merchant.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.merchant.review.entity.MerchantReview;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商户评价数据访问层
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Repository
public interface MerchantReviewRepository extends BaseMapper<MerchantReview> {

    /**
     * 分页查询商户评价列表
     */
    IPage<MerchantReview> selectReviewPage(
        Page<MerchantReview> page,
        @Param("merchantId") Long merchantId,
        @Param("status") Integer status,
        @Param("minRating") BigDecimal minRating,
        @Param("maxRating") BigDecimal maxRating,
        @Param("hasMedia") Boolean hasMedia,
        @Param("isHighQuality") Boolean isHighQuality,
        @Param("orderBy") String orderBy
    );

    /**
     * 查询商户评价统计
     */
    @Select("SELECT " +
            "  COUNT(*) as totalCount, " +
            "  AVG(overall_rating) as avgRating, " +
            "  COUNT(CASE WHEN overall_rating = 5 THEN 1 END) as fiveStarCount, " +
            "  COUNT(CASE WHEN overall_rating = 4 THEN 1 END) as fourStarCount, " +
            "  COUNT(CASE WHEN overall_rating = 3 THEN 1 END) as threeStarCount, " +
            "  COUNT(CASE WHEN overall_rating = 2 THEN 1 END) as twoStarCount, " +
            "  COUNT(CASE WHEN overall_rating = 1 THEN 1 END) as oneStarCount, " +
            "  AVG(taste_rating) as avgTaste, " +
            "  AVG(environment_rating) as avgEnvironment, " +
            "  AVG(service_rating) as avgService, " +
            "  AVG(value_rating) as avgValue " +
            "FROM merchant_review " +
            "WHERE merchant_id = #{merchantId} " +
            "  AND status = 1 " +
            "  AND deleted = 0")
    ReviewStatisticsVO selectMerchantReviewStatistics(@Param("merchantId") Long merchantId);

    /**
     * 查询用户的评价列表
     */
    @Select("SELECT * FROM merchant_review " +
            "WHERE user_id = #{userId} " +
            "  AND deleted = 0 " +
            "ORDER BY create_time DESC")
    List<MerchantReview> selectUserReviews(@Param("userId") Long userId);

    /**
     * 增加点赞数
     */
    @Update("UPDATE merchant_review SET like_count = like_count + 1 WHERE review_id = #{reviewId}")
    int incrementLikeCount(@Param("reviewId") Long reviewId);

    /**
     * 减少点赞数
     */
    @Update("UPDATE merchant_review SET like_count = GREATEST(0, like_count - 1) WHERE review_id = #{reviewId}")
    int decrementLikeCount(@Param("reviewId") Long reviewId);

    /**
     * 增加回复数
     */
    @Update("UPDATE merchant_review SET reply_count = reply_count + 1 WHERE review_id = #{reviewId}")
    int incrementReplyCount(@Param("reviewId") Long reviewId);

    /**
     * 增加浏览数
     */
    @Update("UPDATE merchant_review SET view_count = view_count + 1 WHERE review_id = #{reviewId}")
    int incrementViewCount(@Param("reviewId") Long reviewId);

    /**
     * 查询待审核的评价列表
     */
    @Select("SELECT * FROM merchant_review " +
            "WHERE status = 0 " +
            "  AND deleted = 0 " +
            "ORDER BY create_time ASC")
    List<MerchantReview> selectPendingReviews();

    /**
     * 查询虚假评价列表
     */
    @Select("SELECT * FROM merchant_review " +
            "WHERE is_fake > 0 " +
            "  AND deleted = 0 " +
            "ORDER BY is_fake DESC, create_time DESC")
    List<MerchantReview> selectFakeReviews();

    /**
     * 批量更新评价状态
     */
    int batchUpdateStatus(@Param("reviewIds") List<Long> reviewIds, 
                          @Param("status") Integer status,
                          @Param("auditorId") Long auditorId);

    /**
     * 查询商户的优质评价
     */
    List<MerchantReview> selectHighQualityReviews(@Param("merchantId") Long merchantId, 
                                                  @Param("limit") Integer limit);
}
