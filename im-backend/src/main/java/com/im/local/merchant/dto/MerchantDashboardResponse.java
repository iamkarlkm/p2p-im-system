package com.im.local.merchant.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商户经营数据仪表盘响应DTO
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
public class MerchantDashboardResponse {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 商户名称
     */
    private String merchantName;
    
     /**
     * 统计周期
     */
    private String timeRangeLabel;
    
    /**
     * 统计开始日期
     */
    private LocalDate startDate;
    
    /**
     * 统计结束日期
     */
    private LocalDate endDate;
    
    /**
     * 数据生成时间
     */
    private LocalDateTime generatedAt;
    
    /**
     * 经营总览数据
     */
    private BusinessOverview overview;
    
    /**
     * 营收分析数据
     */
    private RevenueAnalysis revenue;
    
    /**
     * 客流分析数据
     */
    private TrafficAnalysis traffic;
    
    /**
     * 评价分析数据
     */
    private ReviewAnalysis reviews;
    
    /**
     * 竞品对比数据
     */
    private CompetitorAnalysis competitor;
    
    /**
     * 智能洞察建议
     */
    private List<BusinessInsight> insights;
    
    // ==================== 内部类定义 ====================
    
    /**
     * 经营总览
     */
    @Data
    @Builder
    public static class BusinessOverview {
        // 营收指标
        private BigDecimal totalRevenue;
        private BigDecimal revenueGrowth;
        private String revenueGrowthRate;
        
        // 订单指标
        private Integer totalOrders;
        private Integer orderGrowth;
        private String orderGrowthRate;
        
        // 客流指标
        private Integer totalVisitors;
        private Integer visitorGrowth;
        private String visitorGrowthRate;
        
        // 转化指标
        private String conversionRate;
        private String conversionRateChange;
        
        // 客单价
        private BigDecimal avgOrderValue;
        private BigDecimal avgOrderValueChange;
        
        // 综合评分
        private BigDecimal overallRating;
        private BigDecimal ratingChange;
        
        // 今日实时数据
        private RealTimeStats realTimeStats;
    }
    
    /**
     * 实时统计数据
     */
    @Data
    @Builder
    public static class RealTimeStats {
        private Integer todayVisitors;
        private Integer todayOrders;
        private BigDecimal todayRevenue;
        private Integer currentOnline;
        private String todayTrend; // UP, DOWN, FLAT
    }
    
    /**
     * 营收分析
     */
    @Data
    @Builder
    public static class RevenueAnalysis {
        // 营收构成
        private RevenueComposition composition;
        
        // 营收趋势
        private List<TimeSeriesData> revenueTrend;
        
        // 时段分布
        private List<HourlyDistribution> hourlyDistribution;
        
        // 支付渠道分布
        private List<PaymentChannel> paymentChannels;
        
        // 退款统计
        private RefundStats refundStats;
    }
    
    /**
     * 营收构成
     */
    @Data
    @Builder
    public static class RevenueComposition {
        private BigDecimal productRevenue;
        private BigDecimal serviceRevenue;
        private BigDecimal membershipRevenue;
        private BigDecimal otherRevenue;
    }
    
    /**
     * 时序数据点
     */
    @Data
    @Builder
    public static class TimeSeriesData {
        private String timeLabel;
        private LocalDateTime timestamp;
        private BigDecimal value;
        private BigDecimal compareValue;
        private String growthRate;
    }
    
    /**
     * 时段分布
     */
    @Data
    @Builder
    public static class HourlyDistribution {
        private Integer hour;
        private String hourLabel;
        private BigDecimal revenue;
        private Integer orders;
        private String percentage;
    }
    
    /**
     * 支付渠道
     */
    @Data
    @Builder
    public static class PaymentChannel {
        private String channelCode;
        private String channelName;
        private BigDecimal amount;
        private Integer count;
        private String percentage;
    }
    
    /**
     * 退款统计
     */
    @Data
    @Builder
    public static class RefundStats {
        private Integer refundCount;
        private BigDecimal refundAmount;
        private String refundRate;
        private Integer pendingRefunds;
    }
    
    /**
     * 客流分析
     */
    @Data
    @Builder
    public static class TrafficAnalysis {
        // 客流概览
        private Integer totalVisitors;
        private Integer newVisitors;
        private Integer returningVisitors;
        private String newVisitorRate;
        private String returnRate;
        
        // 客流趋势
        private List<TimeSeriesData> visitorTrend;
        
