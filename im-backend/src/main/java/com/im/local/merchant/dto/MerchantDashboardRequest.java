package com.im.local.merchant.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 商户经营数据仪表盘请求DTO
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
public class MerchantDashboardRequest {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 统计时间范围类型
     * TODAY-今日, YESTERDAY-昨日, WEEK-本周, MONTH-本月, YEAR-本年, CUSTOM-自定义
     */
    private String timeRangeType;
    
    /**
     * 自定义开始日期
     */
    private LocalDate startDate;
    
    /**
     * 自定义结束日期
     */
    private LocalDate endDate;
    
    /**
     * 数据维度
     * OVERVIEW-总览, REVENUE-营收, TRAFFIC-客流, REVIEW-评价, COMPETE-竞品对比
     */
    private List<String> dataDimensions;
    
    /**
     * 对比类型
     * NONE-无对比, PREVIOUS_PERIOD-环比, SAME_PERIOD_LAST_YEAR-同比
     */
    private String compareType;
    
    /**
     * 是否包含趋势数据
     */
    private Boolean includeTrend;
    
    /**
     * 趋势粒度
     * HOUR-小时, DAY-天, WEEK-周, MONTH-月
     */
    private String trendGranularity;
    
    /**
     * 商圈ID（用于竞品对比）
     */
    private Long businessDistrictId;
    
    /**
     * 对比商户分类
     */
    private String compareCategory;
    
    // ==================== 构造函数 ====================
    
    public MerchantDashboardRequest() {}
    
    public MerchantDashboardRequest(Long merchantId, String timeRangeType) {
        this.merchantId = merchantId;
        this.timeRangeType = timeRangeType;
    }
    
    // ==================== 业务方法 ====================
    
    /**
     * 计算实际日期范围
     */
    public void calculateDateRange() {
        LocalDate today = LocalDate.now();
        switch (timeRangeType) {
            case "TODAY":
                this.startDate = today;
                this.endDate = today;
                break;
            case "YESTERDAY":
                this.startDate = today.minusDays(1);
                this.endDate = today.minusDays(1);
                break;
            case "WEEK":
                this.startDate = today.minusDays(today.getDayOfWeek().getValue() - 1);
                this.endDate = today;
                break;
            case "MONTH":
                this.startDate = today.withDayOfMonth(1);
                this.endDate = today;
                break;
            case "YEAR":
                this.startDate = today.withDayOfYear(1);
                this.endDate = today;
                break;
            default:
                break;
        }
    }
    
    /**
     * 是否需要对比数据
     */
    public boolean needCompare() {
        return compareType != null && !"NONE".equals(compareType);
    }
    
    /**
     * 获取对比周期日期范围
     */
    public Map<String, LocalDate> getCompareDateRange() {
        if (!needCompare()) return null;
        
        LocalDate compareStart, compareEnd;
        long days = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate);
        
        switch (compareType) {
            case "PREVIOUS_PERIOD":
                compareEnd = startDate.minusDays(1);
                compareStart = compareEnd.minusDays(days);
                break;
            case "SAME_PERIOD_LAST_YEAR":
                compareStart = startDate.minusYears(1);
                compareEnd = endDate.minusYears(1);
                break;
            default:
                return null;
        }
        
        return Map.of("startDate", compareStart, "endDate", compareEnd);
    }
    
    // ==================== 常量定义 ====================
    
    public static final String TIME_RANGE_TODAY = "TODAY";
    public static final String TIME_RANGE_YESTERDAY = "YESTERDAY";
    public static final String TIME_RANGE_WEEK = "WEEK";
    public static final String TIME_RANGE_MONTH = "MONTH";
    public static final String TIME_RANGE_YEAR = "YEAR";
    public static final String TIME_RANGE_CUSTOM = "CUSTOM";
    
    public static final String COMPARE_NONE = "NONE";
    public static final String COMPARE_PREVIOUS_PERIOD = "PREVIOUS_PERIOD";
    public static final String COMPARE_SAME_PERIOD_LAST_YEAR = "SAME_PERIOD_LAST_YEAR";
    
    public static final String DIMENSION_OVERVIEW = "OVERVIEW";
    public static final String DIMENSION_REVENUE = "REVENUE";
    public static final String DIMENSION_TRAFFIC = "TRAFFIC";
    public static final String DIMENSION_REVIEW = "REVIEW";
    public static final String DIMENSION_COMPETE = "COMPETE";
    
    public static final String GRANULARITY_HOUR = "HOUR";
    public static final String GRANULARITY_DAY = "DAY";
    public static final String GRANULARITY_WEEK = "WEEK";
    public static final String GRANULARITY_MONTH = "MONTH";
}
