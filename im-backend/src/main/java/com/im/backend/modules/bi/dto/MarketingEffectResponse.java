package com.im.backend.modules.bi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 营销效果分析响应DTO
 */
@Data
public class MarketingEffectResponse {

    /** 活动总数 */
    private Integer totalCampaigns;

    /** 进行中活动数 */
    private Integer activeCampaigns;

    /** 总曝光人数 */
    private Integer totalExposeCount;

    /** 总领取人数 */
    private Integer totalClaimCount;

    /** 总使用人数 */
    private Integer totalUsedCount;

    /** 整体领取率 */
    private BigDecimal overallClaimRate;

    /** 整体使用率 */
    private BigDecimal overallUseRate;

    /** 总转化金额 */
    private BigDecimal totalConvertAmount;

    /** 整体ROI */
    private BigDecimal overallRoi;

    /** 活动效果列表 */
    private List<CampaignEffectItem> campaignEffects;

    /** 转化漏斗 */
    private ConversionFunnel conversionFunnel;

    @Data
    public static class CampaignEffectItem {
        private Long campaignId;
        private String campaignName;
        private String campaignType;
        private Integer exposeCount;
        private Integer claimCount;
        private Integer usedCount;
        private BigDecimal claimRate;
        private BigDecimal useRate;
        private BigDecimal convertAmount;
        private BigDecimal roi;
    }

    @Data
    public static class ConversionFunnel {
        private Integer exposeCount;
        private Integer claimCount;
        private Integer usedCount;
        private Integer orderCount;
        private BigDecimal claimConversion;
        private BigDecimal useConversion;
        private BigDecimal orderConversion;
    }
}
