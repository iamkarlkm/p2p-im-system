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
 * 商家用户画像分析实体
 * 记录顾客消费行为画像数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("merchant_customer_portrait")
public class MerchantCustomerPortrait {
    
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
     * 画像类型
     */
    @TableField("portrait_type")
    private String portraitType;
    
    /**
     * 画像维度值
     */
    @TableField("dimension_value")
    private String dimensionValue;
    
    /**
     * 维度名称
     */
    @TableField("dimension_name")
    private String dimensionName;
    
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
     * 总消费金额
     */
    @TableField("total_revenue")
    private BigDecimal totalRevenue;
    
    /**
     * 平均消费频次(次/月)
     */
    @TableField("avg_frequency")
    private BigDecimal avgFrequency;
    
    /**
     * 平均客单价
     */
    @TableField("avg_order_value")
    private BigDecimal avgOrderValue;
    
    /**
     * 占比(%)
     */
    @TableField("percentage")
    private BigDecimal percentage;
    
    /**
     * 画像分类
     */
    @TableField("category")
    private String category;
    
    /**
     * 数据标签(JSON)
     */
    @TableField("tags")
    private String tags;
    
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
    
    // 画像类型常量
    public static final String TYPE_AGE = "AGE";                    // 年龄段
    public static final String TYPE_GENDER = "GENDER";              // 性别
    public static final String TYPE_CONSUMPTION_LEVEL = "CONSUMPTION_LEVEL"; // 消费水平
    public static final String TYPE_PREFERENCE = "PREFERENCE";      // 消费偏好
    public static final String TYPE_VISIT_TIME = "VISIT_TIME";      // 到访时段
    public static final String TYPE_LOYALTY = "LOYALTY";            // 忠诚度
}
