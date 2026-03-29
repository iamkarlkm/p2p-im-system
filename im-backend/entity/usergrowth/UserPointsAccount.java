package com.im.entity.usergrowth;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * 用户积分账户实体
 * 记录用户的积分余额、累计获取、累计消耗等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPointsAccount {
    
    /** 账户ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 当前可用积分余额 */
    private Long availablePoints;
    
    /** 累计获取积分 */
    private Long totalEarnedPoints;
    
    /** 累计消耗积分 */
    private Long totalSpentPoints;
    
    /** 冻结积分 (如订单未确认) */
    private Long frozenPoints;
    
    /** 即将过期积分 */
    private Long expiringSoonPoints;
    
    /** 即将过期日期 */
    private LocalDateTime expiringSoonDate;
    
    /** 今日获取积分 */
    private Long todayEarnedPoints;
    
    /** 本月获取积分 */
    private Long monthEarnedPoints;
    
    /** 本年获取积分 */
    private Long yearEarnedPoints;
    
    /** 积分等级 (根据累计积分计算) */
    private String pointsLevel;
    
    /** 积分会员到期时间 */
    private LocalDateTime pointsMemberExpireTime;
    
    /** 连续签到天数 */
    private Integer consecutiveSignDays;
    
    /** 最后签到日期 */
    private LocalDateTime lastSignDate;
    
    /** 签到累计天数 */
    private Integer totalSignDays;
    
    /** 积分获取明细统计JSON */
    private String earnSourceStats;
    
    /** 积分消耗明细统计JSON */
    private String spendSourceStats;
    
    /** 版本号 (乐观锁) */
    private Integer version;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /**
     * 增加积分
     */
    public void addPoints(Long points) {
        if (points == null || points <= 0) return;
        this.availablePoints = (this.availablePoints == null ? 0 : this.availablePoints) + points;
        this.totalEarnedPoints = (this.totalEarnedPoints == null ? 0 : this.totalEarnedPoints) + points;
        this.todayEarnedPoints = (this.todayEarnedPoints == null ? 0 : this.todayEarnedPoints) + points;
        this.monthEarnedPoints = (this.monthEarnedPoints == null ? 0 : this.monthEarnedPoints) + points;
        this.yearEarnedPoints = (this.yearEarnedPoints == null ? 0 : this.yearEarnedPoints) + points;
    }
    
    /**
     * 消耗积分
     * @return 是否消耗成功
     */
    public boolean deductPoints(Long points) {
        if (points == null || points <= 0) return false;
        if (availablePoints == null || availablePoints < points) return false;
        this.availablePoints -= points;
        this.totalSpentPoints = (this.totalSpentPoints == null ? 0 : this.totalSpentPoints) + points;
        return true;
    }
    
    /**
     * 冻结积分
     */
    public boolean freezePoints(Long points) {
        if (points == null || points <= 0) return false;
        if (availablePoints == null || availablePoints < points) return false;
        this.availablePoints -= points;
        this.frozenPoints = (this.frozenPoints == null ? 0 : this.frozenPoints) + points;
        return true;
    }
    
    /**
     * 解冻积分
     */
    public void unfreezePoints(Long points) {
        if (points == null || points <= 0) return;
        if (frozenPoints == null || frozenPoints < points) return;
        this.frozenPoints -= points;
        this.availablePoints = (this.availablePoints == null ? 0 : this.availablePoints) + points;
    }
    
    /**
     * 确认消耗冻结积分
     */
    public void confirmFrozenPoints(Long points) {
        if (points == null || points <= 0) return;
        if (frozenPoints == null) return;
        this.frozenPoints = Math.max(0, this.frozenPoints - points);
        this.totalSpentPoints = (this.totalSpentPoints == null ? 0 : this.totalSpentPoints) + points;
    }
    
    /**
     * 每日重置
     */
    public void resetDaily() {
        this.todayEarnedPoints = 0L;
    }
    
    /**
     * 每月重置
     */
    public void resetMonthly() {
        this.monthEarnedPoints = 0L;
    }
    
    /**
     * 每年重置
     */
    public void resetYearly() {
        this.yearEarnedPoints = 0L;
    }
    
    /**
     * 签到
     */
    public void doSign(LocalDateTime signTime) {
        this.lastSignDate = signTime;
        this.totalSignDays = (this.totalSignDays == null ? 0 : this.totalSignDays) + 1;
        this.consecutiveSignDays = (this.consecutiveSignDays == null ? 0 : this.consecutiveSignDays) + 1;
    }
    
    /**
     * 中断连续签到
     */
    public void breakConsecutiveSign() {
        this.consecutiveSignDays = 0;
    }
    
    /**
     * 获取实际可用积分 (可用余额 - 冻结)
     */
    public Long getRealAvailablePoints() {
        return availablePoints == null ? 0 : availablePoints;
    }
    
    /**
     * 检查积分是否充足
     */
    public boolean hasEnoughPoints(Long requiredPoints) {
        if (requiredPoints == null || requiredPoints <= 0) return true;
        Long available = getRealAvailablePoints();
        return available >= requiredPoints;
    }
    
    /**
     * 计算积分价值 (假设100积分=1元)
     */
    public double calculatePointsValue(double exchangeRate) {
        Long available = getRealAvailablePoints();
        return available * exchangeRate;
    }
    
    /**
     * 积分等级计算
     */
    public void calculatePointsLevel() {
        Long total = totalEarnedPoints == null ? 0 : totalEarnedPoints;
        if (total >= 100000) {
            this.pointsLevel = "PLATINUM";      // 铂金
        } else if (total >= 50000) {
            this.pointsLevel = "GOLD";          // 黄金
        } else if (total >= 20000) {
            this.pointsLevel = "SILVER";        // 白银
        } else if (total >= 5000) {
            this.pointsLevel = "BRONZE";        // 青铜
        } else {
            this.pointsLevel = "NORMAL";        // 普通
        }
    }
    
    /**
     * 积分来源类型常量
     */
    public static class PointsSourceType {
        public static final String DAILY_SIGN = "DAILY_SIGN";                    // 每日签到
        public static final String CONTINUOUS_SIGN = "CONTINUOUS_SIGN";        // 连续签到
        public static final String PROFILE_COMPLETE = "PROFILE_COMPLETE";      // 完善资料
        public static final String FIRST_BIND_PHONE = "FIRST_BIND_PHONE";      // 绑定手机
        public static final String FIRST_BIND_WECHAT = "FIRST_BIND_WECHAT";    // 绑定微信
        public static final String EMAIL_VERIFICATION = "EMAIL_VERIFICATION";  // 邮箱验证
        public static final String DAILY_BROWSE = "DAILY_BROWSE";              // 每日浏览
        public static final String DAILY_SEARCH = "DAILY_SEARCH";              // 每日搜索
        public static final String DAILY_SHARE = "DAILY_SHARE";                // 每日分享
        public static final String POI_FAVORITE = "POI_FAVORITE";              // 收藏POI
        public static final String POI_VISIT = "POI_VISIT";                    // 到店打卡
        public static final String POI_REVIEW = "POI_REVIEW";                  // 发布评价
        public static final String REVIEW_LIKED = "REVIEW_LIKED";              // 评价被点赞
        public static final String REVIEW_COMMENTED = "REVIEW_COMMENTED";      // 评价被评论
        public static final String CONTENT_POST = "CONTENT_POST";              // 发布内容
        public static final String CONTENT_LIKED = "CONTENT_LIKED";            // 内容被点赞
        public static final String CONTENT_SHARED = "CONTENT_SHARED";          // 内容被分享
        public static final String INVITE_FRIEND = "INVITE_FRIEND";            // 邀请好友
        public static final String FRIEND_ACCEPT = "FRIEND_ACCEPT";            // 好友接受邀请
        public static final String GROUP_JOIN = "GROUP_JOIN";                  // 加入群组
        public static final String GROUP_POST = "GROUP_POST";                  // 群组发帖
        public static final String ORDER_COMPLETE = "ORDER_COMPLETE";          // 完成订单
        public static final String ORDER_REVIEW = "ORDER_REVIEW";              // 订单评价
        public static final String ORDER_AMOUNT = "ORDER_AMOUNT";              // 订单金额返积分
        public static final String COUPON_USE = "COUPON_USE";                  // 使用优惠券
        public static final String ACTIVITY_JOIN = "ACTIVITY_JOIN";            // 参与活动
        public static final String TASK_COMPLETE = "TASK_COMPLETE";            // 完成任务
        public static final String ACHIEVEMENT_UNLOCK = "ACHIEVEMENT_UNLOCK";  // 解锁成就
        public static final String LEVEL_UPGRADE = "LEVEL_UPGRADE";            // 等级提升
        public static final String LOTTERY_WIN = "LOTTERY_WIN";                // 抽奖获得
        public static final String EXCHANGE_CODE = "EXCHANGE_CODE";            // 兑换码
        public static final String SYSTEM_COMPENSATION = "SYSTEM_COMPENSATION"; // 系统补偿
        public static final String ADMIN_GRANT = "ADMIN_GRANT";                // 管理员发放
    }
    
    /**
     * 积分消耗类型常量
     */
    public static class PointsSpendType {
        public static final String CASH_DEDUCTION = "CASH_DEDUCTION";          // 抵扣现金
        public static final String COUPON_EXCHANGE = "COUPON_EXCHANGE";        // 兑换优惠券
        public static final String GIFT_EXCHANGE = "GIFT_EXCHANGE";            // 兑换礼品
        public static final String LOTTERY_DRAW = "LOTTERY_DRAW";              // 抽奖消耗
        public static final String VIP_UPGRADE = "VIP_UPGRADE";                // 升级会员
        public static final String PRIVILEGE_UNLOCK = "PRIVILEGE_UNLOCK";      // 解锁特权
        public static final String DONATION = "DONATION";                      // 积分捐赠
        public static final String EXPIRED = "EXPIRED";                        // 积分过期
        public static final String ADMIN_DEDUCT = "ADMIN_DEDUCT";              // 管理员扣除
        public static final String ORDER_CANCEL = "ORDER_CANCEL";              // 订单取消返还
    }
}
