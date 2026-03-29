package com.im.backend.modules.merchant.bi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户画像数据实体
 * 记录到店顾客的地域分布、消费偏好等画像信息
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("merchant_customer_profile")
public class MerchantCustomerProfile {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商户ID */
    private Long merchantId;

    /** 统计日期 */
    private String statsDate;

    /** 用户ID (聚合统计时为null) */
    private Long userId;

    /** 年龄段 */
    private String ageGroup;

    /** 性别 */
    private String gender;

    /** 所在省份 */
    private String province;

    /** 所在城市 */
    private String city;

    /** 所在区县 */
    private String district;

    /** 消费频次 (低/中/高) */
    private String consumptionFrequency;

    /** 消费偏好标签 */
    private String preferenceTags;

    /** 平均消费金额 */
    private BigDecimal avgConsumption;

    /** 最后消费时间 */
    private LocalDateTime lastConsumptionTime;

    /** 到店次数 */
    private Integer visitCount;

    /** 消费金额 */
    private BigDecimal totalConsumption;

    /** 经度 */
    private Double longitude;

    /** 纬度 */
    private Double latitude;

    /** 地理哈希 */
    private String geoHash;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
