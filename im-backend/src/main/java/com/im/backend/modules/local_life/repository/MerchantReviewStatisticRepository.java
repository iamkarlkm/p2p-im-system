package com.im.backend.modules.local_life.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.local_life.entity.MerchantReviewStatistic;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 商户评价统计 Repository
 */
public interface MerchantReviewStatisticRepository extends BaseMapper<MerchantReviewStatistic> {

    /**
     * 根据商户ID查询统计
     */
    @Select("SELECT * FROM merchant_review_statistic WHERE merchant_id = #{merchantId} LIMIT 1")
    MerchantReviewStatistic selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 更新综合评分
     */
    @Update("UPDATE merchant_review_statistic SET overall_rating = #{rating}, update_time = NOW() WHERE merchant_id = #{merchantId}")
    int updateOverallRating(@Param("merchantId") Long merchantId, @Param("rating") BigDecimal rating);

    /**
     * 增加评价计数
     */
    @Update("UPDATE merchant_review_statistic SET " +
            "total_count = total_count + 1, " +
            "latest_review_time = NOW(), " +
            "daily_new_count = daily_new_count + 1, " +
            "weekly_new_count = weekly_new_count + 1, " +
            "monthly_new_count = monthly_new_count + 1, " +
            "update_time = NOW() " +
            "WHERE merchant_id = #{merchantId}")
    int incrementCount(@Param("merchantId") Long merchantId);

    /**
     * 增加有图评价计数
     */
    @Update("UPDATE merchant_review_statistic SET with_image_count = with_image_count + 1 WHERE merchant_id = #{merchantId}")
    int incrementImageCount(@Param("merchantId") Long merchantId);

    /**
     * 增加视频评价计数
     */
    @Update("UPDATE merchant_review_statistic SET with_video_count = with_video_count + 1 WHERE merchant_id = #{merchantId}")
    int incrementVideoCount(@Param("merchantId") Long merchantId);
}
