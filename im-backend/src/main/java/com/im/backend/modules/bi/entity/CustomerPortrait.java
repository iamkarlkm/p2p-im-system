package com.im.backend.modules.bi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户画像实体
 * 存储顾客画像分析数据
 */
@Data
@TableName("customer_portrait")
public class CustomerPortrait {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 用户ID */
    private Long userId;

    /** 地域编码 */
    private String regionCode;

    /** 地域名称 */
    private String regionName;

    /** 消费频次 */
    private Integer consumptionFrequency;

    /** 累计消费金额 */
    private BigDecimal totalConsumption;

    /** 平均客单价 */
    private BigDecimal avgOrderValue;

    /** 最后消费时间 */
    private LocalDateTime lastConsumptionTime;

    /** 首次消费时间 */
    private LocalDateTime firstConsumptionTime;

    /** 偏好分类标签(JSON) */
    private String preferenceTags;

    /** 用户价值分层(RFM) */
    private String rfmSegment;

    /** 生命周期阶段 */
    private String lifecycleStage;

    /** 创建时间 */
    private LocalDateTime createdAt;

    /** 更新时间 */
    private LocalDateTime updatedAt;
}
