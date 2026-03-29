package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 经营数据看板响应DTO
 */
@Data
public class BusinessDashboardResponse {

    /** 商户ID */
    private Long merchantId;

    /** 统计时段 */
    private String period;

    /** 开始日期 */
    private String startDate;

    /** 结束日期 */
    private String endDate;

    /** 核心指标概览 */
    private KeyMetricsOverview overview;

    /** 趋势数据 */
    private List<TrendData> trendData;

    /** 时段分布 */
    private List<HourlyDistribution> hourlyDistribution;

    /** 同比环比数据 */
    private ComparisonData comparison;

    /**
     * 核心指标概览
     */
    @Data
    public static class KeyMetricsOverview {
        /** 营业额 */
        private BigDecimal revenue;
        private BigDecimal revenueChange;
        private String revenueChangeType; // INCREASE/DECREASE

        /** 订单数 */
        private Integer orderCount;
        private Integer orderCountChange;
        private String orderCountChangeType;

        /** 客单价 */
        private BigDecimal avgOrderValue;
        private BigDecimal avgOrderValueChange;
        private String avgOrderValueChangeType;

        /** 访客数 */
        private Integer visitorCount;
        private Integer visitorCountChange;
        private String visitorCountChangeType;

        /** 新客数 */
        private Integer newCustomerCount;
        private Integer newCustomerCountChange;
        private String newCustomerCountChangeType;
    }

    /**
     * 趋势数据
     */
    @Data
    public static class TrendData {
        private String date;
        private BigDecimal revenue;
        private Integer orderCount;
        private Integer visitorCount;
    }

    /**
     * 时段分布
     */
    @Data
    public static class HourlyDistribution {
        private Integer hour;
        private BigDecimal revenue;
        private Integer orderCount;
        private Double percentage;
    }

    /**
     * 对比数据
     */
    @Data
    public static class ComparisonData {
        private BigDecimal revenueYoY; // 同比
        private BigDecimal revenueMoM; // 环比
        private BigDecimal orderCountYoY;
        private BigDecimal orderCountMoM;
        private BigDecimal visitorCountYoY;
        private BigDecimal visitorCountMoM;
    }
}
