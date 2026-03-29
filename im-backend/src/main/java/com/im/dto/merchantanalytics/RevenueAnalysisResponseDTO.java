// 营收分析响应DTO
package com.im.dto.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RevenueAnalysisResponseDTO {
    private Long merchantId;
    private LocalDate statDate;
    
    // 营收概览
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private BigDecimal avgOrderValue;
    
    // 支付渠道
    private PaymentChannelDTO paymentChannels;
    
    // 营收构成
    private RevenueCompositionDTO composition;
    
    // 毛利
    private BigDecimal grossProfit;
    private BigDecimal grossProfitRate;
    
    // 退款
    private BigDecimal refundAmount;
    private Integer refundCount;
    private BigDecimal refundRate;
    
    // 优惠
    private BigDecimal discountAmount;
    private Integer discountOrderCount;
    
    // 对比
    private BigDecimal yesterdayRevenue;
    private BigDecimal revenueGrowthRate;
    
    // 趋势
    private Object sevenDayTrend;
    private Object thirtyDayTrend;
}
