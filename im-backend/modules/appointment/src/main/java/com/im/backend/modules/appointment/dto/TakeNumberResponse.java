package com.im.backend.modules.appointment.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 取号响应
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class TakeNumberResponse {

    /**
     * 记录ID
     */
    private Long recordId;

    /**
     * 队列ID
     */
    private Long queueId;

    /**
     * 排队号码
     */
    private Integer queueNumber;

    /**
     * 队列名称
     */
    private String queueName;

    /**
     * 前面等待人数
     */
    private Integer waitingCount;

    /**
     * 预估等待时间(分钟)
     */
    private Integer estimatedWaitMinutes;

    /**
     * 当前叫号
     */
    private Integer currentNumber;

    /**
     * 取号时间
     */
    private LocalDateTime takeTime;

    /**
     * 二维码/取号凭证
     */
    private String qrCode;

    /**
     * 取号类型
     */
    private Integer takeType;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;
}
