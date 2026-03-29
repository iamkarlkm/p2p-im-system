package com.im.backend.modules.merchant.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.review.entity.MerchantReviewReply;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 评价回复Mapper
 */
@Mapper
public interface MerchantReviewReplyMapper extends BaseMapper<MerchantReviewReply> {

    /**
     * 根据评价ID查询回复列表
     */
    @Select("SELECT * FROM merchant_review_reply WHERE review_id = #{reviewId} AND status = 1 ORDER BY created_at ASC")
    List<MerchantReviewReply> selectByReviewId(@Param("reviewId") String reviewId);

    /**
     * 查询评价的回复数量
     */
    @Select("SELECT COUNT(*) FROM merchant_review_reply WHERE review_id = #{reviewId} AND status = 1")
    Integer countByReviewId(@Param("reviewId") String reviewId);

    /**
     * 更新点赞数
     */
    @Update("UPDATE merchant_review_reply SET like_count = like_count + #{delta} WHERE reply_id = #{replyId}")
    int updateLikeCount(@Param("replyId") String replyId, @Param("delta") int delta);

    /**
     * 根据回复ID查询
     */
    @Select("SELECT * FROM merchant_review_reply WHERE reply_id = #{replyId}")
    MerchantReviewReply selectByReplyId(@Param("replyId") String replyId);
}
