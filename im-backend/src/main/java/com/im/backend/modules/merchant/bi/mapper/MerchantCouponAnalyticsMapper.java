package com.im.backend.modules.merchant.bi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantCouponAnalytics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 商家优惠券效果分析Mapper
 */
@Mapper
public interface MerchantCouponAnalyticsMapper extends BaseMapper<MerchantCouponAnalytics> {
    
    /**
     * 查询优惠券汇总数据
     */
    @Select("SELECT * FROM merchant_coupon_analytics WHERE merchant_id = #{merchantId} AND report_date BETWEEN #{startDate} AND #{endDate} AND deleted = 0 ORDER BY report_date DESC")
    List<MerchantCouponAnalytics> selectByDateRange(@Param("merchantId") Long merchantId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
