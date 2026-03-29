package com.im.entity.usergrowth;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * 用户任务定义实体
 * 定义系统中的成长任务/积分任务
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserTaskDefinition {
    
    /** 任务ID */
    private Long id;
    
    /** 任务编码 (唯一) */
    private String taskCode;
    
    /** 任务名称 */
    private String taskName;
    
    /** 任务描述 */
    private String taskDesc;
    
    /** 任务图标 */
    private String taskIcon;
    
    /** 任务类型 */
    private String taskType;
    
    /** 任务分类 */
    private String category;
    
    /** 任务触发条件 */
    private String triggerCondition;
    
    /** 任务完成条件类型 */
    private String completeConditionType;
    
    /** 任务完成条件值 */
    private String completeConditionValue;
    
    /** 任务完成条件描述 */
    private String completeConditionDesc;
    
    /** 任务周期 (ONCE/DAILY/WEEKLY/MONTHLY) */
    private String taskCycle;
    
    /** 任务奖励积分 */
    private Long rewardPoints;
    
    /** 任务奖励成长值 */
    private Long rewardGrowth;
    
    /** 是否奖励徽章 */
    private Boolean rewardBadge;
    
    /** 奖励徽章编码 */
    private String rewardBadgeCode;
    
    /** 任务排序 */
    private Integer sortOrder;
    
    /** 任务有效期开始 */
    private LocalDateTime validStartTime;
    
    /** 任务有效期结束 */
    private LocalDateTime validEndTime;
    
    /** 每日限完成次数 */
    private Integer dailyLimit;
    
    /** 总限完成次数 */
    private Integer totalLimit;
    
    /** 是否是新手任务 */
    private Boolean newbieTask;
    
    /** 前置任务编码 */
    private String prerequisiteTaskCode;
    
    /** 跳转链接 */
    private String actionUrl;
    
    /** 是否启用 */
    private Boolean enabled;
    
    /** 创建时间 */
    private LocalDateTime createTime;
    
    /** 更新时间 */
    private LocalDateTime updateTime;
    
    /**
     * 任务类型常量
     */
    public static class TaskType {
        public static final String GROWTH = "GROWTH";                          // 成长任务
        public static final String POINTS = "POINTS";                          // 积分任务
        public static final String DAILY = "DAILY";                            // 日常任务
        public static final String ACHIEVEMENT = "ACHIEVEMENT";                // 成就任务
        public static final String EVENT = "EVENT";                            // 活动任务
    }
    
    /**
     * 任务分类常量
     */
    public static class Category {
        public static final String NEWBIE = "NEWBIE";                          // 新手任务
        public static final String DAILY = "DAILY";                            // 日常任务
        public static final String SOCIAL = "SOCIAL";                          // 社交任务
        public static final String CONSUMPTION = "CONSUMPTION";                // 消费任务
        public static final String EXPLORATION = "EXPLORATION";                // 探索任务
        public static final String CONTRIBUTION = "CONTRIBUTION";              // 贡献任务
    }
    
    /**
     * 任务周期常量
     */
    public static class TaskCycle {
        public static final String ONCE = "ONCE";                              // 一次性
        public static final String DAILY = "DAILY";                            // 每日
        public static final String WEEKLY = "WEEKLY";                          // 每周
        public static final String MONTHLY = "MONTHLY";                        // 每月
    }
    
    /**
     * 完成条件类型常量
     */
    public static class CompleteConditionType {
        public static final String ACTION_PERFORM = "ACTION_PERFORM";          // 执行动作
        public static final String COUNT_REACH = "COUNT_REACH";                // 数量达标
        public static final String AMOUNT_REACH = "AMOUNT_REACH";              // 金额达标
        public static final String DAYS_CONSECUTIVE = "DAYS_CONSECUTIVE";      // 连续天数
        public static final String DAYS_CUMULATIVE = "DAYS_CUMULATIVE";        // 累计天数
        public static final String TIME_SPEND = "TIME_SPEND";                  // 时长要求
        public static final String CONTENT_POST = "CONTENT_POST";              // 发布内容
        public static final String CONTENT_LIKE = "CONTENT_LIKE";              // 点赞内容
        public static final String CONTENT_SHARE = "CONTENT_SHARE";            // 分享内容
    }
    
    /**
     * 检查任务是否在有效期内
     */
    public boolean isInValidPeriod() {
        LocalDateTime now = LocalDateTime.now();
        if (validStartTime != null && now.isBefore(validStartTime)) return false;
        if (validEndTime != null && now.isAfter(validEndTime)) return false;
        return true;
    }
    
    /**
     * 检查是否为周期任务
     */
    public boolean isCyclical() {
        return !ONCE.equals(taskCycle);
    }
    
    /**
     * 检查是否还有完成次数限制
     */
    public boolean hasRemainingLimit(Integer completedCount, Integer todayCompletedCount) {
        if (totalLimit != null && totalLimit > 0) {
            if (completedCount != null && completedCount >= totalLimit) return false;
        }
        if (dailyLimit != null && dailyLimit > 0) {
            if (todayCompletedCount != null && todayCompletedCount >= dailyLimit) return false;
        }
        return true;
    }
}
