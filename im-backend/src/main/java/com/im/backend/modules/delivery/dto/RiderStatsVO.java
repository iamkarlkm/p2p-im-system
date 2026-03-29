package com.im.backend.modules.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 骑手统计VO
 */
@Data
public class RiderStatsVO {

    private Long riderId;
    private String riderName;

    /** 今日统计 */
    private Integer todayOrders;
    private Integer todayCompleted;
    private Integer todayCancelled;
    private Integer todayDistance;
    private BigDecimal todayEarnings;

    /** 本周统计 */
    private Integer weekOrders;
    private BigDecimal weekEarnings;

    /** 本月统计 */
    private Integer monthOrders;
    private BigDecimal monthEarnings;

    /** 总统计 */
    private Integer totalOrders;
    private BigDecimal totalEarnings;
    private BigDecimal avgRating;
    private BigDecimal onTimeRate;
}
