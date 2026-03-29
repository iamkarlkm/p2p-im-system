package com.im.entity.usergrowth;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 用户成长值记录实体
 * 记录用户的成长值获取、消耗、当前总值等
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserGrowthRecord {
    
    /** 记录ID */
    private Long id;
    
    /** 用户ID */
    private Long userId;
    
    /** 当前等级ID */
    private Long currentLevelId;
    
    /** 当前等级编码 */
    private String currentLevelCode;
    
    /** 当前总成长值 */
    private Long totalGrowthValue;
    
    /** 本年累计成长值 */
    private Long yearGrowthValue;
    
    /** 本月累计成长值 */
    private Long monthGrowthValue;
    
    /** 今日获得成长值 */
    private Long todayGrowthValue;
    
    /** 等级生效日期 */
    private LocalDate levelEffectiveDate;
    
    /** 等级到期日期 */
    private LocalDate levelExpireDate;
    
    /** 保级截止日期 */
    private LocalDate retainDeadline;
    
    /** 是否保级成功 */
    private Boolean retainSuccess;
    
    /** 上次保级日期 */
    private LocalDate lastRetainDate;
    
    /** 连续保级次数 */
    private Integer consecutiveRetainCount;
    
    /** 历史最高等级 */
    private String highestLevelCode;
    
    /** 历史最高成长值 */
    private Long highestGrowthValue;
    
    /** 升级次数统计 */
    private Integer upgradeCount;
    
    /** 降级次数统计 */
    private Integer downgradeCount;
    
    /** 成长值获取明细统计JSON */
    private String growthSourceStats;
    
    /** 版本号 (乐观锁) */
    private Integer version;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /**
     * 成长值来源类型常量
     */
    public static class GrowthSourceType {
        public static final String DAILY_SIGN = "DAILY_SIGN";                    // 每日签到
        public static final String CONTINUOUS_SIGN = "CONTINUOUS_SIGN";        // 连续签到
        public static final String PROFILE_COMPLETE = "PROFILE_COMPLETE";      // 完善资料
        public static final String FIRST_SEARCH = "FIRST_SEARCH";              // 首次搜索
        public static final String FIRST_REVIEW = "FIRST_REVIEW";              // 首次评价
        public static final String FIRST_SHARE = "FIRST_SHARE";                // 首次分享
        public static final String FIRST_FAVORITE = "FIRST_FAVORITE";          // 首次收藏
        public static final String DAILY_BROWSE = "DAILY_BROWSE";              // 每日浏览
        public static final String DAILY_SEARCH = "DAILY_SEARCH";              // 每日搜索
        public static final String POI_VISIT = "POI_VISIT";                    // 到店打卡
        public static final String POI_REVIEW = "POI_REVIEW";                  // 发布评价
        public static final String REVIEW_LIKED = "REVIEW_LIKED";              // 评价被点赞
        public static final String CONTENT_SHARE = "CONTENT_SHARE";            // 内容分享
        public static final String INVITE_FRIEND = "INVITE_FRIEND";            // 邀请好友
        public static final String FRIEND_ACCEPT = "FRIEND_ACCEPT";            // 好友接受邀请
        public static final String GROUP_JOIN = "GROUP_JOIN";                  // 加入群组
        public static final String GROUP_POST = "GROUP_POST";                  // 群组发帖
        public static final String ORDER_COMPLETE = "ORDER_COMPLETE";          // 完成订单
        public static final String ORDER_REVIEW = "ORDER_REVIEW";              // 订单评价
        public static final String COUPON_USE = "COUPON_USE";                  // 使用优惠券
        public static final String ACTIVITY_JOIN = "ACTIVITY_JOIN";            // 参与活动
        public static final String TASK_COMPLETE = "TASK_COMPLETE";            // 完成任务
        public static final String ACHIEVEMENT_UNLOCK = "ACHIEVEMENT_UNLOCK";  // 解锁成就
        public static final String LEVEL_UPGRADE = "LEVEL_UPGRADE";            // 等级提升
        public static final String CONSECUTIVE_LOGIN = "CONSECUTIVE_LOGIN";    // 连续登录
        public static final String VIP_PURCHASE = "VIP_PURCHASE";              // 购买会员
    }
    
    /**
     * 增加成长值
     */
    public void addGrowthValue(Long value) {
        if (value == null || value <= 0) return;
        this.totalGrowthValue = (this.totalGrowthValue == null ? 0 : this.totalGrowthValue) + value;
        this.yearGrowthValue = (this.yearGrowthValue == null ? 0 : this.yearGrowthValue) + value;
        this.monthGrowthValue = (this.monthGrowthValue == null ? 0 : this.monthGrowthValue) + value;
        this.todayGrowthValue = (this.todayGrowthValue == null ? 0 : this.todayGrowthValue) + value;
        updateHighestGrowthValue();
    }
    
    /**
     * 减少成长值 (降级时使用)
     */
    public void deductGrowthValue(Long value) {
        if (value == null || value <= 0) return;
        this.totalGrowthValue = Math.max(0, this.totalGrowthValue - value);
    }
    
    /**
     * 更新历史最高成长值
     */
    private void updateHighestGrowthValue() {
        if (this.totalGrowthValue > this.highestGrowthValue) {
            this.highestGrowthValue = this.totalGrowthValue;
        }
    }
    
    /**
     * 每日重置今日成长值
     */
    public void resetDailyGrowth() {
        this.todayGrowthValue = 0L;
    }
    
    /**
     * 每月重置月度成长值
     */
    public void resetMonthlyGrowth() {
        this.monthGrowthValue = 0L;
    }
    
    /**
     * 每年重置年度成长值
     */
    public void resetYearlyGrowth() {
        this.yearGrowthValue = 0L;
    }
    
    /**
     * 计算距离等级到期还有多少天
     */
    public long getDaysUntilExpire() {
        if (levelExpireDate == null) return Long.MAX_VALUE;
        return ChronoUnit.DAYS.between(LocalDate.now(), levelExpireDate);
    }
    
    /**
     * 计算距离保级截止还有多少天
     */
    public long getDaysUntilRetainDeadline() {
        if (retainDeadline == null) return Long.MAX_VALUE;
        return ChronoUnit.DAYS.between(LocalDate.now(), retainDeadline);
    }
    
    /**
     * 判断等级是否已过期
     */
    public boolean isLevelExpired() {
        if (levelExpireDate == null) return false;
        return LocalDate.now().isAfter(levelExpireDate);
    }
    
    /**
     * 判断是否在保级期内
     */
    public boolean isInRetainPeriod() {
        if (retainDeadline == null) return false;
        LocalDate now = LocalDate.now();
        return !now.isAfter(retainDeadline);
    }
    
    /**
     * 记录升级
     */
    public void recordUpgrade(String newLevelCode) {
        this.upgradeCount = (this.upgradeCount == null ? 0 : this.upgradeCount) + 1;
        this.currentLevelCode = newLevelCode;
        if (highestLevelCode == null || compareLevel(newLevelCode, highestLevelCode) > 0) {
            this.highestLevelCode = newLevelCode;
        }
    }
    
    /**
     * 记录降级
     */
    public void recordDowngrade(String newLevelCode) {
        this.downgradeCount = (this.downgradeCount == null ? 0 : this.downgradeCount) + 1;
        this.currentLevelCode = newLevelCode;
    }
    
    /**
     * 记录保级成功
     */
    public void recordRetainSuccess() {
        this.retainSuccess = true;
        this.lastRetainDate = LocalDate.now();
        this.consecutiveRetainCount = (this.consecutiveRetainCount == null ? 0 : this.consecutiveRetainCount) + 1;
    }
    
    /**
     * 记录保级失败
     */
    public void recordRetainFail() {
        this.retainSuccess = false;
        this.consecutiveRetainCount = 0;
    }
    
    /**
     * 比较等级高低
     * @return 正数表示level1更高，负数表示level2更高，0表示相等
     */
    private int compareLevel(String level1, String level2) {
        if (level1 == null || level2 == null) return 0;
        try {
            int num1 = Integer.parseInt(level1.replaceAll("[^0-9]", ""));
            int num2 = Integer.parseInt(level2.replaceAll("[^0-9]", ""));
            return num1 - num2;
        } catch (Exception e) {
            return level1.compareTo(level2);
        }
    }
    
    /**
     * 获取成长值进度百分比
     */
    public double getGrowthProgress(Long nextLevelMinValue) {
        if (nextLevelMinValue == null || nextLevelMinValue <= 0) return 100.0;
        if (totalGrowthValue == null) return 0.0;
        return Math.min(100.0, (totalGrowthValue * 100.0) / nextLevelMinValue);
    }
    
    /**
     * 获取升级到下一级还需多少成长值
     */
    public Long getGrowthValueToNextLevel(Long nextLevelMinValue) {
        if (nextLevelMinValue == null) return 0L;
        if (totalGrowthValue == null) return nextLevelMinValue;
        return Math.max(0, nextLevelMinValue - totalGrowthValue);
    }
}
