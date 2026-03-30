package com.im.backend.modules.merchant.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.review.entity.MerchantReviewLike;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

/**
 * 商户评价点赞数据访问层 - 功能#310: 本地商户评价口碑
 */
@Repository
public interface MerchantReviewLikeMapper extends BaseMapper<MerchantReviewLike> {

    /**
     * 查询用户是否点赞
     */
    @Select("SELECT * FROM merchant_review_like WHERE review_id = #{reviewId} AND user_id = #{userId} AND like_type = #{likeType} LIMIT 1")
    MerchantReviewLike selectByUserAndReview(@Param("userId") Long userId, 
                                              @Param("reviewId") Long reviewId,
                                              @Param("likeType") Integer likeType);

    /**
     * 查询用户是否点赞回复
     */
    @Select("SELECT * FROM merchant_review_like WHERE ref_id = #{refId} AND user_id = #{userId} AND like_type = 2 LIMIT 1")
    MerchantReviewLike selectByUserAndReply(@Param("userId") Long userId, @Param("refId") Long refId);

    /**
     * 统计点赞数
     */
    @Select("SELECT COUNT(*) FROM merchant_review_like WHERE review_id = #{reviewId} AND like_type = #{likeType}")
    Integer countLikes(@Param("reviewId") Long reviewId, @Param("likeType") Integer likeType);
}
