package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 营销活动效果分析响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignAnalyticsResponse {
    
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
     * 活动列表
     */
    private List<CampaignDetailDTO> campaigns;
    
    /**
     * 汇总数据
     */
    private SummaryDTO summary;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CampaignDetailDTO {
        private Long campaignId;
        private String campaignName;
        private String campaignType;
        private LocalDate startDate;
        private LocalDate endDate;
        private Integer impressions;
        private Integer clicks;
        private BigDecimal ctr;
        private Integer participants;
        private BigDecimal participationRate;
        private Integer conversions;
        private BigDecimal conversionRate;
        private BigDecimal conversionRevenue;
        private Integer newCustomers;
        private Integer shares;
        private BigDecimal campaignCost;
        private BigDecimal roi;
        private BigDecimal cpa;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SummaryDTO {
        private Integer totalCampaigns;
        private Integer totalImpressions;
        private Integer totalClicks;
        private BigDecimal avgCtr;
        private Integer totalConversions;
        private BigDecimal avgConversionRate;
        private BigDecimal totalConversionRevenue;
        private BigDecimal totalCost;
        private BigDecimal totalRoi;
        private Integer totalNewCustomers;
        private BigDecimal avgCpa;
    }
}
