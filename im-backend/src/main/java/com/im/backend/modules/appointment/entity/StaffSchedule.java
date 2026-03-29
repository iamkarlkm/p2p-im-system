package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 员工排班实体类
 * 管理商户员工的工作排班和可预约时段
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_staff_schedule")
public class StaffSchedule extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 商户ID */
    @TableField("merchant_id")
    private Long merchantId;

    /** 员工ID */
    @TableField("staff_id")
    private Long staffId;

    /** 员工姓名 */
    @TableField("staff_name")
    private String staffName;

    /** 排班日期 */
    @TableField("schedule_date")
    private LocalDate scheduleDate;

    /** 班次类型: MORNING-早班, AFTERNOON-中班, EVENING-晚班, FULL-全天, CUSTOM-自定义 */
    @TableField("shift_type")
    private String shiftType;

    /** 上班开始时间 */
    @TableField("start_time")
    private LocalTime startTime;

    /** 上班结束时间 */
    @TableField("end_time")
    private LocalTime endTime;

    /** 排班状态: WORKING-上班, REST-休息, LEAVE-请假, HOLIDAY-休假, TRAINING-培训 */
    @TableField("status")
    private String status;

    /** 是否可预约 */
    @TableField("available_for_booking")
    private Boolean availableForBooking;

    /** 最大可预约数（每天） */
    @TableField("max_daily_appointments")
    private Integer maxDailyAppointments;

    /** 已预约数 */
    @TableField("booked_count")
    private Integer bookedCount;

    /** 服务技能标签（逗号分隔） */
    @TableField("skills")
    private String skills;

    /** 可服务项目ID列表 */
    @TableField("service_type_ids")
    private String serviceTypeIds;

    /** 专属服务资源ID */
    @TableField("dedicated_resource_id")
    private Long dedicatedResourceId;

    /** 排班备注 */
    @TableField("remark")
    private String remark;

    /** 是否删除 */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    // ==================== 业务方法 ====================

    /**
     * 获取工作时间字符串
     */
    public String getWorkHours() {
        if (startTime != null && endTime != null) {
            return startTime.toString() + " - " + endTime.toString();
        }
        return "";
    }

    /**
     * 计算工作时长（分钟）
     */
    public int getWorkDurationMinutes() {
        if (startTime != null && endTime != null) {
            return (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
        return 0;
    }

    /**
     * 检查是否可以预约
     */
    public boolean canAcceptBooking() {
        return "WORKING".equals(status) && 
               Boolean.TRUE.equals(availableForBooking) &&
               (maxDailyAppointments == null || bookedCount < maxDailyAppointments);
    }

    /**
     * 获取剩余可预约数
     */
    public int getRemainingCapacity() {
        if (maxDailyAppointments == null) {
            return Integer.MAX_VALUE;
        }
        return maxDailyAppointments - (bookedCount != null ? bookedCount : 0);
    }

    /**
     * 预约一个名额
     */
    public boolean bookOne() {
        if (canAcceptBooking()) {
            bookedCount = (bookedCount != null ? bookedCount : 0) + 1;
            return true;
        }
        return false;
    }

    /**
     * 释放一个名额
     */
    public boolean releaseOne() {
        if (bookedCount != null && bookedCount > 0) {
            bookedCount--;
            return true;
        }
        return false;
    }

    /**
     * 获取技能列表
     */
    public java.util.List<String> getSkillList() {
        if (skills != null && !skills.isEmpty()) {
            return java.util.Arrays.asList(skills.split(","));
        }
        return new java.util.ArrayList<>();
    }

    /**
     * 检查是否支持指定服务类型
     */
    public boolean supportsServiceType(Long serviceTypeId) {
        if (serviceTypeIds == null || serviceTypeIds.isEmpty()) {
            return true;
        }
        return serviceTypeIds.contains(serviceTypeId.toString());
    }

    /**
     * 检查时间段是否在排班时间内
     */
    public boolean isWithinSchedule(LocalTime appointmentStart, LocalTime appointmentEnd) {
        if (startTime == null || endTime == null) {
            return false;
        }
        return !appointmentStart.isBefore(startTime) && !appointmentEnd.isAfter(endTime);
    }

    /**
     * 检查日期是否是今天
     */
    public boolean isToday() {
        return scheduleDate != null && scheduleDate.equals(LocalDate.now());
    }

    /**
     * 检查日期是否在未来
     */
    public boolean isFuture() {
        return scheduleDate != null && scheduleDate.isAfter(LocalDate.now());
    }
}
