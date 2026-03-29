package com.im.local.review.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.local.review.entity.MerchantReputationStats;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * 商户口碑统计数据访问层
 */
@Mapper
public interface MerchantReputationStatsMapper extends BaseMapper<MerchantReputationStats> {

    /**
     * 根据商户ID查询
     */
    @Select("SELECT * FROM merchant_reputation_stats WHERE merchant_id = #{merchantId}")
    MerchantReputationStats selectByMerchantId(@Param("merchantId") Long merchantId);

    /**
     * 更新统计信息
     */
    @Update("UPDATE merchant_reputation_stats SET " +
            "overall_score = #{overallScore}, " +
            "total_reviews = #{totalReviews}, " +
            "positive_rate = #{positiveRate}, " +
            "stats_updated_at = NOW() " +
            "WHERE merchant_id = #{merchantId}")
    int updateStats(@Param("merchantId") Long merchantId,
                    @Param("overallScore") java.math.BigDecimal overallScore,
                    @Param("totalReviews") Integer totalReviews,
                    @Param("positiveRate") java.math.BigDecimal positiveRate);
}
