package com.im.local.marketing.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 优惠券模板实体
 * 用于批量生成优惠券
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "coupon_templates")
public class CouponTemplate {
    
    @Id
    private String templateId;
    
    private String merchantId;
    
    private String merchantName;
    
    private String title;
    
    private String description;
    
    /**
     * 模板类型
     * SINGLE: 单次发放
     * BATCH: 批量生成
     * EVENT: 活动触发
     */
    private String templateType;
    
    /**
     * 优惠券类型
     */
    private String couponType;
    
    private BigDecimal discountValue;
    
    private BigDecimal minOrderAmount;
    
    private BigDecimal maxDiscountAmount;
    
    /**
     * 发行总量
     */
    private Integer totalQuantity;
    
    /**
     * 已生成数量
     */
    private Integer generatedCount;
    
    /**
     * 已领取数量
     */
    private Integer claimedCount;
    
    /**
     * 已使用数量
     */
    private Integer usedCount;
    
    private String validityType;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    private Integer validityDays;
    
    /**
     * 适用分类
     */
    private List<String> applicableCategories;
    
    /**
     * 适用商品
     */
    private List<String> applicableProducts;
    
    private String useTimeRange;
    
    private List<Integer> useWeekDays;
    
    private String couponImage;
    
    /**
     * 领取限制
     */
    private Integer limitPerUser;
    
    private Integer dailyLimitPerUser;
    
    private Integer minMemberLevel;
    
    private Boolean newUserOnly;
    
    private Boolean firstOrderOnly;
    
    private Boolean shareable;
    
    private Boolean transferable;
    
    private String usageInstructions;
    
    /**
     * 发行渠道
     * APP: 应用内发放
     * MINI_PROGRAM: 小程序发放
     * PUSH: 推送发放
     * QR_CODE: 二维码领取
     * LINK: 链接领取
     * EVENT: 活动触发
     */
    private List<String> issueChannels;
    
    /**
     * LBS地理位置限制
     * 为空表示不限制
     */
    private GeoLimit geoLimit;
    
    /**
     * 地理限制配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeoLimit {
        /**
         * 限制类型
         * RADIUS: 半径范围
         * CITY: 城市范围
         * DISTRICT: 区县范围
         * POI: 指定POI
         */
        private String limitType;
        
        /**
         * 中心经度（RADIUS类型）
         */
        private Double centerLng;
        
        /**
         * 中心纬度（RADIUS类型）
         */
        private Double centerLat;
        
        /**
         * 半径（米）
         */
        private Integer radius;
        
        /**
         * 城市代码
         */
        private String cityCode;
        
        /**
         * 区县ID
         */
        private String districtId;
        
        /**
         * POI ID列表
         */
        private List<String> poiIds;
    }
    
    /**
     * 触发规则（EVENT类型）
     */
    private TriggerRule triggerRule;
    
    /**
     * 触发规则配置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TriggerRule {
        /**
         * 触发事件类型
         * REGISTER: 用户注册
         * FIRST_ORDER: 首单完成
         * BIRTHDAY: 生日
         * LOGIN: 每日登录
         * SHARE: 分享成功
         * INVITE: 邀请好友
         * PAYMENT: 支付完成
         * GEO_FENCE: 进入地理围栏
         */
        private String eventType;
        
        /**
         * 触发条件参数
         */
        private Map<String, Object> conditions;
        
        /**
         * 触发次数限制
         */
        private Integer triggerLimit;
        
        /**
         * 冷却时间（分钟）
         */
        private Integer cooldownMinutes;
    }
    
    private Map<String, Object> extraData;
    
    /**
     * 模板状态
     * DRAFT: 草稿
     * ACTIVE: 生效中
     * PAUSED: 已暂停
     * EXPIRED: 已过期
     * EXHAUSTED: 已领完
     */
    private String status;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
    
    private String createBy;
    
    private String updateBy;
    
    /**
     * 检查是否可以生成新的优惠券
     */
    public boolean canGenerate() {
        if (!"ACTIVE".equals(status)) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (endTime != null && now.isAfter(endTime)) {
            return false;
        }
        if (generatedCount >= totalQuantity) {
            return false;
        }
        return true;
    }
    
    /**
     * 生成优惠券ID
     */
    public String generateCouponId() {
        return String.format("CP%s%06d", 
            templateId.substring(2), 
            generatedCount + 1);
    }
}
