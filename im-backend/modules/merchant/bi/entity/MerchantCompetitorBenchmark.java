package com.im.backend.modules.merchant.bi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 竞品对标数据实体
 * 记录商户与竞品/商圈平均水平的对比数据
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("merchant_competitor_benchmark")
public class MerchantCompetitorBenchmark {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 对标类型 (DISTRICT-商圈/CATEGORY-品类/TOP-头部商户) */
    private String benchmarkType;

    /** 对标对象ID */
    private String benchmarkTargetId;

    /** 对标对象名称 */
    private String benchmarkTargetName;

    /** 商户排名 */
    private Integer merchantRank;

    /** 总商户数 */
    private Integer totalMerchantCount;

    /** 商户评分 */
    private BigDecimal merchantRating;

    /** 平均评分 */
    private BigDecimal avgRating;

    /** 商户月销量 */
    private Integer merchantMonthlySales;

    /** 平均月销量 */
    private Integer avgMonthlySales;

    /** 商户客单价 */
    private BigDecimal merchantAvgOrderValue;

    /** 平均客单价 */
    private BigDecimal avgOrderValue;

    /** 商户访客数 */
    private Integer merchantVisitorCount;

    /** 平均访客数 */
    private Integer avgVisitorCount;

    /** 综合得分 */
    private BigDecimal compositeScore;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
