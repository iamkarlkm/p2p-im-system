package com.im.service.bi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 竞品对标数据实体
 * 存储商圈竞品分析数据
 */
@Data
@TableName("competitor_benchmark")
public class CompetitorBenchmark {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 商圈ID */
    private Long businessDistrictId;

    /** 所属分类 */
    private String category;

    /** 商户排名 */
    private Integer ranking;

    /** 商圈商户总数 */
    private Integer totalMerchants;

    /** 商户评分 */
    private BigDecimal merchantRating;

    /** 商圈平均评分 */
    private BigDecimal avgRating;

    /** 商户月销量 */
    private Integer merchantMonthlySales;

    /** 商圈平均销量 */
    private Integer avgMonthlySales;

    /** 商户客单价 */
    private BigDecimal merchantAvgPrice;

    /** 商圈平均客单价 */
    private BigDecimal avgPrice;

    /** 评价数量 */
    private Integer reviewCount;

    /** 好评率 */
    private BigDecimal positiveRate;

    /** 统计时间 */
    private LocalDateTime statTime;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
