package com.im.backend.modules.coupon.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 营销活动实体 - 本地生活营销引擎核心
 * 
 * 功能说明:
 * 1. 支持多种活动类型（满减/折扣/秒杀/拼团/砍价）
 * 2. 活动规则配置引擎（条件+动作）
 * 3. 目标人群定向（新客/老客/会员等级/地理位置）
 * 4. 活动效果实时监控
 * 5. 多活动叠加与互斥管理
 * 
 * 活动类型:
 * - 满减活动: 满X元减Y元，可阶梯（满100减10，满200减30）
 * - 折扣活动: X折优惠，可限制最大优惠额
 * - 秒杀活动: 限时限量抢购
 * - 拼团活动: 多人拼团享优惠
 * - 砍价活动: 邀请好友砍价
 * - 首单优惠: 新客首单特惠
 * - 满赠活动: 满额赠送商品/券
 * - 加价购: 低价换购商品
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("im_marketing_activity")
public class MarketingActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    // ==================== 基础字段 ====================
    
    /**
     * 活动ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 活动编码
     */
    private String activityCode;
    
    /**
     * 活动名称
     */
    private String name;
    
    /**
     * 活动描述
     */
    private String description;
    
    /**
     * 活动类型
     * 1: 满减活动
     * 2: 折扣活动
     * 3: 秒杀活动
     * 4: 拼团活动
     * 5: 砍价活动
     * 6: 首单优惠
     * 7: 满赠活动
     * 8: 加价购
     * 9: 优惠券发放
     * 10: 积分翻倍
     * 11: 免配送费
     * 12: 会员日
     */
    private Integer activityType;
    
    /**
     * 活动子类型 - 细分类别
     */
    private Integer subType;
    
    // ==================== 商户关联 ====================
    
    /**
     * 创建商户ID
     */
    private Long merchantId;
    
    /**
     * 商户名称
     */
    private String merchantName;
    
    /**
     * 适用商户列表 - JSON数组
     * 多商户联合活动时使用
     */
    private String applicableMerchantIds;
    
    /**
     * 平台活动标识
     */
    private Boolean isPlatformActivity;
    
    // ==================== 时间配置 ====================
    
    /**
     * 活动开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 活动结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 每日开始时段 - 如秒杀每天10点开始
     * 格式: HH:mm
     */
    private String dailyStartTime;
    
    /**
     * 每日结束时段
     */
    private String dailyEndTime;
    
    /**
     * 适用星期 - JSON数组 [1,2,3,4,5] 表示周一至周五
     * 空表示每天
     */
    private String applicableWeekdays;
    
    /**
     * 预热开始时间 - 提前展示但不可参与
     */
    private LocalDateTime previewStartTime;
    
    /**
     * 报名截止时间 - 拼团/秒杀等需要报名的活动
     */
    private LocalDateTime registrationDeadline;
    
    // ==================== 活动规则配置 ====================
    
    /**
     * 规则配置 - JSON对象
     * 根据活动类型存储不同规则
     * 
     * 满减活动示例:
     * {
     *   "tiers": [
     *     {"threshold": 100, "discount": 10},
     *     {"threshold": 200, "discount": 30},
     *     {"threshold": 500, "discount": 100}
     *   ]
     * }
     * 
     * 秒杀活动示例:
     * {
     *   "productId": 12345,
     *   "seckillPrice": 9.9,
     *   "originalPrice": 99.0,
     *   "stock": 100,
     *   "limitPerUser": 1
     * }
     * 
     * 拼团活动示例:
     * {
     *   "productId": 12345,
     *   "groupSize": 3,
     *   "groupPrice": 59.9,
     *   "originalPrice": 99.0,
     *   "groupValidHours": 24
     * }
     */
    private String ruleConfig;
    
    /**
     * 参与条件 - JSON对象
     * {
     *   "minOrderAmount": 100,
     *   "applicableCategories": ["food"],
     *   "excludeProducts": [123, 456],
     *   "userLevels": ["GOLD", "PLATINUM"],
     *   "newUserOnly": false
     * }
     */
    private String participationCondition;
    
    /**
     * 参与限制
     * 0: 无限制
     * 1: 每人限参与X次
     * 2: 每人每天限X次
     * 3: 每人每周限X次
     * 4: 每人每月限X次
     */
    private Integer participationLimitType;
    
    /**
     * 参与限制次数
     */
    private Integer participationLimit;
    
    /**
     * 总参与次数限制
     * -1表示无限制
     */
    private Integer totalParticipationLimit;
    
    // ==================== 目标人群定向 ====================
    
    /**
     * 目标用户类型
     * 0: 全部用户
     * 1: 新用户
     * 2: 老用户
     * 3: 会员用户
     * 4: 非会员用户
     * 5: 指定会员等级
     * 6: 指定地域用户
     * 7: 指定标签用户
     */
    private Integer targetUserType;
    
    /**
     * 目标用户配置 - JSON对象
     * {
     *   "memberLevels": ["GOLD", "PLATINUM"],
     *   "geoCityCodes": ["110000", "310000"],
     *   "userTags": ["high_value", "frequent"]
     * }
     */
    private String targetUserConfig;
    
    /**
     * 用户黑名单 - JSON数组
     * 排除特定用户
     */
    private String excludedUserIds;
    
    // ==================== 库存与预算控制 ====================
    
    /**
     * 活动总预算
     */
    private BigDecimal totalBudget;
    
    /**
     * 已使用预算
     */
    private BigDecimal usedBudget;
    
    /**
     * 预算预警阈值百分比
     * 80表示预算使用达80%时预警
     */
    private Integer budgetAlertThreshold;
    
    /**
     * 是否预算已预警
     */
    private Boolean budgetAlertSent;
    
    /**
     * 总库存/总量
     */
    private Integer totalStock;
    
    /**
     * 剩余库存
     */
    private Integer remainingStock;
    
    // ==================== 活动互斥与叠加 ====================
    
    /**
     * 是否可与优惠券叠加
     */
    private Boolean stackableWithCoupon;
    
    /**
     * 是否可与会员折扣叠加
     */
    private Boolean stackableWithMemberDiscount;
    
    /**
     * 互斥活动列表 - JSON数组
     * 这些活动不能与本活动同时享受
     */
    private String exclusiveActivityIds;
    
    /**
     * 优先级 - 数值越大优先级越高
     * 互斥时优先应用高优先级活动
     */
    private Integer priority;
    
    // ==================== 展示配置 ====================
    
    /**
     * 活动封面图
     */
    private String coverImage;
    
    /**
     * 活动详情图列表 - JSON数组
     */
    private String detailImages;
    
    /**
     * 活动标签 - JSON数组
     * ["限时", "热销", "独家"]
     */
    private String tags;
    
    /**
     * 展示开始时间 - 可提前展示
     */
    private LocalDateTime displayStartTime;
    
    /**
     * 展示结束时间
     */
    private LocalDateTime displayEndTime;
    
    /**
     * 是否在首页推荐
     */
    private Boolean homepageRecommended;
    
    /**
     * 推荐排序值
     */
    private Integer recommendSort;
    
    /**
     * 活动分享标题
     */
    private String shareTitle;
    
    /**
     * 活动分享描述
     */
    private String shareDescription;
    
    /**
     * 活动分享图片
     */
    private String shareImage;
    
    // ==================== 数据统计 ====================
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 参与人数
     */
    private Integer participantCount;
    
    /**
     * 参与次数
     */
    private Integer participationCount;
    
    /**
     * 成交订单数
     */
    private Integer orderCount;
    
    /**
     * 成交金额
     */
    private BigDecimal orderAmount;
    
    /**
     * 优惠总金额
     */
    private BigDecimal discountAmount;
    
    /**
     * 转化率
     */
    private BigDecimal conversionRate;
    
    /**
     * 拉新用户数
     */
    private Integer newUserCount;
    
    /**
     * ROI
     */
    private BigDecimal roi;
    
    // ==================== 状态 ====================
    
    /**
     * 活动状态
     * 0: 草稿
     * 1: 待审核
     * 2: 进行中
     * 3: 暂停
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
     * 审核人
     */
    private Long auditUserId;
    
    // ==================== 扩展 ====================
    
    /**
     * 扩展字段
     */
    private String extFields;
    
    /**
     * 版本号
     */
    @Version
    private Integer version;
    
    /**
     * 逻辑删除
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
     * 创建人
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;
    
    /**
     * 更新人
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;
    
    // ==================== 便捷方法 ====================
    
    /**
     * 检查活动是否在进行中
     */
    public boolean isActive() {
        if (status != 2) {
            return false;
        }
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(startTime) && now.isBefore(endTime);
    }
    
    /**
     * 检查活动是否在展示期
     */
    public boolean isDisplaying() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(displayStartTime) && now.isBefore(displayEndTime);
    }
    
    /**
     * 检查是否还有库存
     */
    public boolean hasStock() {
        return totalStock < 0 || remainingStock > 0;
    }
    
    /**
     * 检查预算是否充足
     */
    public boolean hasBudget() {
        if (totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            return true;
        }
        return usedBudget.compareTo(totalBudget) < 0;
    }
    
    /**
     * 检查是否达到预算预警
     */
    public boolean isBudgetAlert() {
        if (totalBudget.compareTo(BigDecimal.ZERO) == 0) {
            return false;
        }
        BigDecimal threshold = totalBudget.multiply(new BigDecimal(budgetAlertThreshold)).divide(new BigDecimal(100));
        return usedBudget.compareTo(threshold) >= 0;
    }
    
    /**
     * 获取状态描述
     */
    public String getStatusDescription() {
        switch (status) {
            case 0: return "草稿";
            case 1: return "待审核";
            case 2: return "进行中";
            case 3: return "暂停";
            case 4: return "已结束";
            case 5: return "审核拒绝";
            default: return "未知";
        }
    }
    
    /**
     * 获取活动类型描述
     */
    public String getActivityTypeDescription() {
        switch (activityType) {
            case 1: return "满减活动";
            case 2: return "折扣活动";
            case 3: return "秒杀活动";
            case 4: return "拼团活动";
            case 5: return "砍价活动";
            case 6: return "首单优惠";
            case 7: return "满赠活动";
            case 8: return "加价购";
            case 9: return "优惠券发放";
            case 10: return "积分翻倍";
            case 11: return "免配送费";
            case 12: return "会员日";
            default: return "未知类型";
        }
    }
}
