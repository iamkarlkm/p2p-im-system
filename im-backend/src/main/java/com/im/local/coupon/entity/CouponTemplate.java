package com.im.local.coupon.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板实体类
 * 用于快速创建优惠券实例
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "coupon_templates")
public class CouponTemplate {
    
    @Id
    private String id;
    
    /** 模板名称 */
    private String name;
    
    /** 模板描述 */
    private String description;
    
    /** 优惠券类型 */
    @Indexed
    private String couponType;
    
    /** 模板分类 */
    private String category;
    
    /** 面额/折扣值 */
    private BigDecimal faceValue;
    
    /** 满减门槛金额 */
    private BigDecimal minSpend;
    
    /** 折扣比例 */
    private BigDecimal discountRate;
    
    /** 最高优惠金额 */
    private BigDecimal maxDiscount;
    
    /** 默认发行总量 */
    private Integer defaultTotalQuantity;
    
    /** 默认每人限领数量 */
    private Integer defaultLimitPerUser;
    
    /** 默认有效天数 */
    private Integer defaultValidDays;
    
    /** 使用规则模板 */
    private List<String> usageRuleTemplates;
    
    /** 默认封面图 */
    private String defaultCoverImage;
    
    /** 模板配置JSON */
    private String templateConfig;
    
    /** 是否仅限新用户 */
    private Boolean newUserOnly;
    
    /** 是否仅限会员 */
    private Boolean memberOnly;
    
    /** 最低会员等级 */
    private Integer minMemberLevel;
    
    /** 状态: ACTIVE-可用, DISABLED-已禁用 */
    @Indexed
    private String status;
    
    /** 使用次数统计 */
    private Integer usageCount;
    
    /** 创建时间 */
    private LocalDateTime createdAt;
    
    /** 更新时间 */
    private LocalDateTime updatedAt;
    
    /** 创建人 */
    private String createdBy;
    
    /** 扩展配置 */
    private Map<String, Object> extraConfig;
}
