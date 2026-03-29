package com.im.backend.modules.local_life.checkin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 徽章信息DTO
 */
@Data
public class BadgeDTO {

    /**
     * 徽章代码
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
     * 是否已获得
     */
    private Boolean earned;

    /**
     * 获得时间
     */
    private LocalDateTime earnedTime;

    /**
     * 是否已领取奖励
     */
    private Boolean rewardClaimed;
}
