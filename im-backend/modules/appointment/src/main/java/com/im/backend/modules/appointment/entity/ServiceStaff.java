package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * 服务人员实体
 * 本地生活服务预约与排班管理系统的核心实体
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("service_staff")
public class ServiceStaff implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 员工ID
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
     * 员工编号
     */
    private String staffNo;

    /**
     * 员工姓名
     */
    private String name;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 性别
     * 0-未知, 1-男, 2-女
     */
    private Integer gender;

    /**
     * 职位
     */
    private String position;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 服务星级(1-5)
     */
    private Integer starLevel;

    /**
     * 评分
     */
    private Double rating;

    /**
     * 评分数量
     */
    private Integer ratingCount;

    /**
     * 服务项目数量
     */
    private Integer serviceCount;

    /**
     * 从业年限
     */
    private Integer experienceYears;

    /**
     * 状态
     * 0-离职, 1-在职, 2-休假中
     */
    private Integer status;

    /**
     * 是否可预约
     */
    private Boolean bookable;

    /**
     * 今日预约状态
     * 0-休息, 1-可预约, 2-已满
     */
    private Integer todayBookingStatus;

    /**
     * 排序号
     */
    private Integer sortOrder;

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
     * 服务项目列表
     */
    @TableField(exist = false)
    private List<Long> serviceIds;

    /**
     * 服务项目名称列表
     */
    @TableField(exist = false)
    private List<String> serviceNames;

    /**
     * 今日排班
     */
    @TableField(exist = false)
    private StaffSchedule todaySchedule;

    /**
     * 状态名称
     */
    @TableField(exist = false)
    private String statusName;

    /**
     * 获取状态名称
     */
    public String getStatusName() {
        if (this.status == null) return "未知";
        switch (this.status) {
            case 0: return "离职";
            case 1: return "在职";
            case 2: return "休假中";
            default: return "未知";
        }
    }

    /**
     * 是否可服务
     */
    public boolean isAvailable() {
        return this.status != null && this.status == 1 && this.bookable != null && this.bookable;
    }

    /**
     * 状态枚举
     */
    public enum Status {
        RESIGNED(0, "离职"),
        ACTIVE(1, "在职"),
        ON_LEAVE(2, "休假中");

        private final int code;
        private final String desc;

        Status(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static Status fromCode(Integer code) {
            if (code == null) return null;
            for (Status status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return null;
        }
    }

    /**
     * 今日预约状态枚举
     */
    public enum TodayBookingStatus {
        REST(0, "休息"),
        AVAILABLE(1, "可预约"),
        FULL(2, "已满");

        private final int code;
        private final String desc;

        TodayBookingStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static TodayBookingStatus fromCode(Integer code) {
            if (code == null) return null;
            for (TodayBookingStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            return null;
        }
    }
}
