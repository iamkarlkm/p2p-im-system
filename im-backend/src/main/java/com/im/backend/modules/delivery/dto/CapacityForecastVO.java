package com.im.backend.modules.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 运力预测VO
 */
@Data
public class CapacityForecastVO {

    private Long stationId;
    private String stationName;
    private String forecastDate;

    /** 时段预测列表 */
    private List<TimeSlotForecast> timeSlots;

    /** 建议骑手配置 */
    private Integer suggestedTotalRiders;
    private Integer currentRiders;
    private Integer needToRecruit;

    @Data
    public static class TimeSlotForecast {
        private String timeSlot;
        private Integer predictedOrders;
        private Integer suggestedRiders;
        private BigDecimal confidence;
        private String weather;
        private Boolean isPeak;
    }
}
