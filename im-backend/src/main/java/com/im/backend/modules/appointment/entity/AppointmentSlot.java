package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 预约时段实体类
 * 管理商户的服务时段配置和可用性
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_appointment_slot")
public class AppointmentSlot extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 商户ID */
    @TableField("merchant_id")
    private Long merchantId;

    /** 服务类型ID */
    @TableField("service_type_id")
    private Long serviceTypeId;

    /** 日期 */
    @TableField("slot_date")
    private LocalDate slotDate;

    /** 时段开始时间 */
    @TableField("start_time")
    private LocalTime startTime;

    /** 时段结束时间 */
    @TableField("end_time")
    private LocalTime endTime;

    /** 时段状态: AVAILABLE-可预约, BOOKED-已预约, LOCKED-锁定, CLOSED-关闭 */
    @TableField("status")
    private String status;

    /** 最大可预约数量 */
    @TableField("max_capacity")
    private Integer maxCapacity;

    /** 已预约数量 */
    @TableField("booked_count")
    private Integer bookedCount;

    /** 剩余可预约数量 */
    @TableField("available_count")
    private Integer availableCount;

    /** 服务人员ID */
    @TableField("staff_id")
    private Long staffId;

    /** 服务资源ID */
    @TableField("resource_id")
    private Long resourceId;

    /** 是否特殊时段（节假日/活动时段） */
    @TableField("is_special")
    private Boolean isSpecial;

    /** 特殊时段价格系数 */
    @TableField("price_multiplier")
    private java.math.BigDecimal priceMultiplier;

    /** 特殊时段备注 */
    @TableField("special_remark")
    private String specialRemark;

    /** 时段标签: PEAK-高峰时段, NORMAL-普通时段, OFF_PEAK-闲时 */
    @TableField("time_tag")
    private String timeTag;

    /** 是否删除 */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    /** 乐观锁版本号 */
    @Version
    @TableField("version")
    private Integer version;

    // ==================== 业务方法 ====================

    /**
     * 获取时段字符串
     */
    public String getTimeRange() {
        if (startTime != null && endTime != null) {
            return startTime.toString() + "-" + endTime.toString();
        }
        return "";
    }

    /**
     * 检查是否有可用名额
     */
    public boolean hasAvailable() {
        return availableCount != null && availableCount > 0 && "AVAILABLE".equals(status);
    }

    /**
     * 预约一个名额
     */
    public boolean bookOne() {
        if (hasAvailable()) {
            bookedCount = bookedCount + 1;
            availableCount = availableCount - 1;
            if (availableCount <= 0) {
                status = "BOOKED";
            }
            return true;
        }
        return false;
    }

    /**
     * 取消预约释放名额
     */
    public boolean releaseOne() {
        if (bookedCount > 0) {
            bookedCount = bookedCount - 1;
            availableCount = availableCount + 1;
            if (!"AVAILABLE".equals(status)) {
                status = "AVAILABLE";
            }
            return true;
        }
        return false;
    }

    /**
     * 获取时段时长（分钟）
     */
    public int getDurationMinutes() {
        if (startTime != null && endTime != null) {
            return (int) java.time.Duration.between(startTime, endTime).toMinutes();
        }
        return 0;
    }

    /**
     * 检查是否跨天
     */
    public boolean isOvernight() {
        if (startTime != null && endTime != null) {
            return endTime.isBefore(startTime);
        }
        return false;
    }
}
