package com.im.entity.usergrowth;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户成就徽章实体
 * 定义系统中所有可获得的成就徽章
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAchievementBadge {
    
    /** 徽章ID */
    private Long id;
    
    /** 徽章编码 (唯一) */
    private String badgeCode;
    
    /** 徽章名称 */
    private String badgeName;
    
    /** 徽章描述 */
    private String badgeDesc;
    
    /** 徽章图标 (未获得状态) */
    private String badgeIconLocked;
    
    /** 徽章图标 (已获得状态) */
    private String badgeIconUnlocked;
    
    /** 徽章动画效果URL */
    private String badgeAnimation;
    
    /** 徽章颜色主题 */
    private String badgeColor;
    
    /** 徽章分类 */
    private String category;
    
    /** 徽章等级 (COMMON/RARE/EPIC/LEGENDARY) */
    private String rarity;
    
    /** 解锁条件类型 */
    private String unlockConditionType;
    
    /** 解锁条件值 */
    private String unlockConditionValue;
    
    /** 解锁条件描述 */
    private String unlockConditionDesc;
    
    /** 奖励积分 */
    private Long rewardPoints;
    
    /** 奖励成长值 */
    private Long rewardGrowth;
    
    /** 是否隐藏徽章 (达成后才显示) */
    private Boolean hidden;
    
    /** 排序优先级 */
    private Integer sortOrder;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /**
     * 徽章分类常量
     */
    public static class Category {
        public static final String SIGN_IN = "SIGN_IN";                        // 签到类
        public static final String SOCIAL = "SOCIAL";                          // 社交类
        public static final String CONSUMPTION = "CONSUMPTION";                // 消费类
        public static final String EXPLORATION = "EXPLORATION";                // 探索类
        public static final String CONTRIBUTION = "CONTRIBUTION";              // 贡献类
        public static final String EVENT = "EVENT";                            // 活动类
        public static final String SPECIAL = "SPECIAL";                        // 特殊类
    }
    
    /**
     * 徽章稀有度常量
     */
    public static class Rarity {
        public static final String COMMON = "COMMON";                          // 普通 (白色)
        public static final String UNCOMMON = "UNCOMMON";                      // 优秀 (绿色)
        public static final String RARE = "RARE";                              // 稀有 (蓝色)
        public static final String EPIC = "EPIC";                              // 史诗 (紫色)
        public static final String LEGENDARY = "LEGENDARY";                    // 传说 (橙色)
    }
    
    /**
     * 解锁条件类型常量
     */
    public static class UnlockConditionType {
        public static final String CUMULATIVE_SIGN_DAYS = "CUMULATIVE_SIGN_DAYS";           // 累计签到天数
        public static final String CONSECUTIVE_SIGN_DAYS = "CONSECUTIVE_SIGN_DAYS";         // 连续签到天数
        public static final String TOTAL_ORDERS = "TOTAL_ORDERS";                           // 累计订单数
        public static final String TOTAL_SPEND = "TOTAL_SPEND";                             // 累计消费金额
        public static final String TOTAL_REVIEWS = "TOTAL_REVIEWS";                         // 累计评价数
        public static final String TOTAL_FAVORITES = "TOTAL_FAVORITES";                     // 累计收藏数
        public static final String TOTAL_SHARES = "TOTAL_SHARES";                           // 累计分享数
        public static final String TOTAL_INVITES = "TOTAL_INVITES";                         // 累计邀请数
        public static final String VISIT_POI_COUNT = "VISIT_POI_COUNT";                     // 访问POI数量
        public static final String VISIT_CITY_COUNT = "VISIT_CITY_COUNT";                   // 访问城市数量
        public static final String GROUP_JOIN_COUNT = "GROUP_JOIN_COUNT";                   // 加入群组数量
        public static final String GROUP_POST_COUNT = "GROUP_POST_COUNT";                   // 群组发帖数量
        public static final String CONTENT_LIKES_RECEIVED = "CONTENT_LIKES_RECEIVED";       // 获赞数量
        public static final String LEVEL_REACHED = "LEVEL_REACHED";                         // 达到等级
        public static final String POINTS_EARNED = "POINTS_EARNED";                         // 累计积分
        public static final String TASK_COMPLETE_COUNT = "TASK_COMPLETE_COUNT";             // 完成任务数
        public static final String ACHIEVEMENT_COUNT = "ACHIEVEMENT_COUNT";                 // 获得成就数
        public static final String EARLY_BIRD = "EARLY_BIRD";                               // 早起打卡
        public static final String NIGHT_OWL = "NIGHT_OWL";                                 // 夜猫子
        public static final String SPECIAL_DATE = "SPECIAL_DATE";                           // 特殊日期
        public static final String MANUAL_GRANT = "MANUAL_GRANT";                           // 手动授予
    }
    
    /**
     * 获取徽章稀有度颜色
     */
    public String getRarityColor() {
        if (rarity == null) return "#999999";
        switch (rarity) {
            case COMMON: return "#999999";         // 灰
            case UNCOMMON: return "#4CAF50";       // 绿
            case RARE: return "#2196F3";           // 蓝
            case EPIC: return "#9C27B0";           // 紫
            case LEGENDARY: return "#FF9800";      // 橙
            default: return "#999999";
        }
    }
    
    /**
     * 检查是否满足解锁条件
     */
    public boolean checkUnlockCondition(String userStats) {
        if (unlockConditionValue == null) return false;
        try {
            long required = Long.parseLong(unlockConditionValue);
            long actual = Long.parseLong(userStats);
            return actual >= required;
        } catch (Exception e) {
            return false;
        }
    }
}
