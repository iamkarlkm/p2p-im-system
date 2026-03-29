// 商户经营仪表盘 - 实时客流数据实体
package com.im.entity.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MerchantTrafficDashboard {
    private Long id;
    private Long merchantId;
    private LocalDate statDate;
    
    // 实时客流数据
    private Integer todayVisitors;
    private Integer todayPageViews;
    private Integer todayStoreVisits;
    private Integer todayOrderCount;
    private BigDecimal todayRevenue;
    
    // 对比数据
    private Integer yesterdayVisitors;
    private Integer yesterdayPageViews;
    private Integer yesterdayStoreVisits;
    private Integer yesterdayOrderCount;
    private BigDecimal yesterdayRevenue;
    
    // 环比增长率
    private BigDecimal visitorGrowthRate;
    private BigDecimal revenueGrowthRate;
    private BigDecimal orderGrowthRate;
    
    // 时段分布
    private String hourlyDistribution; // JSON格式
    private String peakHourRange;
    
    // 客户来源
    private Integer searchSourceCount;
    private Integer recommendationSourceCount;
    private Integer directSourceCount;
    private Integer shareSourceCount;
    private Integer adSourceCount;
    
    // 转化率
    private BigDecimal viewToVisitRate;
    private BigDecimal visitToOrderRate;
    private BigDecimal overallConversionRate;
    
    // 新老客比例
    private Integer newCustomerCount;
    private Integer returningCustomerCount;
    private BigDecimal newCustomerRatio;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 扩展统计字段
    private Integer weekTotalVisitors;
    private Integer monthTotalVisitors;
    private BigDecimal weekTotalRevenue;
    private BigDecimal monthTotalRevenue;
    private Integer avgDailyVisitors;
    private BigDecimal avgOrderValue;
    private Integer avgStayMinutes;
    
    // 排名数据
    private Integer categoryRank; // 同品类排名
    private Integer districtRank; // 同商圈排名
    private Integer totalCategoryMerchants;
    private Integer totalDistrictMerchants;
    
    // 热门时段统计
    private Integer breakfastCount;
    private Integer lunchCount;
    private Integer dinnerCount;
    private Integer lateNightCount;
    
    // 营销效果
    private Integer couponUsageCount;
    private Integer activityParticipationCount;
    private BigDecimal activityRevenue;
    
    // 设备分布
    private Integer iosUserCount;
    private Integer androidUserCount;
    private Integer miniProgramUserCount;
    
    // 地域分布TOP5
    private String topDistricts; // JSON格式
    
    // 实时在线
    private Integer currentOnlineUsers;
    private Integer maxConcurrentUsers;
    private LocalDateTime maxConcurrentTime;
    
    // 7日趋势
    private String sevenDayTrend; // JSON数组
    
    // 预测数据
    private Integer predictedTomorrowVisitors;
    private BigDecimal predictedTomorrowRevenue;
    private String predictedPeakHours;
    
    // 数据质量
    private String dataQualityScore;
    private Boolean isDataComplete;
    
    // 环比上周
    private BigDecimal weekOverWeekGrowth;
    private BigDecimal monthOverMonthGrowth;
}
