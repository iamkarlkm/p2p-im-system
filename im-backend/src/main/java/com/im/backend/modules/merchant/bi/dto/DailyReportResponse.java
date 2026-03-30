package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 经营日报响应DTO - 功能#312: 商家BI数据智能平台
 */
@Data
public class DailyReportResponse {

    private LocalDate reportDate;
    private Integer orderCount;
    private BigDecimal orderAmount;
    private BigDecimal actualAmount;
    private BigDecimal refundAmount;
    private Integer visitorCount;
    private BigDecimal conversionRate;
    private BigDecimal avgOrderValue;
    private Integer newCustomerCount;
    private Integer oldCustomerCount;
    private Integer goodReviewCount;
    private Integer badReviewCount;
}
