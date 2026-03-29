package com.im.backend.modules.delivery.dto;

import lombok.Data;

/**
 * 骑手今日统计响应
 */
@Data
public class RiderTodayStatsResponse {
    private Long riderId;
    private Integer orderCount;
    private Integer completedCount;
    private Integer cancelCount;
    private Double totalIncome;
    private Double totalDistance;
    private Integer onlineMinutes;
}
