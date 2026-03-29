package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 商户服务时段配置实体
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("appointment_time_config")
public class AppointmentTimeConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 服务ID
     */
    private Long serviceId;

    /**
     * 配置名称
     */
    private String configName;

    /**
     * 配置类型
     * 1-通用配置, 2-服务专属配置
     */
    private Integer configType;

    /**
     * 星期几(1-7)
     */
    private Integer dayOfWeek;

    /**
     * 开始日期(用于特殊日期配置)
     */
    private LocalDate startDate;

    /**
     * 结束日期(用于特殊日期配置)
     */
    private LocalDate endDate;

    /**
     * 营业开始时间
     */
    private LocalTime openTime;

    /**
     * 营业结束时间
     */
    private LocalTime closeTime;

    /**
     * 预约时段间隔(分钟)
     */
    private Integer timeSlotInterval;

    /**
     * 时段最大预约数
     */
    private Integer maxBookingsPerSlot;

    /**
     * 提前几天可预约
     */
    private Integer advanceBookingDays;

    /**
     * 最少提前几小时预约
     */
    private Integer minAdvanceHours;

    /**
     * 最多提前几小时预约
     */
    private Integer maxAdvanceHours;

    /**
     * 是否需要定金
     */
    private Boolean requireDeposit;

    /**
     * 定金金额
     */
    private BigDecimal depositAmount;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // ========== 扩展字段 ==========

    /**
     * 时段列表
     */
    @TableField(exist = false)
    private List<TimeSlot> timeSlots;

    /**
     * 配置类型枚举
     */
    public enum ConfigType {
        GENERAL(1, "通用配置"),
        SERVICE_SPECIFIC(2, "服务专属配置");

        private final int code;
        private final String desc;

        ConfigType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static ConfigType fromCode(Integer code) {
            if (code == null) return null;
            for (ConfigType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * 时段类
     */
    @Data
    public static class TimeSlot {
        /**
         * 开始时间
         */
        private LocalTime startTime;

        /**
         * 结束时间
         */
        private LocalTime endTime;

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
         * 是否满员
         */
        public boolean isFull() {
            return this.bookedCount != null && this.maxCapacity != null 
                && this.bookedCount >= this.maxCapacity;
        }

        /**
         * 获取时段显示文本
         */
        public String getTimeRange() {
            if (this.startTime == null || this.endTime == null) {
                return "";
            }
            return this.startTime.toString() + "-" + this.endTime.toString();
        }
    }
}
