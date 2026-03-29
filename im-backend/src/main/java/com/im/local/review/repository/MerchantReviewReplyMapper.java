package com.im.local.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.local.review.entity.MerchantReviewReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评价回复数据访问层
 */
@Mapper
public interface MerchantReviewReplyMapper extends BaseMapper<MerchantReviewReply> {

    /**
     * 查询评价的回复列表
     */
    @Select("SELECT * FROM merchant_review_reply WHERE review_id = #{reviewId} AND status = 1 AND deleted = 0 ORDER BY created_at")
    List<MerchantReviewReply> selectByReviewId(@Param("reviewId") Long reviewId);

    /**
     * 查询商户的最近回复
     */
    List<MerchantReviewReply> selectRecentByMerchantId(@Param("merchantId") Long merchantId,
                                                       @Param("limit") Integer limit);
}
