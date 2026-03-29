package com.im.backend.modules.appointment.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.im.backend.common.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 服务预约实体类
 * 本地生活服务预约与排班管理系统核心实体
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("im_appointment")
public class Appointment extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 预约编号 */
    @TableField("appointment_no")
    private String appointmentNo;

    /** 用户ID */
    @TableField("user_id")
    private Long userId;

    /** 商户ID */
    @TableField("merchant_id")
    private Long merchantId;

    /** 服务类型ID */
    @TableField("service_type_id")
    private Long serviceTypeId;

    /** 服务类型名称 */
    @TableField("service_type_name")
    private String serviceTypeName;

    /** 预约日期 */
    @TableField("appointment_date")
    private LocalDate appointmentDate;

    /** 预约开始时间 */
    @TableField("start_time")
    private LocalTime startTime;

    /** 预约结束时间 */
    @TableField("end_time")
    private LocalTime endTime;

    /** 预约状态: PENDING-待确认, CONFIRMED-已确认, IN_SERVICE-服务中, COMPLETED-已完成, CANCELLED-已取消, NO_SHOW-爽约 */
    @TableField("status")
    private String status;

    /** 服务人员ID */
    @TableField("staff_id")
    private Long staffId;

    /** 服务人员名称 */
    @TableField("staff_name")
    private String staffName;

    /** 服务资源ID（工位/包间/设备等） */
    @TableField("resource_id")
    private Long resourceId;

    /** 服务资源名称 */
    @TableField("resource_name")
    private String resourceName;

    /** 预约人数 */
    @TableField("people_count")
    private Integer peopleCount;

    /** 客户姓名 */
    @TableField("customer_name")
    private String customerName;

    /** 客户电话 */
    @TableField("customer_phone")
    private String customerPhone;

    /** 客户备注 */
    @TableField("customer_remark")
    private String customerRemark;

    /** 商户备注 */
    @TableField("merchant_remark")
    private String merchantRemark;

    /** 取消原因 */
    @TableField("cancel_reason")
    private String cancelReason;

    /** 取消时间 */
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

    /** 取消操作方: USER-用户, MERCHANT-商户, SYSTEM-系统 */
    @TableField("cancel_by")
    private String cancelBy;

    /** 确认时间 */
    @TableField("confirm_time")
    private LocalDateTime confirmTime;

    /** 开始服务时间 */
    @TableField("service_start_time")
    private LocalDateTime serviceStartTime;

    /** 完成服务时间 */
    @TableField("service_end_time")
    private LocalDateTime serviceEndTime;

    /** 预约来源: APP-APP预约, MINI_PROGRAM-小程序, WEB-网页, PHONE-电话, WALK_IN-到店 */
    @TableField("source")
    private String source;

    /** 是否使用排队号 */
    @TableField("use_queue_ticket")
    private Boolean useQueueTicket;

    /** 关联排队号ID */
    @TableField("queue_ticket_id")
    private Long queueTicketId;

    /** 提醒状态: NONE-未提醒, REMINDED-已提醒, CONFIRMED-已确认到达 */
    @TableField("remind_status")
    private String remindStatus;

    /** 提醒时间 */
    @TableField("remind_time")
    private LocalDateTime remindTime;

    /** 预估服务时长(分钟) */
    @TableField("estimated_duration")
    private Integer estimatedDuration;

    /** 实际服务时长(分钟) */
    @TableField("actual_duration")
    private Integer actualDuration;

    /** 预估价格 */
    @TableField("estimated_price")
    private java.math.BigDecimal estimatedPrice;

    /** 实际价格 */
    @TableField("actual_price")
    private java.math.BigDecimal actualPrice;

    /** 是否预付费 */
    @TableField("prepaid")
    private Boolean prepaid;

    /** 预付金额 */
    @TableField("prepaid_amount")
    private java.math.BigDecimal prepaidAmount;

    /** 是否会员预约 */
    @TableField("is_member")
    private Boolean isMember;

    /** 会员等级 */
    @TableField("member_level")
    private String memberLevel;

    /** 享受的会员折扣 */
    @TableField("member_discount")
    private java.math.BigDecimal memberDiscount;

    /** 是否使用优惠券 */
    @TableField("use_coupon")
    private Boolean useCoupon;

    /** 优惠券ID */
    @TableField("coupon_id")
    private Long couponId;

    /** 是否删除 */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;

    /** 删除时间 */
    @TableField("delete_time")
    private LocalDateTime deleteTime;

    /** 乐观锁版本号 */
    @Version
    @TableField("version")
    private Integer version;

    // ==================== 业务方法 ====================

    /**
     * 生成预约编号
     */
    public void generateAppointmentNo() {
        this.appointmentNo = "AP" + System.currentTimeMillis() + String.format("%04d", (int)(Math.random() * 10000));
    }

    /**
     * 检查是否可以取消
     */
    public boolean canCancel() {
        return "PENDING".equals(status) || "CONFIRMED".equals(status);
    }

    /**
     * 检查是否可以修改
     */
    public boolean canModify() {
        return "PENDING".equals(status);
    }

    /**
     * 获取预约时间段字符串
     */
    public String getTimeRange() {
        if (startTime != null && endTime != null) {
            return startTime.toString() + " - " + endTime.toString();
        }
        return "";
    }

    /**
     * 计算服务时长
     */
    public Integer calculateDuration() {
        if (serviceStartTime != null && serviceEndTime != null) {
            return (int) java.time.Duration.between(serviceStartTime, serviceEndTime).toMinutes();
        }
        return estimatedDuration;
    }

    /**
     * 是否即将过期（30分钟内）
     */
    public boolean isExpiringSoon() {
        if (appointmentDate == null || startTime == null) {
            return false;
        }
        LocalDateTime appointmentTime = LocalDateTime.of(appointmentDate, startTime);
        LocalDateTime now = LocalDateTime.now();
        return appointmentTime.isAfter(now) && 
               appointmentTime.isBefore(now.plusMinutes(30));
    }

    /**
     * 是否已过期
     */
    public boolean isExpired() {
        if (appointmentDate == null || startTime == null) {
            return false;
        }
        LocalDateTime appointmentTime = LocalDateTime.of(appointmentDate, startTime);
        return appointmentTime.isBefore(LocalDateTime.now());
    }
}
