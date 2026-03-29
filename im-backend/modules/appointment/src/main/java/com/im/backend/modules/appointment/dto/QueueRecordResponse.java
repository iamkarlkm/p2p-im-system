package com.im.backend.modules.appointment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 排队记录响应
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class QueueRecordResponse {

    /**
     * 记录ID
     */
    private Long id;

    /**
     * 队列ID
     */
    private Long queueId;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 排队号码
     */
    private Integer queueNumber;

    /**
     * 排队类型
     */
    private Integer takeType;

    /**
     * 排队类型名称
     */
    private String takeTypeName;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 前面等待人数
     */
    private Integer waitingCount;

    /**
     * 预估等待时间
     */
    private Integer estimatedWaitMinutes;

    /**
     * 取号时间
     */
    private LocalDateTime takeTime;

    /**
     * 叫号时间
     */
    private LocalDateTime callTime;

    /**
     * 服务窗口
     */
    private Integer serviceWindow;
}
