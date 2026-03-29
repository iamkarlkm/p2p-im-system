// 顾客画像分析实体
package com.im.entity.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class CustomerProfileAnalysis {
    private Long id;
    private Long merchantId;
    private LocalDate statDate;
    
    // 顾客总量
    private Integer totalCustomers;
    private Integer newCustomers;
    private Integer returningCustomers;
    private Integer lostCustomers;
    private Integer reactivatedCustomers;
    
    // 新老客比例
    private BigDecimal newCustomerRatio;
    private BigDecimal returningCustomerRatio;
    private BigDecimal retentionRate;
    
    // 地域分布
    private String provinceDistribution; // JSON
    private String cityDistribution; // JSON
    private String districtDistribution; // JSON
    private String top5Districts; // JSON
    private Integer sameCityCustomers;
    private Integer otherCityCustomers;
    private BigDecimal sameCityRatio;
    
    // 性别分布
    private Integer maleCustomers;
    private Integer femaleCustomers;
    private Integer unknownGenderCustomers;
    private BigDecimal maleRatio;
    private BigDecimal femaleRatio;
    
    // 年龄分布
    private String ageDistribution; // JSON - 年龄段分布
    private Integer under18;
    private Integer age18to25;
    private Integer age26to35;
    private Integer age36to45;
    private Integer age46to55;
    private Integer over55;
    private Integer unknownAge;
    
    // 消费频次
    private String frequencyDistribution; // JSON
    private Integer oneTimeCustomers;
    private Integer occasionalCustomers; // 2-3次
    private Integer regularCustomers; // 4-6次
    private Integer frequentCustomers; // 7-12次
    private Integer vipCustomers; // >12次
    
    // 消费金额分层 (RFM - Monetary)
    private String monetaryDistribution; // JSON
    private Integer lowValueCustomers; // <100
    private Integer midValueCustomers; // 100-500
    private Integer highValueCustomers; // 500-1000
    private Integer premiumCustomers; // >1000
    
    // 最近一次消费 (RFM - Recency)
    private String recencyDistribution; // JSON
    private Integer recent7Days;
    private Integer recent30Days;
    private Integer recent90Days;
    private Integer recent180Days;
    private Integer inactiveOver180;
    
    // RFM综合评分
    private String rfmSegments; // JSON - 客群细分
    private Integer championsCount;
    private Integer loyalCustomersCount;
    private Integer potentialLoyalistsCount;
    private Integer newCustomersCount;
    private Integer promisingCount;
    private Integer needAttentionCount;
    private Integer aboutToSleepCount;
    private Integer atRiskCount;
    private Integer cannotLoseCount;
    private Integer hibernatingCount;
    private Integer lostCount;
    
    // 消费偏好
    private String preferredCategories; // JSON
    private String preferredTimeSlots; // JSON
    private String preferredPaymentMethods; // JSON
    private BigDecimal avgOrderValue;
    private Integer avgVisitFrequency;
    
    // 会员数据
    private Integer memberCount;
    private Integer nonMemberCount;
    private BigDecimal memberRatio;
    private BigDecimal memberContributionRate;
    
    // 设备分布
    private Integer iosUsers;
    private Integer androidUsers;
    private Integer miniProgramUsers;
    private Integer webUsers;
    
    // 来源渠道
    private String sourceChannels; // JSON
    private Integer searchSource;
    private Integer recommendationSource;
    private Integer directSource;
    private Integer shareSource;
    private Integer adSource;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 生命周期价值
    private BigDecimal avgLifetimeValue;
    private BigDecimal totalLifetimeValue;
    private Integer avgCustomerLifespanDays;
    
    // 消费趋势
    private String spendingTrend; // JSON
    private String visitTrend; // JSON
    
    // 标签画像
    private String customerTags; // JSON - 标签云
    private String interests; // JSON - 兴趣偏好
    
    // 流失预警
    private Integer churnRiskCount;
    private String churnRiskCustomers; // JSON - 高风险流失客户
}
