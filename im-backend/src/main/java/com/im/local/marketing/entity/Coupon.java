package com.im.local.marketing.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 优惠券实体类
 * 支持LBS地理位置索引，用于附近优惠券搜索
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "coupons")
public class Coupon {
    
    @Id
    @Field(type = FieldType.Keyword)
    private String couponId;
    
    @Field(type = FieldType.Keyword)
    private String templateId;
    
    @Field(type = FieldType.Keyword)
    private String merchantId;
    
    @Field(type = FieldType.Keyword)
    private String merchantName;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "ik_max_word", searchAnalyzer = "ik_smart")
    private String description;
    
    /**
     * 优惠券类型
     * FULL_REDUCTION: 满减券
     * DISCOUNT: 折扣券
     * CASH: 现金券
     * EXCHANGE: 兑换券
     */
    @Field(type = FieldType.Keyword)
    private String couponType;
    
    /**
     * 优惠面额
     * 满减券: 减X元
     * 折扣券: 折扣率(0-1)
     * 现金券: 金额
     */
    @Field(type = FieldType.Double)
    private BigDecimal discountValue;
    
    /**
     * 使用门槛
     * 0: 无门槛
     * >0: 满X元可用
     */
    @Field(type = FieldType.Double)
    private BigDecimal minOrderAmount;
    
    /**
     * 最高优惠金额（折扣券适用）
     */
    @Field(type = FieldType.Double)
    private BigDecimal maxDiscountAmount;
    
    @Field(type = FieldType.Integer)
    private Integer totalQuantity;
    
    @Field(type = FieldType.Integer)
    private Integer remainingQuantity;
    
    @Field(type = FieldType.Integer)
    private Integer claimedCount;
    
    @Field(type = FieldType.Integer)
    private Integer usedCount;
    
    /**
     * 有效期类型
     * FIXED: 固定日期范围
     * DYNAMIC: 领取后X天有效
     */
    @Field(type = FieldType.Keyword)
    private String validityType;
    
    @Field(type = FieldType.Date)
    private LocalDateTime startTime;
    
    @Field(type = FieldType.Date)
    private LocalDateTime endTime;
    
    /**
     * 动态有效期天数
     */
    @Field(type = FieldType.Integer)
    private Integer validityDays;
    
    /**
     * 商户地理位置，用于附近搜索
     */
    @GeoPointField
    private GeoPoint location;
    
    @Field(type = FieldType.Keyword)
    private String address;
    
    @Field(type = FieldType.Keyword)
    private String cityCode;
    
    @Field(type = FieldType.Keyword)
    private String districtId;
    
    /**
     * 适用分类
     */
    @Field(type = FieldType.Keyword)
    private List<String> applicableCategories;
    
    /**
     * 适用商品（空表示全店通用）
     */
    @Field(type = FieldType.Keyword)
    private List<String> applicableProducts;
    
    /**
     * 使用时段限制
     * 格式: "09:00-12:00,14:00-22:00"
     */
    @Field(type = FieldType.Keyword)
    private String useTimeRange;
    
    /**
     * 使用星期限制（空表示全部）
     * 1-7: 周一到周日
     */
    @Field(type = FieldType.Integer)
    private List<Integer> useWeekDays;
    
    /**
     * 优惠券图片
     */
    @Field(type = FieldType.Keyword)
    private String couponImage;
    
    /**
     * 商户logo
     */
    @Field(type = FieldType.Keyword)
    private String merchantLogo;
    
    /**
     * 每人限领数量
     */
    @Field(type = FieldType.Integer)
    private Integer limitPerUser;
    
    /**
     * 每日限领数量
     */
    @Field(type = FieldType.Integer)
    private Integer dailyLimitPerUser;
    
    /**
     * 用户等级限制
     */
    @Field(type = FieldType.Integer)
    private Integer minMemberLevel;
    
    /**
     * 新用户专享
     */
    @Field(type = FieldType.Boolean)
    private Boolean newUserOnly;
    
    /**
     * 首次消费专享
     */
    @Field(type = FieldType.Boolean)
    private Boolean firstOrderOnly;
    
    /**
     * 是否可分享
     */
    @Field(type = FieldType.Boolean)
    private Boolean shareable;
    
    /**
     * 是否可转赠
     */
    @Field(type = FieldType.Boolean)
    private Boolean transferable;
    
    /**
     * 使用说明
     */
    @Field(type = FieldType.Text)
    private String usageInstructions;
    
    /**
     * 扩展字段
     */
    @Field(type = FieldType.Object)
    private Map<String, Object> extraData;
    
    /**
     * 优惠券状态
     * ACTIVE: 可领取
     * PAUSED: 暂停领取
     * EXHAUSTED: 已领完
     * EXPIRED: 已过期
     */
    @Field(type = FieldType.Keyword)
    private String status;
    
    @Field(type = FieldType.Date)
    private LocalDateTime createTime;
    
    @Field(type = FieldType.Date)
    private LocalDateTime updateTime;
    
    @Field(type = FieldType.Keyword)
    private String createBy;
    
    @Field(type = FieldType.Keyword)
    private String updateBy;
    
    /**
     * 热度评分（用于排序）
     * 算法: 领取数*0.3 + 使用数*0.5 + 浏览数*0.2
     */
    @Field(type = FieldType.Double)
    private Double hotScore;
    
    /**
     * 计算热度评分
     */
    public Double calculateHotScore(int viewCount) {
        this.hotScore = claimedCount * 0.3 + usedCount * 0.5 + viewCount * 0.2;
        return this.hotScore;
    }
    
    /**
     * 检查优惠券是否有效
     */
    public boolean isValid() {
        if (!"ACTIVE".equals(status)) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        if (startTime != null && now.isBefore(startTime)) {
            return false;
        }
        if (endTime != null && now.isAfter(endTime)) {
            return false;
        }
        if (remainingQuantity != null && remainingQuantity <= 0) {
            return false;
        }
        return true;
    }
    
    /**
     * 检查当前时间是否可用
     */
    public boolean isUsableAt(LocalDateTime time) {
        // 检查星期限制
        if (useWeekDays != null && !useWeekDays.isEmpty()) {
            int dayOfWeek = time.getDayOfWeek().getValue();
            if (!useWeekDays.contains(dayOfWeek)) {
                return false;
            }
        }
        
        // 检查时段限制
        if (useTimeRange != null && !useTimeRange.isEmpty()) {
            String[] ranges = useTimeRange.split(",");
            int currentMinutes = time.getHour() * 60 + time.getMinute();
            boolean inRange = false;
            for (String range : ranges) {
                String[] parts = range.trim().split("-");
                if (parts.length == 2) {
                    String[] start = parts[0].split(":");
                    String[] end = parts[1].split(":");
                    int startMinutes = Integer.parseInt(start[0]) * 60 + Integer.parseInt(start[1]);
                    int endMinutes = Integer.parseInt(end[0]) * 60 + Integer.parseInt(end[1]);
                    if (currentMinutes >= startMinutes && currentMinutes <= endMinutes) {
                        inRange = true;
                        break;
                    }
                }
            }
            return inRange;
        }
        
        return true;
    }
}
