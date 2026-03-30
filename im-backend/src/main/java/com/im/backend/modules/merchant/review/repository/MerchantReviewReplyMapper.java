package com.im.backend.modules.merchant.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.review.entity.MerchantReviewReply;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商户评价回复数据访问层 - 功能#310: 本地商户评价口碑
 */
@Repository
public interface MerchantReviewReplyMapper extends BaseMapper<MerchantReviewReply> {

    /**
     * 查询评价的所有回复
     */
    @Select("SELECT * FROM merchant_review_reply WHERE review_id = #{reviewId} AND status = 1 AND deleted = 0 ORDER BY create_time ASC")
    List<MerchantReviewReply> selectByReviewId(@Param("reviewId") Long reviewId);

    /**
     * 查询回复数
     */
    @Select("SELECT COUNT(*) FROM merchant_review_reply WHERE review_id = #{reviewId} AND status = 1 AND deleted = 0")
    Integer countByReviewId(@Param("reviewId") Long reviewId);

    /**
     * 更新点赞数
     */
    @Update("UPDATE merchant_review_reply SET like_count = like_count + #{delta} WHERE id = #{replyId}")
    int updateLikeCount(@Param("replyId") Long replyId, @Param("delta") int delta);
}
