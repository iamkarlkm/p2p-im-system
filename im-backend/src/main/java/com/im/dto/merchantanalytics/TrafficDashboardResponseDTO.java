// 经营仪表盘响应DTO
package com.im.dto.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TrafficDashboardResponseDTO {
    private Long merchantId;
    private LocalDate statDate;
    
    // 今日数据
    private Integer todayVisitors;
    private Integer todayPageViews;
    private Integer todayStoreVisits;
    private Integer todayOrderCount;
    private BigDecimal todayRevenue;
    
    // 增长率
    private BigDecimal visitorGrowthRate;
    private BigDecimal revenueGrowthRate;
    private BigDecimal orderGrowthRate;
    
    // 转化率
    private BigDecimal viewToVisitRate;
    private BigDecimal visitToOrderRate;
    private BigDecimal overallConversionRate;
    
    // 新老客
    private Integer newCustomerCount;
    private Integer returningCustomerCount;
    private BigDecimal newCustomerRatio;
    
    // 排名
    private Integer categoryRank;
    private Integer districtRank;
    private Integer totalCategoryMerchants;
    private Integer totalDistrictMerchants;
    
    // 实时数据
    private Integer currentOnlineUsers;
    private String peakHourRange;
    
    // 来源分布
    private CustomerSourceDTO customerSource;
    
    // 7日趋势
    private Object sevenDayTrend;
}
