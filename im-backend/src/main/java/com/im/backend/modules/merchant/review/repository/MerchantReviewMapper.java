package com.im.backend.modules.merchant.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.modules.merchant.review.entity.MerchantReview;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 商户评价数据访问层 - 功能#310: 本地商户评价口碑
 */
@Repository
public interface MerchantReviewMapper extends BaseMapper<MerchantReview> {

    /**
     * 分页查询商户评价
     */
    IPage<MerchantReview> selectByMerchantId(Page<MerchantReview> page, 
                                              @Param("merchantId") Long merchantId,
                                              @Param("rating") Integer rating,
                                              @Param("hasImage") Boolean hasImage);

    /**
     * 查询用户对该商户的评价
     */
    @Select("SELECT * FROM merchant_review WHERE user_id = #{userId} AND merchant_id = #{merchantId} AND deleted = 0 ORDER BY create_time DESC LIMIT 1")
    MerchantReview selectByUserAndMerchant(@Param("userId") Long userId, @Param("merchantId") Long merchantId);

    /**
     * 查询用户的所有评价
     */
    IPage<MerchantReview> selectByUserId(Page<MerchantReview> page, @Param("userId") Long userId);

    /**
     * 更新点赞数
     */
    @Update("UPDATE merchant_review SET like_count = like_count + #{delta} WHERE id = #{reviewId}")
    int updateLikeCount(@Param("reviewId") Long reviewId, @Param("delta") int delta);

    /**
     * 更新回复数
     */
    @Update("UPDATE merchant_review SET reply_count = reply_count + 1 WHERE id = #{reviewId}")
    int incrementReplyCount(@Param("reviewId") Long reviewId);

    /**
     * 更新浏览数
     */
    @Update("UPDATE merchant_review SET view_count = view_count + 1 WHERE id = #{reviewId}")
    int incrementViewCount(@Param("reviewId") Long reviewId);

    /**
     * 商家回复
     */
    @Update("UPDATE merchant_review SET merchant_reply = #{reply}, merchant_reply_time = NOW() WHERE id = #{reviewId}")
    int merchantReply(@Param("reviewId") Long reviewId, @Param("reply") String reply);

    /**
     * 获取评分统计
     */
    @Select("SELECT rating, COUNT(*) as count FROM merchant_review WHERE merchant_id = #{merchantId} AND status = 1 AND deleted = 0 GROUP BY rating")
    List<java.util.Map<String, Object>> selectRatingStats(@Param("merchantId") Long merchantId);

    /**
     * 查询推荐评价
     */
    @Select("SELECT * FROM merchant_review WHERE merchant_id = #{merchantId} AND recommended = 1 AND status = 1 AND deleted = 0 ORDER BY create_time DESC LIMIT #{limit}")
    List<MerchantReview> selectRecommended(@Param("merchantId") Long merchantId, @Param("limit") Integer limit);
}
