package com.im.backend.modules.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 经营数据看板响应DTO
 */
@Data
public class BusinessDashboardResponse {

    /** 今日营业额 */
    private BigDecimal todayRevenue;

    /** 今日订单数 */
    private Integer todayOrderCount;

    /** 今日客流 */
    private Integer todayCustomerCount;

    /** 今日客单价 */
    private BigDecimal todayAvgOrderValue;

    /** 营业额环比 */
    private BigDecimal revenueMom;

    /** 订单环比 */
    private BigDecimal orderMom;

    /** 客流环比 */
    private BigDecimal customerMom;

    /** 近7天趋势 */
    private List<DailyTrend> weeklyTrend;

    /** 近30天趋势 */
    private List<DailyTrend> monthlyTrend;

    /** 时段分布 */
    private List<HourlyDistribution> hourlyDistribution;

    @Data
    public static class DailyTrend {
        private String date;
        private BigDecimal revenue;
        private Integer orderCount;
    }

    @Data
    public static class HourlyDistribution {
        private Integer hour;
        private BigDecimal revenue;
        private Integer orderCount;
    }
}
