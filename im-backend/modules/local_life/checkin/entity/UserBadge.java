package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户成就徽章实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_badge")
public class UserBadge extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 徽章类型代码
     */
    private String badgeCode;

    /**
     * 徽章名称
     */
    private String badgeName;

    /**
     * 徽章描述
     */
    private String badgeDesc;

    /**
     * 徽章图标URL
     */
    private String iconUrl;

    /**
     * 奖励积分
     */
    private Integer bonusPoints;

    /**
     * 获得时间
     */
    private LocalDateTime earnedTime;

    /**
     * 是否已领取奖励
     */
    private Boolean rewardClaimed;

    /**
     * 领取奖励时间
     */
    private LocalDateTime rewardClaimedTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
