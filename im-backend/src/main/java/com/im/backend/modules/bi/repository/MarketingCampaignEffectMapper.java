package com.im.backend.modules.bi.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.bi.entity.MarketingCampaignEffect;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 营销活动效果数据访问层
 */
@Mapper
public interface MarketingCampaignEffectMapper extends BaseMapper<MarketingCampaignEffect> {

    /**
     * 查询日期范围内的活动效果
     */
    @Select("SELECT * FROM marketing_campaign_effect WHERE merchant_id = #{merchantId} " +
            "AND stat_date BETWEEN #{startDate} AND #{endDate} ORDER BY stat_date DESC")
    List<MarketingCampaignEffect> selectByDateRange(@Param("merchantId") Long merchantId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 查询活动总计数据
     */
    @Select("SELECT SUM(expose_count) as totalExpose, SUM(claim_count) as totalClaim, " +
            "SUM(used_count) as totalUsed, SUM(convert_amount) as totalConvert " +
            "FROM marketing_campaign_effect WHERE merchant_id = #{merchantId}")
    List<java.util.Map<String, Object>> selectCampaignSummary(@Param("merchantId") Long merchantId);

    /**
     * 按活动类型统计效果
     */
    @Select("SELECT campaign_type, SUM(expose_count) as exposeCount, " +
            "SUM(claim_count) as claimCount, SUM(used_count) as usedCount " +
            "FROM marketing_campaign_effect WHERE merchant_id = #{merchantId} " +
            "GROUP BY campaign_type")
    List<java.util.Map<String, Object>> selectEffectByType(@Param("merchantId") Long merchantId);
}
