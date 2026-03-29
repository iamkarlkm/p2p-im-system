package com.im.backend.modules.appointment.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalTime;

/**
 * 创建队列请求
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class CreateQueueRequest {

    /**
     * 商户ID
     */
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    /**
     * 门店ID
     */
    @NotNull(message = "门店ID不能为空")
    private Long storeId;

    /**
     * 队列名称
     */
    @NotNull(message = "队列名称不能为空")
    private String queueName;

    /**
     * 队列类型
     */
    private Integer queueType = 1;

    /**
     * 服务类型ID
     */
    private Long serviceTypeId;

    /**
     * 服务类型名称
     */
    private String serviceTypeName;

    /**
     * 队列最大容量
     */
    private Integer maxCapacity = 100;

    /**
     * 预估服务时长(分钟)
     */
    private Integer estimatedServiceTime = 30;

    /**
     * 工作窗口数量
     */
    private Integer serviceWindowCount = 1;

    /**
     * 营业开始时间
     */
    private LocalTime businessStartTime;

    /**
     * 营业结束时间
     */
    private LocalTime businessEndTime;

    /**
     * 是否启用在线取号
     */
    private Boolean enableOnlineQueue = true;

    /**
     * 在线取号提前量(分钟)
     */
    private Integer onlineQueueAdvanceMinutes = 30;

    /**
     * 是否启用预约优先
     */
    private Boolean enableAppointmentPriority = true;

    /**
     * 预约优先阈值(分钟)
     */
    private Integer appointmentPriorityThreshold = 15;
}