        // 客流来源
        private List<TrafficSource> sources;
        
        // 到店时段分布
        private List<HourlyDistribution> visitHours;
        
        // 停留时长分布
        private List<DwellTimeDistribution> dwellTime;
        
        // 地域分布
        private List<RegionDistribution> regions;
    }
    
    /**
     * 客流来源
     */
    @Data
    @Builder
    public static class TrafficSource {
        private String sourceCode;
        private String sourceName;
        private Integer visitors;
        private String percentage;
        private String conversionRate;
    }
    
    /**
     * 停留时长分布
     */
    @Data
    @Builder
    public static class DwellTimeDistribution {
        private String timeRange;
        private Integer visitors;
        private String percentage;
    }
    
    /**
     * 地域分布
     */
    @Data
    @Builder
    public static class RegionDistribution {
        private String regionCode;
        private String regionName;
        private Integer visitors;
        private String percentage;
    }
    
    /**
     * 评价分析
     */
    @Data
    @Builder
    public static class ReviewAnalysis {
        // 评分概览
        private BigDecimal avgRating;
        private Integer totalReviews;
        private Integer newReviews;
        private String reviewGrowthRate;
        
        // 评分分布
        private List<RatingDistribution> ratingDistribution;
        
        // 评价趋势
        private List<TimeSeriesData> reviewTrend;
        
        // 维度评分
        private List<DimensionRating> dimensionRatings;
        
        // 关键词云
        private List<KeywordTag> keywords;
        
        // 情感分析
        private SentimentAnalysis sentiment;
        
        // 最新评价
        private List<RecentReview> recentReviews;
    }
    
    /**
     * 评分分布
     */
    @Data
    @Builder
    public static class RatingDistribution {
        private Integer rating;
        private Integer count;
        private String percentage;
    }
    
    /**
     * 维度评分
     */
    @Data
    @Builder
    public static class DimensionRating {
        private String dimensionCode;
        private String dimensionName;
        private BigDecimal score;
        private BigDecimal scoreChange;
    }
    
    /**
     * 关键词标签
     */
    @Data
    @Builder
    public static class KeywordTag {
        private String keyword;
        private Integer count;
        private String sentiment; // POSITIVE, NEGATIVE, NEUTRAL
        private Integer fontSize;
    }
    
    /**
     * 情感分析
     */
    @Data
    @Builder
    public static class SentimentAnalysis {
        private String positiveRate;
        private String negativeRate;
        private String neutralRate;
        private Integer positiveCount;
        private Integer negativeCount;
        private Integer neutralCount;
    }
    
    /**
     * 最新评价
     */
    @Data
    @Builder
    public static class RecentReview {
        private Long reviewId;
        private String userName;
        private String userAvatar;
        private Integer rating;
        private String content;
        private List<String> images;
        private LocalDateTime createTime;
        private String sentiment;
    }
    
    /**
     * 竞品对比
     */
    @Data
    @Builder
    public static class CompetitorAnalysis {
        // 商户排名
        private Integer ranking;
        private Integer totalMerchants;
        private String rankingChange;
        
        // 对比指标
        private List<ComparisonMetric> comparisonMetrics;
        
        // 商圈平均数据
        private DistrictAverage districtAverage;
        
        // Top商户
        private List<TopMerchant> topMerchants;
    }
    
    /**
     * 对比指标
     */
    @Data
    @Builder
    public static class ComparisonMetric {
        private String metricCode;
        private String metricName;
        private String myValue;
        private String avgValue;
        private String topValue;
        private String myPercentile;
    }
    
    /**
     * 商圈平均
     */
    @Data
    @Builder
    public static class DistrictAverage {
        private BigDecimal avgRevenue;
        private Integer avgOrders;
        private Integer avgVisitors;
        private BigDecimal avgRating;
        private String avgConversionRate;
    }
    
    /**
     * Top商户
     */
    @Data
    @Builder
    public static class TopMerchant {
        private Integer rank;
        private Long merchantId;
        private String merchantName;
        private String merchantLogo;
        private BigDecimal score;
    }
    
    /**
     * 经营洞察
     */
    @Data
    @Builder
    public static class BusinessInsight {
        private String insightId;
        private String type; // ALERT, OPPORTUNITY, SUGGESTION, TREND
        private String level; // HIGH, MEDIUM, LOW
        private String title;
        private String description;
        private String metric;
        private String metricValue;
        private String recommendation;
        private LocalDateTime generatedAt;
    }
}
