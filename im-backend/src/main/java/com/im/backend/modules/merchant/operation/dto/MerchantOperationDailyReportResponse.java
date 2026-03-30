package com.im.backend.modules.merchant.operation.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商户运营日报响应DTO
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "商户运营日报响应")
public class MerchantOperationDailyReportResponse {

    @Schema(description = "报表ID")
    private Long id;

    @Schema(description = "商户ID")
    private Long merchantId;

    @Schema(description = "统计日期")
    private LocalDate reportDate;

    @Schema(description = "订单总数")
    private Integer totalOrders;

    @Schema(description = "订单总金额")
    private BigDecimal totalAmount;

    @Schema(description = "成交订单数")
    private Integer completedOrders;

    @Schema(description = "退款订单数")
    private Integer refundOrders;

    @Schema(description = "退款金额")
    private BigDecimal refundAmount;

    @Schema(description = "访客数")
    private Integer visitorCount;

    @Schema(description = "转化率(%)")
    private BigDecimal conversionRate;

    @Schema(description = "客单价")
    private BigDecimal avgOrderValue;

    @Schema(description = "新增用户数")
    private Integer newUsers;

    @Schema(description = "复购用户数")
    private Integer returningUsers;

    @Schema(description = "好评数")
    private Integer positiveReviews;

    @Schema(description = "差评数")
    private Integer negativeReviews;

    @Schema(description = "平均评分")
    private BigDecimal avgRating;

    @Schema(description = "AI分析建议")
    private String aiSuggestions;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
