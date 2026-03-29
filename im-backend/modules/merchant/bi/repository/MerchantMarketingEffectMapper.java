package com.im.backend.modules.merchant.bi.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantMarketingEffect;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 营销效果追踪数据访问层
 */
public interface MerchantMarketingEffectMapper extends BaseMapper<MerchantMarketingEffect> {

    /**
     * 查询营销效果列表
     */
    @Select("SELECT * FROM merchant_marketing_effect " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY stats_date DESC, marketing_id")
    List<MerchantMarketingEffect> selectByDateRange(@Param("merchantId") Long merchantId,
                                                     @Param("startDate") LocalDate startDate,
                                                     @Param("endDate") LocalDate endDate);

    /**
     * 查询指定类型营销效果
     */
    @Select("SELECT * FROM merchant_marketing_effect " +
            "WHERE merchant_id = #{merchantId} " +
            "AND marketing_type = #{marketingType} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY stats_date DESC")
    List<MerchantMarketingEffect> selectByTypeAndDateRange(@Param("merchantId") Long merchantId,
                                                            @Param("marketingType") String marketingType,
                                                            @Param("startDate") LocalDate startDate,
                                                            @Param("endDate") LocalDate endDate);

    /**
     * 查询营销概览汇总
     */
    @Select("SELECT " +
            "SUM(exposure_count) as total_exposure, " +
            "SUM(receive_count) as total_receive, " +
            "SUM(use_count) as total_use, " +
            "SUM(marketing_cost) as total_cost, " +
            "SUM(marketing_revenue) as total_revenue, " +
            "SUM(new_user_count) as total_new_users " +
            "FROM merchant_marketing_effect " +
            "WHERE merchant_id = #{merchantId} " +
            "AND stats_date BETWEEN #{startDate} AND #{endDate}")
    List<java.util.Map<String, Object>> selectMarketingOverview(@Param("merchantId") Long merchantId,
                                                                 @Param("startDate") LocalDate startDate,
                                                                 @Param("endDate") LocalDate endDate);
}
