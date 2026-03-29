package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 优惠券效果分析响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponAnalyticsResponse {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 统计日期范围
     */
    private LocalDate startDate;
    private LocalDate endDate;
    
    /**
     * 总体概览
     */
    private OverviewDTO overview;
    
    /**
     * 各优惠券效果列表
     */
    private List<CouponDetailDTO> couponDetails;
    
    /**
     * 趋势数据
     */
    private List<TrendDTO> trends;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OverviewDTO {
        private Integer totalIssued;
        private Integer totalClaimed;
        private Integer totalUsed;
        private BigDecimal claimRate;
        private BigDecimal usageRate;
        private BigDecimal totalDiscountAmount;
        private BigDecimal drivenRevenue;
        private BigDecimal roi;
        private Integer newCustomers;
        private BigDecimal customerAcquisitionCost;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponDetailDTO {
        private Long couponId;
        private String couponName;
        private String couponType;
        private Integer totalIssued;
        private Integer totalClaimed;
        private Integer totalUsed;
        private BigDecimal claimRate;
        private BigDecimal usageRate;
        private BigDecimal faceValue;
        private BigDecimal totalDiscountAmount;
        private BigDecimal drivenRevenue;
        private Integer drivenOrders;
        private Integer newCustomerUsage;
        private BigDecimal roi;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendDTO {
        private LocalDate date;
        private Integer issued;
        private Integer claimed;
        private Integer used;
        private BigDecimal drivenRevenue;
    }
}
