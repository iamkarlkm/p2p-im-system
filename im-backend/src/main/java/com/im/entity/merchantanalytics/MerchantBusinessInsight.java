// 商户经营洞察实体
package com.im.entity.merchantanalytics;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MerchantBusinessInsight {
    private Long id;
    private Long merchantId;
    private LocalDate insightDate;
    
    // 洞察类型
    private String insightType; // REVENUE, TRAFFIC, OPERATION, MARKETING, COMPETITOR
    private String insightLevel; // INFO, WARNING, CRITICAL, OPPORTUNITY
    private String insightTitle;
    private String insightDescription;
    private String insightDetails;
    
    // 数据指标
    private String relatedMetrics; // JSON
    private BigDecimal metricValue;
    private BigDecimal metricChange;
    private BigDecimal metricBenchmark;
    
    // 洞察来源
    private String dataSource;
    private String algorithmType;
    private Double confidenceScore;
    
    // 建议行动
    private String recommendedAction;
    private String actionPriority; // HIGH, MEDIUM, LOW
    private BigDecimal expectedImpact;
    
    // 状态
    private Boolean isRead;
    private LocalDateTime readAt;
    private Boolean isActioned;
    private LocalDateTime actionedAt;
    private String actionResult;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 扩展字段
    private String relatedInsightIds; // 关联洞察
    private String trendDirection; // UP, DOWN, STABLE
    private Integer similarMerchantCount; // 相似情况商户数
    private String industryBenchmark; // 行业基准
    
    // 预警阈值
    private BigDecimal warningThreshold;
    private BigDecimal criticalThreshold;
    private Boolean isThresholdTriggered;
    
    // 时段分析
    private String applicableHours;
    private String applicableDays;
    
    // 客群标签
    private String targetCustomerSegments;
    
    // 效果追踪
    private Boolean isTracked;
    private LocalDateTime trackingStartedAt;
    private String trackingResults;
    
    // 自动化处理
    private Boolean canAutoResolve;
    private String autoResolveAction;
    private LocalDateTime autoResolvedAt;
}
