package com.im.backend.modules.local_life.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local_life.entity.MerchantReview;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商户评价 Repository
 */
public interface MerchantReviewRepository extends BaseMapper<MerchantReview> {

    /**
     * 查询商户的评价列表
     */
    List<MerchantReview> selectByMerchantId(@Param("merchantId") Long merchantId,
                                            @Param("status") Integer status,
                                            @Param("offset") Integer offset,
                                            @Param("limit") Integer limit);

    /**
     * 统计商户评价数量
     */
    @Select("SELECT COUNT(*) FROM merchant_review WHERE merchant_id = #{merchantId} AND status = #{status} AND deleted = 0")
    Integer countByMerchantId(@Param("merchantId") Long merchantId, @Param("status") Integer status);

    /**
     * 查询用户的评价列表
     */
    List<MerchantReview> selectByUserId(@Param("userId") Long userId,
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

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
     * 增加浏览数
     */
    @Update("UPDATE merchant_review SET view_count = view_count + 1 WHERE id = #{reviewId}")
    int incrementViewCount(@Param("reviewId") Long reviewId);

    /**
     * 查询优质评价（推荐展示）
     */
    List<MerchantReview> selectRecommendedReviews(@Param("merchantId") Long merchantId,
                                                   @Param("limit") Integer limit);

    /**
     * 查询有图/视频评价
     */
    List<MerchantReview> selectWithMediaReviews(@Param("merchantId") Long merchantId,
                                                 @Param("hasVideo") Boolean hasVideo,
                                                 @Param("offset") Integer offset,
                                                 @Param("limit") Integer limit);
}
