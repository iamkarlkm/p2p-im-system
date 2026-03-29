package com.im.backend.modules.merchant.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.review.entity.MerchantReputationStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 商户口碑统计Mapper
 */
@Mapper
public interface MerchantReputationStatsMapper extends BaseMapper<MerchantReputationStats> {

    /**
     * 根据商户ID查询统计信息
     */
    @Select("SELECT * FROM merchant_reputation_stats WHERE merchant_id = #{merchantId}")
    MerchantReputationStats selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 更新商户评分统计
     */
    @Update({
        "UPDATE merchant_reputation_stats SET",
        "overall_score = #{overallScore},",
        "taste_score = #{tasteScore},",
        "environment_score = #{environmentScore},",
        "service_score = #{serviceScore},",
        "value_score = #{valueScore},",
        "total_reviews = #{totalReviews},",
        "five_star_count = #{fiveStarCount},",
        "four_star_count = #{fourStarCount},",
        "three_star_count = #{threeStarCount},",
        "two_star_count = #{twoStarCount},",
        "one_star_count = #{oneStarCount},",
        "positive_rate = #{positiveRate},",
        "last_updated_at = NOW()",
        "WHERE merchant_id = #{merchantId}"
    })
    int updateStats(MerchantReputationStats stats);

    /**
     * 查询同商圈同类目的商户排名
     */
    @Select("SELECT ranking_in_category FROM merchant_reputation_stats WHERE merchant_id = #{merchantId}")
    Integer selectRankingByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 查询榜单（按综合评分排序）
     */
    @Select("SELECT * FROM merchant_reputation_stats WHERE overall_score > 0 ORDER BY overall_score DESC, total_reviews DESC LIMIT #{limit}")
    List<MerchantReputationStats> selectTopByOverallScore(@Param("limit") int limit);

    /**
     * 查询热门榜单（按评价数排序）
     */
    @Select("SELECT * FROM merchant_reputation_stats WHERE total_reviews > 0 ORDER BY total_reviews DESC, overall_score DESC LIMIT #{limit}")
    List<MerchantReputationStats> selectTopByReviewCount(@Param("limit") int limit);
}
