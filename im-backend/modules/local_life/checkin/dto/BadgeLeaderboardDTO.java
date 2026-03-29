package com.im.backend.modules.local_life.checkin.dto;

import lombok.Data;

/**
 * 徽章排行榜DTO
 */
@Data
public class BadgeLeaderboardDTO {

    private Long userId;
    private String userNickname;
    private String userAvatar;
    private String badgeCode;
    private String badgeName;
    private Integer earnedCount;
    private String earnedTime;
    private Integer rank;
}
