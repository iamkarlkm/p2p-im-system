package com.im.backend.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 优惠券实体类 - 本地生活精准营销系统核心实体
 * 
 * 功能说明:
 * 1. 支持多种优惠券类型（满减券、折扣券、代金券、兑换券）
 * 2. 支持LBS地理位置限制（经纬度+半径范围）
 * 3. 支持多维度使用限制（商户/品类/商品/时段）
 * 4. 支持库存管理与领取限制
 * 5. 支持营销场景触发（地理围栏/到店/消费后）
 * 
 * 技术要点:
 * - Redis Geo用于附近优惠券搜索
 * - Lua脚本保证库存扣减原子性
 * - 地理围栏触发优惠券发放
 * 
 * 目标指标:
 * - 优惠券领取响应 < 100ms
 * - 库存扣减并发支持 10万 TPS
 * - 地理围栏触发延迟 < 500ms
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("im_coupon")
public class Coupon implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 基础字段 ====================
    
    /**
     * 优惠券ID - 全局唯一标识符
     * 使用雪花算法生成，确保分布式环境下唯一性
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 优惠券编码 - 用户展示和领取使用的短码
     * 格式: CP + 8位随机字母数字组合
     * 示例: CPA7B3C9D2
     */
    private String couponCode;
    
    /**
     * 优惠券名称 - 展示给用户看的标题
     * 限制: 2-50个字符
     * 示例: "新人专享满100减20券"
     */
    private String name;
    
    /**
     * 优惠券描述 - 详细使用说明
     * 支持富文本格式，可包含使用规则、有效期等
     */
    private String description;
    
    /**
     * 优惠券类型
     * 1: 满减券 - 满X元减Y元
     * 2: 折扣券 - 享受X折优惠
     * 3: 代金券 - 直接抵扣X元
     * 4: 兑换券 - 兑换指定商品/服务
     * 5: 运费券 - 抵扣运费
     * 6: 品类券 - 限定品类使用
     */
    private Integer couponType;
    
    /**
     * 优惠金额/折扣率 - 根据类型不同含义不同
     * 满减券: 减免金额（如20.00）
     * 折扣券: 折扣率（如0.85表示85折）
     * 代金券: 抵扣金额
     * 兑换券: 兑换商品ID
     */
    private BigDecimal discountValue;
    
    /**
     * 使用门槛金额 - 满多少元可用
     * 0表示无门槛
     * 满减券必须大于discountValue
     */
    private BigDecimal minSpend;
    
    /**
     * 最高优惠金额 - 折扣券场景使用
     * 限制折扣后的最大优惠金额
     * 0表示不限制
     */
    private BigDecimal maxDiscount;
    
    // ==================== 商户与POI关联 ====================
    
    /**
     * 创建商户ID - 发行优惠券的商户
     * 平台券则为0
     */
    private Long merchantId;
    
    /**
     * 商户名称 - 冗余存储，避免联表查询
     */
    private String merchantName;
    
    /**
     * 适用POI列表 - JSON数组格式存储
     * [12345, 12346, 12347] 表示限定这些POI可用
     * 空数组表示所有POI通用
     */
    private String applicablePoiIds;
    
    /**
     * 适用品类列表 - JSON数组格式存储
     * ["food", "drink", "entertainment"]
     * 空数组表示全品类通用
     */
    private String applicableCategories;
    
    /**
     * 适用商品ID列表 - JSON数组格式存储
     * 限定特定商品可用
     */
    private String applicableProductIds;
    
    /**
     * 排除商品ID列表 - JSON数组格式存储
     * 这些商品不可使用本券
     */
    private String excludedProductIds;
    
    // ==================== LBS地理位置限制 ====================
    
    /**
     * 地理位置限制开关
     * true: 启用地理位置限制
     * false: 无地理位置限制
     */
    private Boolean geoLimited;
    
    /**
     * 中心点经度 - 地理围栏中心
     * 用于"附近优惠券"搜索和地理围栏触发
     */
    private BigDecimal centerLongitude;
    
    /**
     * 中心点纬度 - 地理围栏中心
     */
    private BigDecimal centerLatitude;
    
    /**
     * 有效半径 - 单位：米
     * 用户必须在此半径范围内才可领取/使用
     * 用于LBS精准营销
     */
    private Integer effectiveRadius;
    
    /**
     * GeoHash编码 - 用于附近搜索索引
     * 根据centerLatitude/centerLongitude计算
     * 精度7位，约150米范围
     */
    private String geoHash;
    
    /**
     * 地理围栏ID - 关联预设围栏
     * 复杂多边形围栏使用此字段关联
     */
    private Long geofenceId;
    
    // ==================== 库存与领取限制 ====================
    
    /**
     * 总库存数量 - 优惠券发行总量
     * -1表示无限库存
     */
    private Integer totalStock;
    
    /**
     * 剩余库存 - 实时剩余可领取数量
     * 使用Redis Lua脚本原子扣减
     */
    private Integer remainingStock;
    
    /**
     * 每人限领数量 - 防止恶意刷券
     * 0表示不限制
     * 建议设置1-5张
     */
    private Integer perUserLimit;
    
    /**
     * 每日限领数量 - 分时段控制
     * 0表示不限制
     * 用于制造稀缺感和持续性营销
     */
    private Integer dailyLimitPerUser;
    
    /**
     * 已领取数量 - 统计字段
     */
    private Integer receivedCount;
    
    /**
     * 已使用数量 - 统计字段
     */
    private Integer usedCount;
    
    // ==================== 时间配置 ====================
    
    /**
     * 发放开始时间 - 领取有效期开始
     */
    private LocalDateTime issueStartTime;
    
    /**
     * 发放结束时间 - 领取有效期结束
     */
    private LocalDateTime issueEndTime;
    
    /**
     * 使用开始时间 - 券可用时间开始
     * 可与发放时间不同，实现预售模式
     */
    private LocalDateTime useStartTime;
    
    /**
     * 使用结束时间 - 券过期时间
     */
    private LocalDateTime useEndTime;
    
    /**
     * 领取后有效天数 - 动态有效期
     * 从用户领取时刻开始计算
     * 0表示使用固定有效期
     */
    private Integer validDaysAfterReceive;
    
    /**
     * 适用时段限制 - JSON格式
     * {"weekdays": [1,2,3,4,5], "hours": ["10:00-14:00", "17:00-22:00"]}
     * 空表示全天可用
     */
    private String applicableTimeRange;
    
    // ==================== 营销场景触发配置 ====================
    
    /**
     * 触发场景类型
     * 0: 无触发，主动领取
     * 1: 地理围栏进入触发
     * 2: 到店打卡触发
     * 3: 消费完成后触发
     * 4: 关注商户后触发
     * 5: 分享后触发
     * 6: 生日特权触发
     */
    private Integer triggerScene;
    
    /**
     * 触发场景配置 - JSON格式
     * 根据不同场景存储不同配置参数
     */
    private String triggerConfig;
    
    /**
     * 是否推送通知
     * 触发时是否发送App Push/短信通知
     */
    private Boolean pushNotification;
    
    /**
     * 推送模板ID
     * 关联消息模板
     */
    private Long pushTemplateId;
    
    // ==================== 分享裂变配置 ====================
    
    /**
     * 是否支持分享
     * true: 用户可分享给好友
     */
    private Boolean shareable;
    
    /**
     * 分享后奖励类型
     * 0: 无奖励
     * 1: 分享者得券
     * 2: 被分享者得券
     * 3: 双方得券
     */
    private Integer shareRewardType;
    
    /**
     * 分享奖励优惠券ID
     * 分享成功后发放的优惠券
     */
    private Long shareRewardCouponId;
    
    // ==================== 显示与排序配置 ====================
    
    /**
     * 权重/排序值 - 越大越靠前
     * 用于列表展示排序
     */
    private Integer sortOrder;
    
    /**
     * 是否置顶展示
     */
    private Boolean isTop;
    
    /**
     * 封面图片URL
     * 列表展示用缩略图
     */
    private String coverImage;
    
    /**
     * 详情图片列表 - JSON数组
     * 优惠券详情页轮播图
     */
    private String detailImages;
    
    /**
     * 标签列表 - JSON数组
     * ["新人专享", "限时", "热门"]
     */
    private String tags;
    
    // ==================== 数据统计字段 ====================
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 领取转化率 - 浏览/领取
     * 实时计算，用于效果分析
     */
    private BigDecimal conversionRate;
    
    /**
     * 使用率 - 领取/使用
     */
    private BigDecimal usageRate;
    
    /**
     * 拉新用户数 - 首次注册并领券的用户数
     */
    private Integer newUserCount;
    
    /**
     * 关联订单金额 - 使用本券的订单总金额
     */
    private BigDecimal relatedOrderAmount;
    
    /**
     * 优惠总金额 - 实际优惠金额汇总
     */
    private BigDecimal totalDiscountAmount;
    
    /**
     * ROI - 投入产出比
     * 优惠金额 / 关联订单金额
     */
    private BigDecimal roi;
    
    // ==================== 状态与审核 ====================
    
    /**
     * 状态
     * 0: 草稿
     * 1: 待审核
     * 2: 审核通过（进行中）
     * 3: 已暂停
     * 4: 已结束
     * 5: 审核拒绝
     */
    private Integer status;
    
    /**
     * 审核备注
     */
    private String auditRemark;
    
    /**
     * 审核时间
     */
    private LocalDateTime auditTime;
    
    /**
     * 审核人ID
     */
    private Long auditUserId;
    
    // ==================== 平台与扩展 ====================
    
    /**
     * 平台券标识
     * true: 平台发行的通用券
     * false: 商户发行的店铺券
     */
    private Boolean isPlatformCoupon;
    
    /**
     * 是否与其他优惠叠加
     * true: 可与满减、折扣等叠加使用
     */
    private Boolean stackable;
    
    /**
     * 扩展字段 - JSON格式
     * 存储业务自定义配置
     */
    private String extFields;
    
    /**
     * 版本号 - 乐观锁
     */
    @Version
    private Integer version;
    
    /**
     * 逻辑删除标识
     */
    @TableLogic
    private Integer deleted;
    
    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 创建人ID
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;
    
    /**
     * 更新人ID
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;
    
    // ==================== 便捷方法 ====================
    
    /**
     * 检查优惠券是否在可领取时间内
     */
    public boolean isInIssuePeriod() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(issueStartTime) && now.isBefore(issueEndTime);
    }
    
    /**
     * 检查优惠券是否可用（未过期）
     */
    public boolean isUsable() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(useStartTime) && now.isBefore(useEndTime);
    }
    
    /**
     * 检查是否还有库存
     */
    public boolean hasStock() {
        return totalStock < 0 || remainingStock > 0;
    }
    
    /**
     * 计算优惠金额
     * @param orderAmount 订单金额
     * @return 实际优惠金额
     */
    public BigDecimal calculateDiscount(BigDecimal orderAmount) {
        if (orderAmount.compareTo(minSpend) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount = BigDecimal.ZERO;
        
        switch (couponType) {
            case 1: // 满减券
                discount = discountValue;
                break;
            case 2: // 折扣券
                discount = orderAmount.multiply(BigDecimal.ONE.subtract(discountValue));
                if (maxDiscount != null && maxDiscount.compareTo(BigDecimal.ZERO) > 0) {
                    discount = discount.min(maxDiscount);
                }
                break;
            case 3: // 代金券
                discount = discountValue.min(orderAmount);
                break;
            default:
                break;
        }
        
        return discount;
    }
    
    /**
     * 获取类型描述
     */
    public String getTypeDescription() {
        switch (couponType) {
            case 1: return "满减券";
            case 2: return "折扣券";
            case 3: return "代金券";
            case 4: return "兑换券";
            case 5: return "运费券";
            case 6: return "品类券";
            default: return "未知类型";
        }
    }
    
    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        switch (status) {
            case 0: return "草稿";
            case 1: return "待审核";
            case 2: return "进行中";
            case 3: return "已暂停";
            case 4: return "已结束";
            case 5: return "审核拒绝";
            default: return "未知状态";
        }
    }
}
