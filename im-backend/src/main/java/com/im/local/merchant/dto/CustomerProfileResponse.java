package com.im.local.merchant.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 顾客画像分析响应DTO
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
public class CustomerProfileResponse {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 时间范围类型
     */
    private String timeRangeType;
    
    /**
     * 数据生成时间
     */
    private LocalDateTime generatedAt;
    
    /**
     * 性别分布
     */
    private List<GenderDistribution> genderDistribution;
    
    /**
     * 年龄分布
     */
    private List<AgeDistribution> ageDistribution;
    
    /**
     * 消费水平分布
     */
    private List<ConsumptionLevel> consumptionLevel;
    
    /**
     * 访问频次分布
     */
    private List<VisitFrequency> visitFrequency;
    
    /**
     * 偏好标签
     */
    private List<PreferenceTag> preferenceTags;
    
    // ==================== 内部类 ====================
    
    @Data
    @Builder
    public static class GenderDistribution {
        private String gender;
        private Integer percentage;
        private Integer count;
    }
    
    @Data
    @Builder
    public static class AgeDistribution {
        private String ageRange;
        private Integer percentage;
        private Integer count;
    }
    
    @Data
    @Builder
    public static class ConsumptionLevel {
        private String level;
        private Integer percentage;
        private BigDecimal avgAmount;
    }
    
    @Data
    @Builder
    public static class VisitFrequency {
        private String frequency;
        private Integer percentage;
        private Integer count;
    }
    
    @Data
    @Builder
    public static class PreferenceTag {
        private String tag;
        private Integer percentage;
    }
}
