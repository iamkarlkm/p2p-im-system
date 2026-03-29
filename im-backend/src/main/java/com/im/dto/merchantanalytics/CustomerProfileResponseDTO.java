// 顾客画像响应DTO
package com.im.dto.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
public class CustomerProfileResponseDTO {
    private Long merchantId;
    private LocalDate statDate;
    
    // 顾客总量
    private Integer totalCustomers;
    private Integer newCustomers;
    private Integer returningCustomers;
    private BigDecimal retentionRate;
    
    // 新老客比例
    private BigDecimal newCustomerRatio;
    private BigDecimal returningCustomerRatio;
    
    // 性别分布
    private GenderDistributionDTO genderDistribution;
    
    // 年龄分布
    private Map<String, Integer> ageDistribution;
    
    // 地域分布
    private List<GeoDistributionDTO> geoDistribution;
    
    // 消费频次
    private Map<String, Object> frequencyDistribution;
    
    // 消费金额分层
    private Map<String, Object> monetaryDistribution;
    
    // RFM客群
    private Map<String, Object> rfmSegments;
    
    // 消费偏好
    private BigDecimal avgOrderValue;
    private List<String> preferredCategories;
    
    // 会员数据
    private Integer memberCount;
    private BigDecimal memberRatio;
    private BigDecimal memberContributionRate;
    
    // 高价值客户
    private Integer highValueCustomerCount;
    private Integer churnRiskCount;
}
