package com.im.local.merchant.dto;

import lombok.Data;
import lombok.Builder;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * RFM客户分层分析响应DTO
 * 本地生活商户数据分析与经营洞察模块
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
public class RfmAnalysisResponse {
    
    /**
     * 商户ID
     */
    private Long merchantId;
    
    /**
     * 数据生成时间
     */
    private LocalDateTime generatedAt;
    
    /**
     * VIP客户
     */
    private Segment vipCustomers;
    
    /**
     * 忠诚客户
     */
    private Segment loyalCustomers;
    
    /**
     * 潜力客户
     */
    private Segment potentialCustomers;
    
    /**
     * 新客户
     */
    private Segment newCustomers;
    
    /**
     * 流失风险客户
     */
    private Segment atRiskCustomers;
    
    /**
     * 流失客户
     */
    private Segment lostCustomers;
    
    // ==================== 内部类 ====================
    
    @Data
    @Builder
    public static class Segment {
        /**
         * 分层名称
         */
        private String segmentName;
        
        /**
         * 客户数量
         */
        private Integer customerCount;
        
        /**
         * 占比
         */
        private String contributionRate;
        
        /**
         * 描述
         */
        private String description;
        
        /**
         * 平均最近消费天数（Recency）
         */
        private Integer avgRecencyDays;
        
        /**
         * 平均消费频次（Frequency）
         */
        private Integer avgFrequency;
        
        /**
         * 平均消费金额（Monetary）
         */
        private BigDecimal avgMonetary;
    }
}
