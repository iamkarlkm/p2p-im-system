package com.im.backend.modules.merchant.bi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 转化漏斗数据实体
 * 记录营销活动的转化漏斗数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("merchant_conversion_funnel")
public class MerchantConversionFunnel {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 漏斗类型 (MARKETING-营销/ORDER-订单/VISIT-访问) */
    private String funnelType;

    /** 关联ID */
    private Long relatedId;

    /** 统计日期 */
    private String statsDate;

    /** 曝光人数 */
    private Integer exposureUsers;

    /** 点击人数 */
    private Integer clickUsers;

    /** 点击率 */
    private BigDecimal clickRate;

    /** 访问人数 */
    private Integer visitUsers;

    /** 访问率 */
    private BigDecimal visitRate;

    /** 下单人数 */
    private Integer orderUsers;

    /** 下单转化率 */
    private BigDecimal orderConversionRate;

    /** 支付人数 */
    private Integer payUsers;

    /** 支付转化率 */
    private BigDecimal payConversionRate;

    /** 完成人数 */
    private Integer completeUsers;

    /** 完成转化率 */
    private BigDecimal completeConversionRate;

    /** 总转化率 */
    private BigDecimal totalConversionRate;

    /** 流失人数 (曝光-完成) */
    private Integer churnUsers;

    /** 流失率 */
    private BigDecimal churnRate;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
