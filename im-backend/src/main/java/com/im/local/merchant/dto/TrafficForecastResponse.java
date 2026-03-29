package com.im.local.merchant.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * 客流预测响应DTO
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
public class TrafficForecastResponse {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 预测天数
     */
    private Integer forecastDays;
    
    /**
     * 数据生成时间
     */
    private java.time.LocalDateTime generatedAt;
    
    /**
     * 每日预测列表
     */
    private List<DailyForecast> forecasts;
    
    // ==================== 内部类 ====================
    
    @lombok.Data
    @lombok.Builder
    public static class DailyForecast {
        /**
         * 日期
         */
        private LocalDate date;
        
        /**
         * 星期
         */
        private String dayOfWeek;
        
        /**
         * 预测客流量
         */
        private Integer predictedVisitors;
        
        /**
         * 置信度
         */
        private String confidenceLevel;
        
        /**
         * 高峰开始时段
         */
        private Integer peakHourStart;
        
        /**
         * 高峰结束时段
         */
        private Integer peakHourEnd;
        
        /**
         * 天气因素
         */
        private String weatherFactor;
    }
}
