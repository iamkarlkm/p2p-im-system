package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 服务人员排班实体
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("staff_schedule")
public class StaffSchedule implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 排班ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 员工ID
     */
    private Long staffId;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 门店ID
     */
    private Long storeId;

    /**
     * 排班日期
     */
    private LocalDate scheduleDate;

    /**
     * 班次类型
     * 1-早班, 2-中班, 3-晚班, 4-全天, 5-休息, 6-自定义
     */
    private Integer shiftType;

    /**
     * 上班开始时间
     */
    private LocalTime workStartTime;

    /**
     * 上班结束时间
     */
    private LocalTime workEndTime;

    /**
     * 休息开始时间
     */
    private LocalTime breakStartTime;

    /**
     * 休息结束时间
     */
    private LocalTime breakEndTime;

    /**
     * 是否可预约
     */
    private Boolean bookable;

    /**
     * 可预约开始时间
     */
    private LocalTime bookingStartTime;

    /**
     * 可预约结束时间
     */
    private LocalTime bookingEndTime;

    /**
     * 最大可预约数量
     */
    private Integer maxBookingCount;

    /**
     * 已预约数量
     */
    private Integer bookedCount;

    /**
     * 备注
     */
    private String remark;

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
     * 班次类型名称
     */
    @TableField(exist = false)
    private String shiftTypeName;

    /**
     * 员工姓名
     */
    @TableField(exist = false)
    private String staffName;

    /**
     * 员工头像
     */
    @TableField(exist = false)
    private String staffAvatar;

    /**
     * 获取班次类型名称
     */
    public String getShiftTypeName() {
        if (this.shiftType == null) return "未知";
        switch (this.shiftType) {
            case 1: return "早班";
            case 2: return "中班";
            case 3: return "晚班";
            case 4: return "全天";
            case 5: return "休息";
            case 6: return "自定义";
            default: return "未知";
        }
    }

    /**
     * 是否已满
     */
    public boolean isFull() {
        return this.maxBookingCount != null && this.bookedCount != null 
            && this.bookedCount >= this.maxBookingCount;
    }

    /**
     * 是否可预约
     */
    public boolean isAvailable() {
        return this.bookable != null && this.bookable && !isFull();
    }

    /**
     * 获取工作时间字符串
     */
    public String getWorkTimeRange() {
        if (this.workStartTime == null || this.workEndTime == null) {
            return "";
        }
        return this.workStartTime.toString() + "-" + this.workEndTime.toString();
    }

    /**
     * 获取可预约时间字符串
     */
    public String getBookingTimeRange() {
        if (this.bookingStartTime == null || this.bookingEndTime == null) {
            return "";
        }
        return this.bookingStartTime.toString() + "-" + this.bookingEndTime.toString();
    }

    /**
     * 班次类型枚举
     */
    public enum ShiftType {
        MORNING(1, "早班"),
        AFTERNOON(2, "中班"),
        EVENING(3, "晚班"),
        FULL_DAY(4, "全天"),
        REST(5, "休息"),
        CUSTOM(6, "自定义");

        private final int code;
        private final String desc;

        ShiftType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static ShiftType fromCode(Integer code) {
            if (code == null) return null;
            for (ShiftType type : values()) {
                if (type.code == code) {
                    return type;
                }
            }
            return null;
        }
    }
}
