package com.im.service.bi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 营销活动效果实体
 * 存储营销活动数据分析
 */
@Data
@TableName("marketing_campaign_effect")
public class MarketingCampaignEffect {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 活动ID */
    private Long campaignId;

    /** 活动名称 */
    private String campaignName;

    /** 活动类型 */
    private String campaignType;

    /** 统计日期 */
    private LocalDate statDate;

    /** 曝光人数 */
    private Integer exposeCount;

    /** 领取人数 */
    private Integer claimCount;

    /** 使用人数 */
    private Integer usedCount;

    /** 领取率 */
    private BigDecimal claimRate;

    /** 使用率 */
    private BigDecimal useRate;

    /** 转化订单数 */
    private Integer convertOrderCount;

    /** 转化金额 */
    private BigDecimal convertAmount;

    /** ROI */
    private BigDecimal roi;

    /** 活动成本 */
    private BigDecimal campaignCost;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
