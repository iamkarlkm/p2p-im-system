package com.im.backend.modules.merchant.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 营销效果追踪响应DTO
 */
@Data
public class MarketingEffectResponse {

    /** 商户ID */
    private Long merchantId;

    /** 统计时段 */
    private String period;

    /** 营销效果概览 */
    private MarketingOverview overview;

    /** 营销列表 */
    private List<MarketingItem> marketingList;

    /**
     * 营销概览
     */
    @Data
    public static class MarketingOverview {
        /** 总曝光 */
        private Integer totalExposure;
        /** 总领取 */
        private Integer totalReceive;
        /** 总使用 */
        private Integer totalUse;
        /** 整体领取率 */
        private BigDecimal overallReceiveRate;
        /** 整体使用率 */
        private BigDecimal overallUseRate;
        /** 总营销成本 */
        private BigDecimal totalCost;
        /** 总营销收益 */
        private BigDecimal totalRevenue;
        /** 整体ROI */
        private BigDecimal overallRoi;
        /** 拉新总数 */
        private Integer totalNewUsers;
    }

    /**
     * 营销项目
     */
    @Data
    public static class MarketingItem {
        private Long marketingId;
        private String marketingName;
        private String marketingType;
        private Integer exposureCount;
        private Integer receiveCount;
        private Integer useCount;
        private BigDecimal receiveRate;
        private BigDecimal useRate;
        private Integer conversionOrderCount;
        private BigDecimal conversionOrderAmount;
        private BigDecimal marketingCost;
        private BigDecimal marketingRevenue;
        private BigDecimal roi;
        private Integer newUserCount;
        private BigDecimal newUserCost;
        private String status;
    }
}
