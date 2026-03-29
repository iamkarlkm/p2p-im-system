package com.im.backend.modules.appointment.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 排队叫号DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class QueueTicketDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 排队号ID */
    private Long id;

    /** 排队号 */
    private String ticketNo;

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

    /** 队列ID */
    private String queueId;

    /** 队列名称 */
    private String queueName;

    /** 排队状态 */
    private String status;

    /** 当前排队序号 */
    private Integer queueNumber;

    /** 前方等待人数 */
    private Integer peopleAhead;

    /** 预估等待时间(分钟) */
    private Integer estimatedWaitMinutes;

    /** 取号时间 */
    private LocalDateTime takeTime;

    /** 叫号时间 */
    private LocalDateTime callTime;

    /** 客户姓名 */
    @Size(max = 50, message = "客户姓名不能超过50字符")
    private String customerName;

    /** 客户电话 */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String customerPhone;

    /** 备注 */
    @Size(max = 200, message = "备注不能超过200字符")
    private String remark;

    /** 人数 */
    @Min(value = 1, message = "人数至少1人")
    @Max(value = 20, message = "人数不能超过20人")
    private Integer peopleCount;

    /** 取号方式 */
    private String source;

    /** 优先级: NORMAL-普通, VIP-VIP, MEMBER-会员 */
    private String priority;

    /** 服务窗口名称 */
    private String serviceWindowName;

    /** 是否提醒过 */
    private Boolean reminded;

    /** 关联预约ID */
    private Long appointmentId;

    /** 是否预约用户 */
    private Boolean hasAppointment;

    /** 预计可到达时间 */
    private LocalDateTime estimatedArrivalTime;

    /** 创建时间 */
    private LocalDateTime createTime;
}
