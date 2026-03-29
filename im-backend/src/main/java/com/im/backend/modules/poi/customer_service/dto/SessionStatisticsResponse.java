package com.im.backend.modules.poi.customer_service.dto;

import lombok.Data;

/**
 * 客服会话统计响应
 */
@Data
public class SessionStatisticsResponse {

    /**
     * 总会话数
     */
    private Integer totalSessions;

    /**
     * 活跃会话数
     */
    private Integer activeSessions;

    /**
     * 今日会话数
     */
    private Integer todaySessions;

    /**
     * 平均响应时间(秒)
     */
    private Integer avgResponseTime;

    /**
     * 平均会话时长(秒)
     */
    private Integer avgDuration;

    /**
     * 平均评分
     */
    private Double avgRating;

    /**
     * 机器人解决率(%)
     */
    private Integer robotResolutionRate;

    /**
     * 待分配会话数
     */
    private Integer pendingSessions;

    /**
     * 排队用户数
     */
    private Integer waitingUsers;
}
