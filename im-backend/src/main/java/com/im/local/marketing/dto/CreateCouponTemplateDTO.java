package com.im.local.marketing.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;
import javax.validation.constraints.Max;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 创建优惠券模板请求DTO
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCouponTemplateDTO {
    
    @NotBlank(message = "商户ID不能为空")
    private String merchantId;
    
    @NotBlank(message = "优惠券标题不能为空")
    private String title;
    
    private String description;
    
    /**
     * 优惠券类型
     */
    @NotBlank(message = "优惠券类型不能为空")
    private String couponType;
    
    /**
     * 优惠面额
     */
    @NotNull(message = "优惠面额不能为空")
    @Min(value = 0, message = "优惠面额不能小于0")
    private BigDecimal discountValue;
    
    /**
     * 使用门槛
     */
    @NotNull(message = "使用门槛不能为空")
    @Min(value = 0, message = "使用门槛不能小于0")
    private BigDecimal minOrderAmount;
    
    /**
     * 最高优惠金额（折扣券适用）
     */
    private BigDecimal maxDiscountAmount;
    
    /**
     * 发行总量
     */
    @NotNull(message = "发行总量不能为空")
    @Min(value = 1, message = "发行总量至少为1")
    private Integer totalQuantity;
    
    /**
     * 有效期类型
     * FIXED: 固定日期
     * DYNAMIC: 动态天数
     */
    @NotBlank(message = "有效期类型不能为空")
    private String validityType;
    
    /**
     * 固定有效期开始
     */
    private LocalDateTime startTime;
    
    /**
     * 固定有效期结束
     */
    private LocalDateTime endTime;
    
    /**
     * 动态有效期天数
     */
    @Min(value = 1, message = "有效期天数至少为1")
    private Integer validityDays;
    
    /**
     * 适用分类
     */
    private List<String> applicableCategories;
    
    /**
     * 适用商品
     */
    private List<String> applicableProducts;
    
    /**
     * 使用时段限制
     */
    private String useTimeRange;
    
    /**
     * 使用星期限制
     */
    private List<Integer> useWeekDays;
    
    /**
     * 优惠券图片
     */
    private String couponImage;
    
    /**
     * 每人限领数量
     */
    @NotNull(message = "每人限领数量不能为空")
    @Min(value = 1, message = "每人限领至少1张")
    private Integer limitPerUser;
    
    /**
     * 每日限领数量
     */
    private Integer dailyLimitPerUser;
    
    /**
     * 用户等级限制
     */
    private Integer minMemberLevel;
    
    /**
     * 新用户专享
     */
    private Boolean newUserOnly;
    
    /**
     * 首次消费专享
     */
    private Boolean firstOrderOnly;
    
    /**
     * 是否可分享
     */
    private Boolean shareable;
    
    /**
     * 是否可转赠
     */
    private Boolean transferable;
    
    /**
     * 使用说明
     */
    private String usageInstructions;
    
    /**
     * 发行渠道
     */
    private List<String> issueChannels;
    
    /**
     * 地理限制
     */
    private GeoLimitDTO geoLimit;
    
    /**
     * 触发规则
     */
    private TriggerRuleDTO triggerRule;
    
    /**
     * 扩展字段
     */
    private Map<String, Object> extraData;
    
    /**
     * 地理限制DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoLimitDTO {
        private String limitType;
        private Double centerLng;
        private Double centerLat;
        private Integer radius;
        private String cityCode;
        private String districtId;
        private List<String> poiIds;
    }
    
    /**
     * 触发规则DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TriggerRuleDTO {
        private String eventType;
        private Map<String, Object> conditions;
        private Integer triggerLimit;
        private Integer cooldownMinutes;
    }
}
