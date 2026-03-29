package com.im.backend.modules.merchant.review.mapper;

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
 * @author IM Development Team
 * @version 1.0.0
 */
@Repository
public interface MerchantReviewMapper extends BaseMapper<MerchantReview> {

    /**
     * 分页查询商户评价列表
     */
    IPage<MerchantReview> selectReviewPage(Page<MerchantReview> page,
                                           @Param("merchantId") Long merchantId,
                                           @Param("poiId") Long poiId,
                                           @Param("userId") Long userId,
                                           @Param("ratingMin") BigDecimal ratingMin,
                                           @Param("ratingMax") BigDecimal ratingMax,
                                           @Param("reviewType") Integer reviewType,
                                           @Param("isQuality") Integer isQuality,
                                           @Param("status") Integer status,
                                           @Param("hasMedia") Boolean hasMedia,
                                           @Param("sortType") Integer sortType);

    /**
     * 查询用户评价列表
     */
    List<MerchantReview> selectUserReviews(@Param("userId") Long userId,
                                          @Param("limit") Integer limit);

    /**
     * 查询商户评价统计
     */
    @Select("SELECT COUNT(*) FROM merchant_review WHERE merchant_id = #{merchantId} AND deleted = 0 AND status = 1")
    Integer countMerchantReviews(@Param("merchantId") Long merchantId);

    /**
     * 查询商户平均评分
     */
    @Select("SELECT AVG(overall_rating) FROM merchant_review WHERE merchant_id = #{merchantId} AND deleted = 0 AND status = 1")
    BigDecimal selectMerchantAverageRating(@Param("merchantId") Long merchantId);

    /**
     * 查询商户各星级评价数量
     */
    @Select("SELECT overall_rating as rating, COUNT(*) as count FROM merchant_review " +
            "WHERE merchant_id = #{merchantId} AND deleted = 0 AND status = 1 " +
            "GROUP BY overall_rating")
    List<java.util.Map<String, Object>> selectMerchantRatingDistribution(@Param("merchantId") Long merchantId);

    /**
     * 查询商户优质评价数量
     */
    @Select("SELECT COUNT(*) FROM merchant_review WHERE merchant_id = #{merchantId} AND is_quality = 1 AND deleted = 0 AND status = 1")
    Integer countQualityReviews(@Param("merchantId") Long merchantId);

    /**
     * 增加浏览数
     */
    @Update("UPDATE merchant_review SET view_count = view_count + 1 WHERE id = #{reviewId}")
    int incrementViewCount(@Param("reviewId") Long reviewId);

    /**
     * 增加点赞数
     */
    @Update("UPDATE merchant_review SET like_count = like_count + 1 WHERE id = #{reviewId}")
    int incrementLikeCount(@Param("reviewId") Long reviewId);

    /**
     * 减少点赞数
     */
    @Update("UPDATE merchant_review SET like_count = GREATEST(0, like_count - 1) WHERE id = #{reviewId}")
    int decrementLikeCount(@Param("reviewId") Long reviewId);

    /**
     * 增加回复数
     */
    @Update("UPDATE merchant_review SET reply_count = reply_count + 1 WHERE id = #{reviewId}")
    int incrementReplyCount(@Param("reviewId") Long reviewId);

    /**
     * 查询待审核评价列表
     */
    List<MerchantReview> selectPendingReviews(@Param("limit") Integer limit);

    /**
     * 查询疑似虚假评价
     */
    List<MerchantReview> selectSuspectedFakeReviews(@Param("confidenceMin") BigDecimal confidenceMin,
                                                    @Param("limit") Integer limit);

    /**
     * 查询申诉中的评价
     */
    List<MerchantReview> selectAppealingReviews(@Param("limit") Integer limit);

    /**
     * 查询置顶评价列表
     */
    List<MerchantReview> selectTopReviews(@Param("merchantId") Long merchantId,
                                         @Param("limit") Integer limit);

    /**
     * 查询推荐评价列表（优质+高互动）
     */
    List<MerchantReview> selectRecommendedReviews(@Param("merchantId") Long merchantId,
                                                  @Param("limit") Integer limit);

    /**
     * 搜索评价内容
     */
    List<MerchantReview> searchReviews(@Param("keyword") String keyword,
                                      @Param("merchantId") Long merchantId,
                                      @Param("limit") Integer limit);

    /**
     * 查询用户是否已评价过某订单
     */
    @Select("SELECT COUNT(*) FROM merchant_review WHERE order_id = #{orderId} AND user_id = #{userId} AND deleted = 0")
    Integer countReviewByOrder(@Param("orderId") Long orderId, @Param("userId") Long userId);

    /**
     * 批量更新评价状态
     */
    int batchUpdateStatus(@Param("reviewIds") List<Long> reviewIds,
                         @Param("status") Integer status,
                         @Param("auditorId") Long auditorId);

    /**
     * 查询POI评价统计
     */
    @Select("SELECT COUNT(*), AVG(overall_rating) FROM merchant_review " +
            "WHERE poi_id = #{poiId} AND deleted = 0 AND status = 1")
    java.util.Map<String, Object> selectPoiReviewStats(@Param("poiId") Long poiId);
}
