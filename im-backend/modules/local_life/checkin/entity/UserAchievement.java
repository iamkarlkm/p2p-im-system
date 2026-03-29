package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 用户成就徽章实体
 */
@Data
@TableName("user_achievement")
public class UserAchievement {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 成就类型: FIRST_CHECKIN-首次签到, STREAK_7-连续7天, STREAK_30-连续30天
     * EXPLORER-探索达人, SOCIAL-社交达人, VIP-贵宾签到
     */
    private String achievementType;

    /**
     * 成就名称
     */
    private String name;

    /**
     * 成就图标
     */
    private String icon;

    /**
     * 成就描述
     */
    private String description;

    /**
     * 获得奖励积分
     */
    private Integer rewardPoints;

    /**
     * 获得时间
     */
    private LocalDateTime achievedAt;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
