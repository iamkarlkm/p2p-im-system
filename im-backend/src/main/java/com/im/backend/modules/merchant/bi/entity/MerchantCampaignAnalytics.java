package com.im.backend.modules.merchant.bi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 商家营销活动效果分析实体
 * 记录营销活动数据与转化漏斗
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_campaign_analytics")
public class MerchantCampaignAnalytics {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    @TableField("merchant_id")
    private Long merchantId;
    
    /**
     * 活动ID
     */
    @TableField("campaign_id")
    private Long campaignId;
    
    /**
     * 活动名称
     */
    @TableField("campaign_name")
    private String campaignName;
    
    /**
     * 活动类型
     */
    @TableField("campaign_type")
    private String campaignType;
    
    /**
     * 统计日期
     */
    @TableField("report_date")
    private LocalDate reportDate;
    
    /**
     * 曝光次数
     */
    @TableField("impressions")
    private Integer impressions;
    
    /**
     * 点击次数
     */
    @TableField("clicks")
    private Integer clicks;
    
    /**
     * 点击率(%)
     */
    @TableField("ctr")
    private BigDecimal ctr;
    
    /**
     * 参与人数
     */
    @TableField("participants")
    private Integer participants;
    
    /**
     * 参与率(%)
     */
    @TableField("participation_rate")
    private BigDecimal participationRate;
    
    /**
     * 转化人数
     */
    @TableField("conversions")
    private Integer conversions;
    
    /**
     * 转化率(%)
     */
    @TableField("conversion_rate")
    private BigDecimal conversionRate;
    
    /**
     * 转化金额
     */
    @TableField("conversion_revenue")
    private BigDecimal conversionRevenue;
    
    /**
     * 分享次数
     */
    @TableField("shares")
    private Integer shares;
    
    /**
     * 新客数量
     */
    @TableField("new_customers")
    private Integer newCustomers;
    
    /**
     * 老客数量
     */
    @TableField("returning_customers")
    private Integer returningCustomers;
    
    /**
     * 活动成本
     */
    @TableField("campaign_cost")
    private BigDecimal campaignCost;
    
    /**
     * 活动投入产出比 ROI
     */
    @TableField("roi")
    private BigDecimal roi;
    
    /**
     * 单笔获客成本 CPA
     */
    @TableField("cpa")
    private BigDecimal cpa;
    
    /**
     * 漏斗数据 - 曝光
     */
    @TableField("funnel_impressions")
    private Integer funnelImpressions;
    
    /**
     * 漏斗数据 - 兴趣
     */
    @TableField("funnel_interest")
    private Integer funnelInterest;
    
    /**
     * 漏斗数据 - 决策
     */
    @TableField("funnel_decision")
    private Integer funnelDecision;
    
    /**
     * 漏斗数据 - 行动
     */
    @TableField("funnel_action")
    private Integer funnelAction;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 是否删除
     */
    @TableLogic
    @TableField("deleted")
    private Boolean deleted;
}
