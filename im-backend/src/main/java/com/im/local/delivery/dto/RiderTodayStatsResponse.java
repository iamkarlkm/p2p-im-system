package com.im.local.delivery.dto;

import lombok.Data;

/**
 * 骑手今日统计响应
 */
@Data
public class RiderTodayStatsResponse {
    
    /** 今日接单数 */
    private Integer todayOrderCount;
    
    /** 今日完成单数 */
    private Integer todayCompletedCount;
    
    /** 今日配送距离(米) */
    private Integer todayDistance;
    
    /** 今日收入 */
    private java.math.BigDecimal todayIncome;
    
    /** 在线时长(分钟) */
    private Integer onlineMinutes;
    
    /** 当前配送中订单数 */
    Integer deliveringCount;
}
