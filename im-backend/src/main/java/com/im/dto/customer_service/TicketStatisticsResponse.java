package com.im.dto.customer_service;

import lombok.Data;
import java.util.Map;

/**
 * 工单统计响应DTO
 * 功能 #319 - 智能客服与工单管理系统
 */
@Data
public class TicketStatisticsResponse {
    
    /** 总工单数 */
    private Integer totalTickets;
    
    /** 待处理数 */
    private Integer pendingCount;
    
    /** 处理中数 */
    private Integer processingCount;
    
    /** 已解决数 */
    private Integer resolvedCount;
    
    /** 已关闭数 */
    private Integer closedCount;
    
    /** 今日新增 */
    private Integer todayNewCount;
    
    /** 今日解决 */
    private Integer todayResolvedCount;
    
    /** 平均处理时间（分钟） */
    private Double avgProcessTime;
    
    /** 满意度评分 */
    private Double satisfactionScore;
    
    /** SLA达成率 */
    private Double slaAchievementRate;
    
    /** 按类型统计 */
    private Map<String, Integer> countByType;
    
    /** 按优先级统计 */
    private Map<String, Integer> countByPriority;
    
    /** 7天趋势 */
    private Map<String, Integer> weeklyTrend;
}
