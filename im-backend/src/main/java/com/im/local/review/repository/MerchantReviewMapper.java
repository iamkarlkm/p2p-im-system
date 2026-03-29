package com.im.local.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.local.review.entity.MerchantReview;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商户评价数据访问层
 */
@Mapper
public interface MerchantReviewMapper extends BaseMapper<MerchantReview> {

    /**
     * 查询商户评价列表
     */
    List<MerchantReview> selectByMerchantId(@Param("merchantId") Long merchantId,
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
    @Update("UPDATE merchant_review SET like_count = like_count - 1 WHERE id = #{reviewId} AND like_count > 0")
    int decrementLikeCount(@Param("reviewId") Long reviewId);

    /**
     * 增加回复数
     */
    @Update("UPDATE merchant_review SET reply_count = reply_count + 1 WHERE id = #{reviewId}")
    int incrementReplyCount(@Param("reviewId") Long reviewId);

    /**
     * 获取商户评价总数
     */
    @Select("SELECT COUNT(*) FROM merchant_review WHERE merchant_id = #{merchantId} AND status = 1 AND deleted = 0")
    int countByMerchantId(@Param("merchantId") Long merchantId);
}
