// 商户营收分析实体
package com.im.entity.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MerchantRevenueAnalysis {
    private Long id;
    private Long merchantId;
    private LocalDate statDate;
    
    // 营收概览
    private BigDecimal totalRevenue;
    private Integer totalOrders;
    private BigDecimal avgOrderValue;
    private BigDecimal avgCustomerValue;
    
    // 支付渠道分布
    private BigDecimal wechatPayAmount;
    private BigDecimal alipayAmount;
    private BigDecimal unionPayAmount;
    private BigDecimal cashAmount;
    private BigDecimal memberCardAmount;
    private BigDecimal otherPayAmount;
    
    private Integer wechatPayCount;
    private Integer alipayCount;
    private Integer unionPayCount;
    private Integer cashCount;
    private Integer memberCardCount;
    private Integer otherPayCount;
    
    // 营收构成
    private BigDecimal productRevenue;
    private BigDecimal serviceRevenue;
    private BigDecimal deliveryRevenue;
    private BigDecimal bookingRevenue;
    private BigDecimal otherRevenue;
    
    // 退款数据
    private BigDecimal refundAmount;
    private Integer refundCount;
    private BigDecimal refundRate;
    
    // 优惠数据
    private BigDecimal discountAmount;
    private BigDecimal couponDiscountAmount;
    private BigDecimal activityDiscountAmount;
    private BigDecimal memberDiscountAmount;
    private Integer discountOrderCount;
    
    // 毛利分析
    private BigDecimal grossProfit;
    private BigDecimal grossProfitRate;
    private BigDecimal costAmount;
    
    // 时段营收
    private String hourlyRevenue; // JSON
    private BigDecimal peakHourRevenue;
    private BigDecimal offPeakRevenue;
    
    // 对比数据
    private BigDecimal yesterdayRevenue;
    private BigDecimal lastWeekRevenue;
    private BigDecimal lastMonthRevenue;
    private BigDecimal revenueGrowthRate;
    
    // 趋势数据
    private String sevenDayRevenueTrend; // JSON
    private String thirtyDayRevenueTrend; // JSON
    
    // 品类贡献
    private String categoryRevenue; // JSON - 各品类营收占比
    
    // 复购分析
    private BigDecimal repeatPurchaseRate;
    private BigDecimal repeatCustomerRevenueRatio;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 扩展字段
    private Integer newCustomerOrders;
    private Integer returningCustomerOrders;
    private BigDecimal newCustomerRevenue;
    private BigDecimal returningCustomerRevenue;
    
    // 客单价分段
    private Integer lowValueOrders; // <50
    private Integer midValueOrders; // 50-200
    private Integer highValueOrders; // >200
    
    // 营业时长
    private Integer businessHours;
    private BigDecimal revenuePerHour;
    
    // 目标完成率
    private BigDecimal dailyTarget;
    private BigDecimal targetCompletionRate;
    private BigDecimal monthlyTarget;
    private BigDecimal monthlyCompletionRate;
    
    // 人均消费趋势
    private String avgOrderValueTrend;
    
    // 退款原因分析
    private String refundReasons; // JSON
    
    // 实收金额
    private BigDecimal netRevenue;
    
    // 平台服务费
    private BigDecimal platformFee;
    private BigDecimal platformFeeRate;
    
    // 结算金额
    private BigDecimal settlementAmount;
    private LocalDate settlementDate;
}
