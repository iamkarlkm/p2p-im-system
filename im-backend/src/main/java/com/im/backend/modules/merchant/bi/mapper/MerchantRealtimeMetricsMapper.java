package com.im.backend.modules.merchant.bi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.im.backend.modules.merchant.bi.entity.MerchantRealtimeMetrics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;

/**
 * 商家实时经营指标Mapper
 */
@Mapper
public interface MerchantRealtimeMetricsMapper extends BaseMapper<MerchantRealtimeMetrics> {
    
    /**
     * 更新实时营业额
     */
    @Update("UPDATE merchant_realtime_metrics SET today_revenue = today_revenue + #{amount}, today_orders = today_orders + 1, update_time = NOW() WHERE merchant_id = #{merchantId}")
    int incrementRevenue(@Param("merchantId") Long merchantId, @Param("amount") BigDecimal amount);
    
    /**
     * 更新客流量
     */
    @Update("UPDATE merchant_realtime_metrics SET current_foot_traffic = #{footTraffic}, update_time = NOW() WHERE merchant_id = #{merchantId}")
    int updateFootTraffic(@Param("merchantId") Long merchantId, @Param("footTraffic") Integer footTraffic);
    
    /**
     * 重置每日数据
     */
    @Update("UPDATE merchant_realtime_metrics SET today_revenue = 0, today_orders = 0, today_new_customers = 0, today_returning_customers = 0, today_reviews = 0, update_time = NOW() WHERE merchant_id = #{merchantId}")
    int resetDailyMetrics(@Param("merchantId") Long merchantId);
}
