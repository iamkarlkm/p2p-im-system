package com.im.backend.modules.appointment.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

/**
 * 预约请求/响应DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class AppointmentDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 预约ID */
    private Long id;

    /** 预约编号 */
    private String appointmentNo;

    /** 用户ID */
    private Long userId;

    /** 商户ID */
    @NotNull(message = "商户不能为空")
    private Long merchantId;

    /** 服务类型ID */
    @NotNull(message = "服务类型不能为空")
    private Long serviceTypeId;

    /** 服务类型名称 */
    private String serviceTypeName;

    /** 预约日期 */
    @NotNull(message = "预约日期不能为空")
    @FutureOrPresent(message = "预约日期必须是今天或未来日期")
    private LocalDate appointmentDate;

    /** 预约开始时间 */
    @NotNull(message = "预约时间不能为空")
    private LocalTime startTime;

    /** 预约结束时间 */
    private LocalTime endTime;

    /** 预约状态 */
    private String status;

    /** 服务人员ID */
    private Long staffId;

    /** 服务人员名称 */
    private String staffName;

    /** 服务资源ID */
    private Long resourceId;

    /** 服务资源名称 */
    private String resourceName;

    /** 预约人数 */
    @NotNull(message = "预约人数不能为空")
    @Min(value = 1, message = "预约人数至少1人")
    @Max(value = 50, message = "预约人数不能超过50人")
    private Integer peopleCount;

    /** 客户姓名 */
    @NotBlank(message = "客户姓名不能为空")
    @Size(max = 50, message = "客户姓名不能超过50字符")
    private String customerName;

    /** 客户电话 */
    @NotBlank(message = "客户电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String customerPhone;

    /** 客户备注 */
    @Size(max = 500, message = "备注不能超过500字符")
    private String customerRemark;

    /** 预估服务时长(分钟) */
    private Integer estimatedDuration;

    /** 预估价格 */
    private BigDecimal estimatedPrice;

    /** 是否会员预约 */
    private Boolean isMember;

    /** 会员等级 */
    private String memberLevel;

    /** 预约来源 */
    private String source;

    /** 创建时间 */
    private java.time.LocalDateTime createTime;

    /** 确认时间 */
    private java.time.LocalDateTime confirmTime;

    /** 服务开始时间 */
    private java.time.LocalDateTime serviceStartTime;

    /** 服务完成时间 */
    private java.time.LocalDateTime serviceEndTime;

    /** 是否使用优惠券 */
    private Boolean useCoupon;

    /** 优惠券ID */
    private Long couponId;
}
