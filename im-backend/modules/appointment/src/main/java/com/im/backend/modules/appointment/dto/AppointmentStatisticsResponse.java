package com.im.backend.modules.appointment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 预约统计响应
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class AppointmentStatisticsResponse {

    /**
     * 总预约数
     */
    private Integer totalAppointments;

    /**
     * 已完成数
     */
    private Integer completedCount;

    /**
     * 已取消数
     */
    private Integer cancelledCount;

    /**
     * 爽约数
     */
    private Integer noShowCount;

    /**
     * 待处理数
     */
    private Integer pendingCount;

    /**
     * 完成率(%)
     */
    private BigDecimal completionRate;

    /**
     * 取消率(%)
     */
    private BigDecimal cancellationRate;

    /**
     * 爽约率(%)
     */
    private BigDecimal noShowRate;

    /**
     * 总收入
     */
    private BigDecimal totalRevenue;

    /**
     * 平均客单价
     */
    private BigDecimal avgOrderValue;

    /**
     * 按日期统计
     */
    private List<DailyStatistics> dailyStatistics;

    /**
     * 按服务类型统计
     */
    private List<ServiceTypeStatistics> serviceTypeStatistics;

    /**
     * 按时段统计
     */
    private Map<String, Integer> timeSlotDistribution;

    @Data
    public static class DailyStatistics {
        private LocalDate date;
        private Integer appointmentCount;
        private Integer completedCount;
        private BigDecimal revenue;
    }

    @Data
    public static class ServiceTypeStatistics {
        private Long serviceId;
        private String serviceName;
        private Integer appointmentCount;
        private Integer completedCount;
        private BigDecimal revenue;
    }
}
