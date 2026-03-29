package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 顾客地域分布响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerGeoDistributionResponse {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 省级分布
     */
    private List<GeoDistributionDTO> provinceDistribution;
    
    /**
     * 市级分布
     */
    private List<GeoDistributionDTO> cityDistribution;
    
    /**
     * 区级分布
     */
    private List<GeoDistributionDTO> districtDistribution;
    
    /**
     * 热力图数据
     */
    private List<HeatmapPointDTO> heatmapData;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoDistributionDTO {
        private String regionCode;
        private String regionName;
        private Integer customerCount;
        private Integer orderCount;
        private BigDecimal revenue;
        private BigDecimal avgOrderValue;
        private Integer newCustomerCount;
        private Integer returningCustomerCount;
        private BigDecimal percentage;
        private BigDecimal growthRate;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HeatmapPointDTO {
        private BigDecimal longitude;
        private BigDecimal latitude;
        private Integer intensity;
        private String regionName;
    }
}
