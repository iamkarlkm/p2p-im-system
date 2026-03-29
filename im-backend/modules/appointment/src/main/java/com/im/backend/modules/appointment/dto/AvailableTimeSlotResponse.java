package com.im.backend.modules.appointment.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 可预约时段响应
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class AvailableTimeSlotResponse {

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 时段显示文本
     */
    private String timeRange;

    /**
     * 是否可预约
     */
    private Boolean available;

    /**
     * 已预约数量
     */
    private Integer bookedCount;

    /**
     * 最大可预约数量
     */
    private Integer maxCapacity;

    /**
     * 剩余可预约数量
     */
    private Integer remainingCapacity;

    /**
     * 是否已满
     */
    private Boolean isFull;

    /**
     * 推荐度(0-100)
     */
    private Integer recommendationScore;

    /**
     * 推荐理由
     */
    private String recommendationReason;

    /**
     * 是否是推荐时段
     */
    private Boolean isRecommended;

    /**
     * 推荐服务人员数量
     */
    private Integer availableStaffCount;

    public Integer getRemainingCapacity() {
        if (this.maxCapacity == null || this.bookedCount == null) {
            return 0;
        }
        return Math.max(0, this.maxCapacity - this.bookedCount);
    }

    public Boolean getIsFull() {
        return getRemainingCapacity() <= 0;
    }
}
