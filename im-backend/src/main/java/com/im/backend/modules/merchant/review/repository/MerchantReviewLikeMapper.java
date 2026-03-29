package com.im.backend.modules.merchant.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.review.entity.MerchantReviewLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Delete;

/**
 * 评价点赞Mapper
 */
@Mapper
public interface MerchantReviewLikeMapper extends BaseMapper<MerchantReviewLike> {

    /**
     * 查询用户是否已点赞
     */
    @Select("SELECT COUNT(*) FROM merchant_review_like WHERE review_id = #{reviewId} AND user_id = #{userId}")
    int countByReviewAndUser(@Param("reviewId") String reviewId, @Param("userId") Long userId);

    /**
     * 删除点赞记录
     */
    @Delete("DELETE FROM merchant_review_like WHERE review_id = #{reviewId} AND user_id = #{userId}")
    int deleteByReviewAndUser(@Param("reviewId") String reviewId, @Param("userId") Long userId);

    /**
     * 查询评价的总点赞数
     */
    @Select("SELECT COUNT(*) FROM merchant_review_like WHERE review_id = #{reviewId}")
    int countByReviewId(@Param("reviewId") String reviewId);
}
