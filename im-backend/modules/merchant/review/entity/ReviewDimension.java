package com.im.backend.modules.merchant.review.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 评价维度实体类
 * 支持多维度细评（口味/环境/服务/性价比等）
 * @author IM Development Team
 * @version 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("review_dimension")
public class ReviewDimension implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 维度ID */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /** 评价ID */
    private Long reviewId;

    /** 商户ID */
    private Long merchantId;

    /** POI ID */
    private Long poiId;

    /** 维度编码 */
    private String dimensionCode;

    /** 维度名称 */
    private String dimensionName;

    /** 维度评分（1-5分，支持半星） */
    private BigDecimal rating;

    /** 维度权重（用于计算综合评分） */
    private BigDecimal weight;

    /** 维度描述/标签 */
    private String tags;

    /** 维度顺序 */
    private Integer sortOrder;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 删除标记 */
    @TableLogic
    private Integer deleted;

    /**
     * 计算加权评分
     */
    public BigDecimal getWeightedRating() {
        if (rating == null || weight == null) {
            return BigDecimal.ZERO;
        }
        return rating.multiply(weight);
    }

    /**
     * 获取评分标签
     */
    public String getRatingLabel() {
        if (rating == null) {
            return "未评分";
        }
        double score = rating.doubleValue();
        if (score >= 4.5) {
            return "超赞";
        } else if (score >= 4.0) {
            return "满意";
        } else if (score >= 3.0) {
            return "一般";
        } else if (score >= 2.0) {
            return "失望";
        } else {
            return "极差";
        }
    }

    // ============ 静态常量：预设维度编码 ============

    /** 餐饮类商户维度 */
    public static final String DIM_TASTE = "taste";           // 口味
    public static final String DIM_ENVIRONMENT = "environment"; // 环境
    public static final String DIM_SERVICE = "service";       // 服务
    public static final String DIM_COST_PERFORMANCE = "cost_performance"; // 性价比

    /** 酒店类商户维度 */
    public static final String DIM_CLEANLINESS = "cleanliness"; // 卫生
    public static final String DIM_FACILITY = "facility";       // 设施
    public static final String DIM_LOCATION = "location";       // 位置
    public static final String DIM_BREAKFAST = "breakfast";     // 早餐

    /** 娱乐类商户维度 */
    public static final String DIM_FUN = "fun";                 // 趣味性
    public static final String DIM_SAFETY = "safety";           // 安全性
    public static final String DIM_WAITING = "waiting";         // 排队体验
    public static final String DIM_VALUE = "value";             // 性价比

    /** 通用维度权重（餐饮行业默认） */
    public static final BigDecimal DEFAULT_WEIGHT_TASTE = new BigDecimal("0.35");
    public static final BigDecimal DEFAULT_WEIGHT_ENVIRONMENT = new BigDecimal("0.25");
    public static final BigDecimal DEFAULT_WEIGHT_SERVICE = new BigDecimal("0.25");
    public static final BigDecimal DEFAULT_WEIGHT_COST = new BigDecimal("0.15");
}
