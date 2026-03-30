package com.im.backend.modules.merchant.operation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * 商户运营数据日报实体
 * Feature #307: Local Merchant Smart Operation Assistant
 * 
 * @author IM Development Team
 * @since 2026-03-30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_operation_daily_report")
public class MerchantOperationDailyReport {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 商户ID
     */
    private Long merchantId;

    /**
     * 统计日期
     */
    private LocalDate reportDate;

    /**
     * 订单总数
     */
    private Integer totalOrders;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 成交订单数
     */
    private Integer completedOrders;

    /**
     * 退款订单数
     */
    private Integer refundOrders;

    /**
     * 退款金额
     */
    private BigDecimal refundAmount;

    /**
     * 访客数
     */
    private Integer visitorCount;

    /**
     * 转化率(%)
     */
    private BigDecimal conversionRate;

    /**
     * 客单价
     */
    private BigDecimal avgOrderValue;

    /**
     * 新增用户数
     */
    private Integer newUsers;

    /**
     * 复购用户数
     */
    private Integer returningUsers;

    /**
     * 好评数
     */
    private Integer positiveReviews;

    /**
     * 差评数
     */
    private Integer negativeReviews;

    /**
     * 平均评分
     */
    private BigDecimal avgRating;

    /**
     * 同行对比数据(JSON)
     */
    private String peerComparisonData;

    /**
     * AI分析建议
     */
    private String aiSuggestions;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标记
     */
    @TableField(fill = FieldFill.INSERT)
    private Integer deleted;
}
