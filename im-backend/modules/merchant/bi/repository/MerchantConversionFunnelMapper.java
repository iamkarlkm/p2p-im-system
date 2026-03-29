package com.im.backend.modules.merchant.bi.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantConversionFunnel;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 转化漏斗数据访问层
 */
public interface MerchantConversionFunnelMapper extends BaseMapper<MerchantConversionFunnel> {

    /**
     * 查询转化漏斗
     */
    @Select("SELECT * FROM merchant_conversion_funnel " +
            "WHERE merchant_id = #{merchantId} " +
            "AND funnel_type = #{funnelType} " +
            "AND stats_date = #{statsDate} " +
            "ORDER BY step_order")
    List<MerchantConversionFunnel> selectByTypeAndDate(@Param("merchantId") Long merchantId,
                                                        @Param("funnelType") String funnelType,
                                                        @Param("statsDate") String statsDate);

    /**
     * 查询最新转化漏斗
     */
    @Select("SELECT * FROM merchant_conversion_funnel " +
            "WHERE merchant_id = #{merchantId} " +
            "AND funnel_type = #{funnelType} " +
            "ORDER BY stats_date DESC " +
            "LIMIT 1")
    MerchantConversionFunnel selectLatestByType(@Param("merchantId") Long merchantId,
                                                 @Param("funnelType") String funnelType);
}
