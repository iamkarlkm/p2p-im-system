package com.im.backend.modules.appointment.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 排队进度响应
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class QueueProgressResponse {

    /**
     * 记录ID
     */
    private Long recordId;

    /**
     * 排队号码
     */
    private Integer queueNumber;

    /**
     * 当前叫号
     */
    private Integer currentNumber;

    /**
     * 前面等待人数
     */
    private Integer waitingCount;

    /**
     * 预估等待时间(分钟)
     */
    private Integer estimatedWaitMinutes;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 进度百分比
     */
    private Integer progressPercent;

    /**
     * 服务窗口号
     */
    private Integer serviceWindow;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否需要提醒
     */
    private Boolean needAlert;
}
