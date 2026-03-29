package com.im.local.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.local.review.entity.MerchantReviewLike;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 评价点赞数据访问层
 */
@Mapper
public interface MerchantReviewLikeMapper extends BaseMapper<MerchantReviewLike> {

    /**
     * 查询用户是否点赞
     */
    @Select("SELECT COUNT(*) FROM merchant_review_like WHERE review_id = #{reviewId} AND user_id = #{userId}")
    int countByUser(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    /**
     * 删除点赞记录
     */
    int deleteByUser(@Param("reviewId") Long reviewId, @Param("userId") Long userId);

    /**
     * 获取评价点赞数
     */
    @Select("SELECT COUNT(*) FROM merchant_review_like WHERE review_id = #{reviewId}")
    int countByReviewId(@Param("reviewId") Long reviewId);
}
