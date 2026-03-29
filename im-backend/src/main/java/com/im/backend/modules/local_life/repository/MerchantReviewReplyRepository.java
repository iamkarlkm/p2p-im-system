package com.im.backend.modules.local_life.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local_life.entity.MerchantReviewReply;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 评价回复 Repository
 */
public interface MerchantReviewReplyRepository extends BaseMapper<MerchantReviewReply> {

    /**
     * 查询评价的所有回复
     */
    List<MerchantReviewReply> selectByReviewId(@Param("reviewId") Long reviewId);

    /**
     * 查询一级回复（不包含楼中楼）
     */
    List<MerchantReviewReply> selectTopLevelReplies(@Param("reviewId") Long reviewId);

    /**
     * 查询子回复（楼中楼）
     */
    List<MerchantReviewReply> selectChildReplies(@Param("parentId") Long parentId);

    /**
     * 统计评价回复数
     */
    @Select("SELECT COUNT(*) FROM merchant_review_reply WHERE review_id = #{reviewId} AND status = 1 AND deleted = 0")
    Integer countByReviewId(@Param("reviewId") Long reviewId);

    /**
     * 查询商家的回复
     */
    List<MerchantReviewReply> selectMerchantReplies(@Param("merchantId") Long merchantId,
                                                     @Param("offset") Integer offset,
                                                     @Param("limit") Integer limit);
}
