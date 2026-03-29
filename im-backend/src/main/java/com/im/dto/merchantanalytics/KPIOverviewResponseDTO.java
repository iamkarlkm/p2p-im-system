// KPI概览响应DTO
package com.im.dto.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class KPIOverviewResponseDTO {
    // 营收指标
    private BigDecimal todayRevenue;
    private BigDecimal revenueGrowth;
    private BigDecimal yesterdayRevenue;
    
    // 订单指标
    private Integer todayOrders;
    private BigDecimal orderGrowth;
    private Integer yesterdayOrders;
    
    // 访客指标
    private Integer todayVisitors;
    private BigDecimal visitorGrowth;
    
    // 转化指标
    private BigDecimal conversionRate;
    private BigDecimal viewToVisitRate;
    private BigDecimal visitToOrderRate;
    
    // 客单价
    private BigDecimal avgOrderValue;
    private BigDecimal avgOrderValueGrowth;
    
    // 评价指标
    private BigDecimal customerRating;
    private BigDecimal ratingChange;
    private Integer newReviewsToday;
    private BigDecimal replyRate;
    
    // 排名
    private Integer categoryRank;
    private Integer districtRank;
    
    // 实时
    private Integer currentOnlineUsers;
    
    // 对比周/月
    private BigDecimal weekGrowthRate;
    private BigDecimal monthGrowthRate;
}
