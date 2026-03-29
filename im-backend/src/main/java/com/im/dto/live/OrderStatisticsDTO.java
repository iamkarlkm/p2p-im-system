package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 订单统计DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "订单统计数据")
public class OrderStatisticsDTO {

    @Schema(description = "订单总数")
    private Long totalOrders;

    @Schema(description = "待付款订单数")
    private Long pendingPaymentCount;

    @Schema(description = "待发货订单数")
    private Long pendingShipCount;

    @Schema(description = "待收货订单数")
    private Long pendingReceiveCount;

    @Schema(description = "已完成订单数")
    private Long completedCount;

    @Schema(description = "已取消订单数")
    private Long cancelledCount;

    @Schema(description = "退款中订单数")
    private Long refundingCount;

    @Schema(description = "商品总金额（元）")
    private BigDecimal totalProductAmount;

    @Schema(description = "运费总金额（元）")
    private BigDecimal totalFreightAmount;

    @Schema(description = "优惠总金额（元）")
    private BigDecimal totalDiscountAmount;

    @Schema(description = "实付总金额（元）")
    private BigDecimal totalPayAmount;

    @Schema(description = "退款总金额（元）")
    private BigDecimal totalRefundAmount;

    @Schema(description = "日均订单数")
    private Double avgDailyOrders;

    @Schema(description = "客单价（元）")
    private BigDecimal avgOrderAmount;

    @Schema(description = "转化率（%）")
    private BigDecimal conversionRate;

    @Schema(description = "按状态统计")
    private Map<String, Long> statusCountMap;

    @Schema(description = "按支付方式统计")
    private Map<String, Long> payTypeCountMap;

    @Schema(description = "按日期统计")
    private Map<String, DailyOrderStat> dailyStats;

    /**
     * 每日订单统计
     */
    @Data
    @Schema(description = "每日订单统计")
    public static class DailyOrderStat {

        @Schema(description = "日期")
        private String date;

        @Schema(description = "订单数")
        private Long orderCount;

        @Schema(description = "销售额（元）")
        private BigDecimal salesAmount;

        @Schema(description = "付款订单数")
        private Long paidCount;

        @Schema(description = "退款订单数")
        private Long refundCount;
    }
}
