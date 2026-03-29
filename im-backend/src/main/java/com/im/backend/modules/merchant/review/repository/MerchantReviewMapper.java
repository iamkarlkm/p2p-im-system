package com.im.backend.modules.merchant.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.merchant.review.entity.MerchantReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商户评价Mapper
 */
@Mapper
public interface MerchantReviewMapper extends BaseMapper<MerchantReview> {

    /**
     * 根据商户ID分页查询评价列表
     */
    @Select("SELECT * FROM merchant_review WHERE merchant_id = #{merchantId} AND status = 1 ORDER BY created_at DESC")
    IPage<MerchantReview> selectByMerchantId(Page<MerchantReview> page, @Param("merchantId") Long merchantId);

    /**
     * 查询商户评价数量
     */
    @Select("SELECT COUNT(*) FROM merchant_review WHERE merchant_id = #{merchantId} AND status = 1")
    Integer countByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 查询商户平均评分
     */
    @Select("SELECT AVG(overall_rating) FROM merchant_review WHERE merchant_id = #{merchantId} AND status = 1")
    Double selectAverageRating(@Param("merchantId") Long merchantId);

    /**
     * 更新点赞数
     */
    @Update("UPDATE merchant_review SET like_count = like_count + #{delta} WHERE review_id = #{reviewId}")
    int updateLikeCount(@Param("reviewId") String reviewId, @Param("delta") int delta);

    /**
     * 更新回复数
     */
    @Update("UPDATE merchant_review SET reply_count = reply_count + 1 WHERE review_id = #{reviewId}")
    int incrementReplyCount(@Param("reviewId") String reviewId);

    /**
     * 查询用户是否已评价订单
     */
    @Select("SELECT COUNT(*) FROM merchant_review WHERE user_id = #{userId} AND order_id = #{orderId} AND status != 3")
    int countByUserAndOrder(@Param("userId") Long userId, @Param("orderId") Long orderId);

    /**
     * 根据评价ID查询
     */
    @Select("SELECT * FROM merchant_review WHERE review_id = #{reviewId}")
    MerchantReview selectByReviewId(@Param("reviewId") String reviewId);

    /**
     * 查询待审核评价列表
     */
    @Select("SELECT * FROM merchant_review WHERE status = 0 ORDER BY created_at DESC")
    IPage<MerchantReview> selectPendingReviews(Page<MerchantReview> page);

    /**
     * 查询用户发布的评价
     */
    @Select("SELECT * FROM merchant_review WHERE user_id = #{userId} AND status = 1 ORDER BY created_at DESC")
    IPage<MerchantReview> selectByUserId(Page<MerchantReview> page, @Param("userId") Long userId);

    /**
     * 查询优质评价（用于推荐）
     */
    @Select("SELECT * FROM merchant_review WHERE status = 1 AND overall_rating >= 4 AND quality_score >= 80 ORDER BY created_at DESC LIMIT #{limit}")
    List<MerchantReview> selectEliteReviews(@Param("limit") int limit);

    /**
     * 更新浏览数
     */
    @Update("UPDATE merchant_review SET view_count = view_count + 1 WHERE review_id = #{reviewId}")
    int incrementViewCount(@Param("reviewId") String reviewId);
}
