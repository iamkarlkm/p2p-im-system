package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 经营概览响应DTO - 功能#312: 商家BI数据智能平台
 */
@Data
public class BusinessOverviewResponse {

    /** 今日订单数 */
    private Integer todayOrderCount;

    /** 今日营收 */
    private BigDecimal todayRevenue;

    /** 今日访客 */
    private Integer todayVisitors;

    /** 待处理订单 */
    private Integer pendingOrders;

    /** 近7天订单数 */
    private Integer weekOrderCount;

    /** 近7天营收 */
    private BigDecimal weekRevenue;

    /** 近30天订单数 */
    private Integer monthOrderCount;

    /** 近30天营收 */
    private BigDecimal monthRevenue;

    /** 好评率 */
    private BigDecimal goodReviewRate;
}
