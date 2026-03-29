package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 用户画像分析响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerPortraitResponse {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 总顾客数
     */
    private Integer totalCustomers;
    
    /**
     * 年龄段分布
     */
    private List<PortraitItemDTO> ageDistribution;
    
    /**
     * 性别分布
     */
    private List<PortraitItemDTO> genderDistribution;
    
    /**
     * 消费水平分布
     */
    private List<PortraitItemDTO> consumptionLevelDistribution;
    
    /**
     * 消费偏好
     */
    private List<PortraitItemDTO> preferenceDistribution;
    
    /**
     * 到访时段分布
     */
    private Map<String, Integer> visitTimeDistribution;
    
    /**
     * 忠诚度分布
     */
    private List<PortraitItemDTO> loyaltyDistribution;
    
    /**
     * 复购周期分析
     */
    private RepurchaseAnalysisDTO repurchaseAnalysis;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PortraitItemDTO {
        private String dimension;
        private String dimensionName;
        private Integer customerCount;
        private Integer orderCount;
        private BigDecimal totalRevenue;
        private BigDecimal avgOrderValue;
        private BigDecimal avgFrequency;
        private BigDecimal percentage;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepurchaseAnalysisDTO {
        private Integer repurchaseCustomers;
        private BigDecimal repurchaseRate;
        private BigDecimal avgRepurchaseDays;
        private List<RepurchaseIntervalDTO> intervalDistribution;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RepurchaseIntervalDTO {
        private String intervalRange;
        private Integer customerCount;
        private BigDecimal percentage;
    }
}
