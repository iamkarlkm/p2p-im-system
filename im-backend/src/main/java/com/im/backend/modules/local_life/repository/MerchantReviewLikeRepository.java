package com.im.backend.modules.local_life.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local_life.entity.MerchantReviewLike;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 评价点赞 Repository
 */
public interface MerchantReviewLikeRepository extends BaseMapper<MerchantReviewLike> {

    /**
     * 查询用户是否点赞过该评价
     */
    @Select("SELECT COUNT(*) FROM merchant_review_like WHERE review_id = #{reviewId} AND user_id = #{userId}")
    Integer checkUserLiked(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    /**
     * 删除点赞记录
     */
    int deleteByReviewAndUser(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    /**
     * 统计评价的总点赞数
     */
    @Select("SELECT COUNT(*) FROM merchant_review_like WHERE review_id = #{reviewId}")
    Integer countByReviewId(@Param("reviewId") Long reviewId);
}
