package com.im.service.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 经营报表响应DTO
 */
@Data
public class BusinessReportResponse {

    /** 报表数据列表 */
    private List<ReportItem> reportData;

    /** 合计数据 */
    private ReportSummary summary;

    @Data
    public static class ReportItem {
        private String date;
        private BigDecimal revenue;
        private Integer orderCount;
        private Integer customerCount;
        private BigDecimal avgOrderValue;
        private Integer newCustomerCount;
        private Integer oldCustomerCount;
        private BigDecimal refundAmount;
    }

    @Data
    public static class ReportSummary {
        private BigDecimal totalRevenue;
        private Integer totalOrderCount;
        private Integer totalCustomerCount;
        private BigDecimal avgOrderValue;
        private BigDecimal totalRefundAmount;
        private BigDecimal refundRate;
    }
}
