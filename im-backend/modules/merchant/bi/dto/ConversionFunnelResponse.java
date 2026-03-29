package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 转化漏斗响应DTO
 */
@Data
public class ConversionFunnelResponse {

    /** 漏斗类型 */
    private String funnelType;

    /** 漏斗名称 */
    private String funnelName;

    /** 统计日期 */
    private String statsDate;

    /** 漏斗步骤 */
    private List<FunnelStep> steps;

    /** 总转化率 */
    private BigDecimal totalConversionRate;

    /** 流失率 */
    private BigDecimal churnRate;

    /**
     * 漏斗步骤
     */
    @Data
    public static class FunnelStep {
        /** 步骤名称 */
        private String stepName;
        /** 步骤序号 */
        private Integer stepOrder;
        /** 用户数 */
        private Integer userCount;
        /** 转化率 (相对于上一步) */
        private BigDecimal conversionRate;
        /** 总转化率 (相对于第一步) */
        private BigDecimal totalConversionRate;
        /** 流失数 */
        private Integer churnCount;
        /** 流失率 */
        private BigDecimal churnRate;
    }
}
