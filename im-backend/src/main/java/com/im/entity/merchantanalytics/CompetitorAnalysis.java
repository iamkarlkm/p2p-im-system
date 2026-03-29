// 竞品分析实体
package com.im.entity.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CompetitorAnalysis {
    private Long id;
    private Long merchantId;
    private Long competitorId;
    private String competitorName;
    private LocalDate statDate;
    
    // 基础信息
    private String competitorCategory;
    private String competitorDistrict;
    private BigDecimal distanceKm;
    
    // 评分对比
    private BigDecimal myRating;
    private BigDecimal competitorRating;
    private BigDecimal ratingGap;
    
    // 评价数量对比
    private Integer myReviewCount;
    private Integer competitorReviewCount;
    private Integer reviewCountGap;
    
    // 人均消费对比
    private BigDecimal myAvgPrice;
    private BigDecimal competitorAvgPrice;
    private BigDecimal priceGap;
    private BigDecimal priceAdvantage; // 价格优势百分比
    
    // 热度对比
    private Integer myPopularityScore;
    private Integer competitorPopularityScore;
    private Integer popularityGap;
    
    // 排名对比
    private Integer myCategoryRank;
    private Integer competitorCategoryRank;
    private Integer rankGap;
    
    // 流量对比
    private Integer myMonthlyVisitors;
    private Integer competitorMonthlyVisitors;
    private BigDecimal visitorShareRatio; // 流量份额比
    
    // 优势劣势分析
    private String myAdvantages; // JSON
    private String myDisadvantages; // JSON
    private String competitorAdvantages; // JSON
    private String competitorDisadvantages; // JSON
    
    // 趋势对比
    private String myTrendDirection; // UP, DOWN, STABLE
    private String competitorTrendDirection;
    private BigDecimal myGrowthRate;
    private BigDecimal competitorGrowthRate;
    
    // 功能对比
    private String featureComparison; // JSON - 各项功能对比
    
    // 营销活动
    private String myOngoingActivities; // JSON
    private String competitorOngoingActivities; // JSON
    private String activityEffectiveness; // 活动效果对比
    
    // 顾客重叠度
    private Integer overlappingCustomers;
    private BigDecimal overlapRate;
    private String overlappingCustomerProfile; // 重叠顾客画像
    
    // 市场份额
    private BigDecimal myMarketShare;
    private BigDecimal competitorMarketShare;
    private Integer totalCompetitorsInArea;
    
    // 策略建议
    private String recommendedStrategy;
    private String priorityActions;
    private BigDecimal estimatedImpact;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 细分对比
    private String tasteComparison; // 口味对比
    private String serviceComparison; // 服务对比
    private String environmentComparison; // 环境对比
    private String valueComparison; // 性价比对比
    
    // 时段热度对比
    private String hourlyTrafficComparison; // JSON
    private String peakHourComparison;
    
    // 客群对比
    private String customerDemographicComparison; // JSON
    
    // 近期动态
    private String recentUpdates; // JSON - 竞品近期动态
    private String alertLevel; // HIGH, MEDIUM, LOW
}
