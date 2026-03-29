package com.im.backend.modules.coupon.dto;

import lombok.Data;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建优惠券请求DTO
 * 
 * 用于商家/平台创建新的优惠券活动
 * 包含完整的优惠券配置信息
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class CreateCouponRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 基础信息 ====================
    
    /**
     * 优惠券名称
     */
    @NotBlank(message = "优惠券名称不能为空")
    @Size(min = 2, max = 50, message = "优惠券名称长度必须在2-50个字符之间")
    private String name;
    
    /**
     * 优惠券描述
     */
    @Size(max = 500, message = "描述长度不能超过500字符")
    private String description;
    
    /**
     * 优惠券类型
     * 1: 满减券
     * 2: 折扣券
     * 3: 代金券
     * 4: 兑换券
     * 5: 运费券
     * 6: 品类券
     */
    @NotNull(message = "优惠券类型不能为空")
    @Min(value = 1, message = "优惠券类型无效")
    @Max(value = 6, message = "优惠券类型无效")
    private Integer couponType;
    
    /**
     * 优惠金额/折扣率
     */
    @NotNull(message = "优惠金额不能为空")
    @DecimalMin(value = "0.01", message = "优惠金额必须大于0")
    @DecimalMax(value = "999999.99", message = "优惠金额超出限制")
    private BigDecimal discountValue;
    
    /**
     * 使用门槛金额
     */
    @DecimalMin(value = "0", message = "门槛金额不能为负数")
    private BigDecimal minSpend = BigDecimal.ZERO;
    
    /**
     * 最高优惠金额 - 折扣券场景
     */
    private BigDecimal maxDiscount;
    
    // ==================== 商户与适用范围 ====================
    
    /**
     * 适用商户ID列表
     */
    private List<Long> applicableMerchantIds;
    
    /**
     * 适用POI列表
     */
    private List<Long> applicablePoiIds;
    
    /**
     * 适用品类列表
     */
    private List<String> applicableCategories;
    
    /**
     * 适用商品列表
     */
    private List<Long> applicableProductIds;
    
    /**
     * 排除商品列表
     */
    private List<Long> excludedProductIds;
    
    /**
     * 是否为平台券
     */
    private Boolean isPlatformCoupon = false;
    
    // ==================== 地理位置限制 ====================
    
    /**
     * 是否启用地理位置限制
     */
    private Boolean geoLimited = false;
    
    /**
     * 中心点经度
     */
    private BigDecimal centerLongitude;
    
    /**
     * 中心点纬度
     */
    private BigDecimal centerLatitude;
    
    /**
     * 有效半径（米）
     */
    @Min(value = 100, message = "有效半径最小100米")
    @Max(value = 50000, message = "有效半径最大50公里")
    private Integer effectiveRadius;
    
    /**
     * 地理围栏ID
     */
    private Long geofenceId;
    
    // ==================== 库存与领取限制 ====================
    
    /**
     * 总库存数量
     * -1表示无限
     */
    @NotNull(message = "库存数量不能为空")
    @Min(value = -1, message = "库存数量无效")
    private Integer totalStock;
    
    /**
     * 每人限领数量
     */
    @Min(value = 1, message = "每人限领至少1张")
    @Max(value = 100, message = "每人限领不能超过100张")
    private Integer perUserLimit = 1;
    
    /**
     * 每日限领数量
     */
    private Integer dailyLimitPerUser;
    
    // ==================== 时间配置 ====================
    
    /**
     * 发放开始时间
     */
    @NotNull(message = "发放开始时间不能为空")
    @Future(message = "发放开始时间必须是未来时间")
    private LocalDateTime issueStartTime;
    
    /**
     * 发放结束时间
     */
    @NotNull(message = "发放结束时间不能为空")
    @Future(message = "发放结束时间必须是未来时间")
    private LocalDateTime issueEndTime;
    
    /**
     * 使用开始时间
     */
    @NotNull(message = "使用开始时间不能为空")
    private LocalDateTime useStartTime;
    
    /**
     * 使用结束时间
     */
    @NotNull(message = "使用结束时间不能为空")
    private LocalDateTime useEndTime;
    
    /**
     * 领取后有效天数
     * 0表示使用固定有效期
     */
    @Min(value = 0, message = "有效天数不能为负数")
    @Max(value = 365, message = "有效天数不能超过365天")
    private Integer validDaysAfterReceive = 0;
    
    /**
     * 适用时段配置
     */
    private ApplicableTimeRange applicableTimeRange;
    
    // ==================== 营销触发配置 ====================
    
    /**
     * 触发场景类型
     * 0: 无触发
     * 1: 地理围栏进入
     * 2: 到店打卡
     * 3: 消费完成后
     * 4: 关注商户后
     * 5: 分享后
     * 6: 生日特权
     */
    private Integer triggerScene = 0;
    
    /**
     * 触发场景配置
     */
    private String triggerConfig;
    
    /**
     * 是否推送通知
     */
    private Boolean pushNotification = false;
    
    /**
     * 推送模板ID
     */
    private Long pushTemplateId;
    
    // ==================== 分享配置 ====================
    
    /**
     * 是否可分享
     */
    private Boolean shareable = false;
    
    /**
     * 分享奖励类型
     * 0: 无
     * 1: 分享者得
     * 2: 被分享者得
     * 3: 双方得
     */
    private Integer shareRewardType = 0;
    
    /**
     * 分享奖励券ID
     */
    private Long shareRewardCouponId;
    
    // ==================== 展示配置 ====================
    
    /**
     * 排序值
     */
    private Integer sortOrder = 0;
    
    /**
     * 是否置顶
     */
    private Boolean isTop = false;
    
    /**
     * 封面图片
     */
    private String coverImage;
    
    /**
     * 详情图片列表
     */
    private List<String> detailImages;
    
    /**
     * 标签列表
     */
    private List<String> tags;
    
    // ==================== 其他配置 ====================
    
    /**
     * 是否可与其他优惠叠加
     */
    private Boolean stackable = false;
    
    /**
     * 扩展字段
     */
    private String extFields;
    
    // ==================== 内部类 ====================
    
    /**
     * 适用时段配置
     */
    @Data
    public static class ApplicableTimeRange {
        /**
         * 适用星期 [1,2,3,4,5,6,7]
         */
        private List<Integer> weekdays;
        
        /**
         * 适用时段 ["10:00-14:00", "17:00-22:00"]
         */
        private List<String> hours;
    }
}
