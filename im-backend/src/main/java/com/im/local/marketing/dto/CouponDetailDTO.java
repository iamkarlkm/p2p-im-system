package com.im.local.marketing.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 优惠券详情响应DTO
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CouponDetailDTO {
    
    private String couponId;
    
    private String templateId;
    
    private String merchantId;
    
    private String merchantName;
    
    private String title;
    
    private String description;
    
    /**
     * 优惠券类型
     */
    private String couponType;
    
    /**
     * 优惠券类型描述
     */
    private String couponTypeDesc;
    
    /**
     * 优惠面额
     */
    private BigDecimal discountValue;
    
    /**
     * 使用门槛
     */
    private BigDecimal minOrderAmount;
    
    /**
     * 最高优惠金额
     */
    private BigDecimal maxDiscountAmount;
    
    /**
     * 总数量
     */
    private Integer totalQuantity;
    
    /**
     * 剩余数量
     */
    private Integer remainingQuantity;
    
    /**
     * 已领取数量
     */
    private Integer claimedCount;
    
    /**
     * 已使用数量
     */
    private Integer usedCount;
    
    /**
     * 有效期类型
     */
    private String validityType;
    
    /**
     * 有效期开始
     */
    private LocalDateTime startTime;
    
    /**
     * 有效期结束
     */
    private LocalDateTime endTime;
    
    /**
     * 动态有效期天数
     */
    private Integer validityDays;
    
    /**
     * 有效期描述
     */
    private String validityDesc;
    
    /**
     * 商户位置
     */
    private Double merchantLng;
    
    private Double merchantLat;
    
    private String address;
    
    private String cityCode;
    
    private String districtId;
    
    /**
     * 距离（附近优惠券使用）
     */
    private Double distance;
    
    /**
     * 距离描述
     */
    private String distanceDesc;
    
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
     * 商户logo
     */
    private String merchantLogo;
    
    /**
     * 每人限领数量
     */
    private Integer limitPerUser;
    
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
     * 优惠券状态
     */
    private String status;
    
    /**
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 是否已领取
     */
    private Boolean alreadyClaimed;
    
    /**
     * 当前用户已领取数量
     */
    private Integer userClaimedCount;
    
    /**
     * 是否可领取
     */
    private Boolean canClaim;
    
    /**
     * 不可领取原因
     */
    private String cannotClaimReason;
    
    /**
     * 热度评分
     */
    private Double hotScore;
    
    /**
     * 扩展字段
     */
    private Map<String, Object> extraData;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
