package com.im.backend.modules.appointment.dto;

import lombok.Data;

import java.time.LocalTime;

/**
 * 队列信息响应
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class QueueInfoResponse {

    /**
     * 队列ID
     */
    private Long id;

    /**
     * 队列编号
     */
    private String queueCode;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 队列类型
     */
    private Integer queueType;

    /**
     * 队列类型名称
     */
    private String queueTypeName;

    /**
     * 服务类型名称
     */
    private String serviceTypeName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 当前排队人数
     */
    private Integer currentQueueCount;

    /**
     * 当前叫号
     */
    private Integer currentNumber;

    /**
     * 队列最大容量
     */
    private Integer maxCapacity;

    /**
     * 预估服务时长
     */
    private Integer estimatedServiceTime;

    /**
     * 平均等待时间
     */
    private Integer averageWaitTime;

    /**
     * 工作窗口数量
     */
    private Integer serviceWindowCount;

    /**
     * 营业开始时间
     */
    private LocalTime businessStartTime;

    /**
     * 营业结束时间
     */
    private LocalTime businessEndTime;

    /**
     * 是否可在线取号
     */
    private Boolean canTakeOnline;

    /**
     * 是否已满
     */
    private Boolean isFull;
}
