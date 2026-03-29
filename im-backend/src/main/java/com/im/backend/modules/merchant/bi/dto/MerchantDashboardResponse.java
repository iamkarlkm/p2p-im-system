package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 商家经营数据看板响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDashboardResponse {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 数据日期
     */
    private LocalDate reportDate;
    
    /**
     * 实时指标
     */
    private RealtimeMetricsDTO realtime;
    
    /**
     * 今日数据
     */
    private DailyMetricsDTO today;
    
    /**
     * 昨日数据
     */
    private DailyMetricsDTO yesterday;
    
    /**
     * 本周数据
     */
    private PeriodMetricsDTO thisWeek;
    
    /**
     * 本月数据
     */
    private PeriodMetricsDTO thisMonth;
    
    /**
     * 趋势数据
     */
    private List<TrendDataDTO> trends;
    
    /**
     * 小时分布
     */
    private Map<Integer, Integer> hourlyDistribution;
    
    /**
     * 支付渠道分布
     */
    private Map<String, BigDecimal> paymentDistribution;
    
    /**
     * 排名信息
     */
    private RankingDTO ranking;
    
    /**
     * 异常预警
     */
    private List<AlertDTO> alerts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RealtimeMetricsDTO {
        private BigDecimal todayRevenue;
        private Integer todayOrders;
        private Integer currentFootTraffic;
        private Integer diningCustomers;
        private Integer waitingCustomers;
        private Integer avgWaitTime;
        private BigDecimal realtimeRating;
        private BigDecimal turnoverRate;
        private BigDecimal seatUtilization;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyMetricsDTO {
        private BigDecimal totalRevenue;
        private Integer totalOrders;
        private Integer validOrders;
        private Integer cancelledOrders;
        private BigDecimal refundAmount;
        private BigDecimal averageOrderValue;
        private Integer footTraffic;
        private Integer newCustomers;
        private Integer returningCustomers;
        private BigDecimal returningRate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PeriodMetricsDTO {
        private BigDecimal totalRevenue;
        private Integer totalOrders;
        private BigDecimal averageOrderValue;
        private Integer footTraffic;
        private Integer newCustomers;
        private BigDecimal growthRate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDataDTO {
        private LocalDate date;
        private BigDecimal revenue;
        private Integer orders;
        private Integer footTraffic;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankingDTO {
        private Integer districtRank;
        private Integer totalInDistrict;
        private Integer categoryRank;
        private Integer totalInCategory;
        private BigDecimal districtTopPercent;
        private BigDecimal categoryTopPercent;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AlertDTO {
        private String alertType;
        private String alertLevel;
        private String title;
        private String content;
    }
}
