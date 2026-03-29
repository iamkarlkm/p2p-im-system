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
 * 商家顾客地域分布实体
 * 记录顾客来源地域分布数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_customer_geo_distribution")
public class MerchantCustomerGeoDistribution {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 商户ID
     */
    @TableField("merchant_id")
    private Long merchantId;
    
    /**
     * 统计日期
     */
    @TableField("report_date")
    private LocalDate reportDate;
    
    /**
     * 区域类型: province/city/district
     */
    @TableField("region_type")
    private String regionType;
    
    /**
     * 区域代码
     */
    @TableField("region_code")
    private String regionCode;
    
    /**
     * 区域名称
     */
    @TableField("region_name")
    private String regionName;
    
    /**
     * 顾客数量
     */
    @TableField("customer_count")
    private Integer customerCount;
    
    /**
     * 订单数量
     */
    @TableField("order_count")
    private Integer orderCount;
    
    /**
     * 消费金额
     */
    @TableField("revenue")
    private BigDecimal revenue;
    
    /**
     * 平均客单价
     */
    @TableField("avg_order_value")
    private BigDecimal avgOrderValue;
    
    /**
     * 新客数量
     */
    @TableField("new_customer_count")
    private Integer newCustomerCount;
    
    /**
     * 老客数量
     */
    @TableField("returning_customer_count")
    private Integer returningCustomerCount;
    
    /**
     * 区域占比(%)
     */
    @TableField("percentage")
    private BigDecimal percentage;
    
    /**
     * 环比增长率
     */
    @TableField("growth_rate")
    private BigDecimal growthRate;
    
    /**
     * 中心经度
     */
    @TableField("center_longitude")
    private BigDecimal centerLongitude;
    
    /**
     * 中心纬度
     */
    @TableField("center_latitude")
    private BigDecimal centerLatitude;
    
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
