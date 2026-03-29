package com.im.backend.modules.merchant.bi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantCustomerGeoDistribution;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家顾客地域分布Mapper
 */
@Mapper
public interface MerchantCustomerGeoDistributionMapper extends BaseMapper<MerchantCustomerGeoDistribution> {
    
    /**
     * 查询指定区域级别的分布
     */
    @Select("SELECT * FROM merchant_customer_geo_distribution WHERE merchant_id = #{merchantId} AND report_date = #{reportDate} AND region_type = #{regionType} AND deleted = 0 ORDER BY customer_count DESC")
    List<MerchantCustomerGeoDistribution> selectByRegionType(@Param("merchantId") Long merchantId, @Param("reportDate") LocalDate reportDate, @Param("regionType") String regionType);
}
