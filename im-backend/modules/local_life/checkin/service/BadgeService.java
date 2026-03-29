package com.im.backend.modules.local_life.checkin.service;

import com.im.backend.modules.local_life.checkin.dto.BadgeDTO;
import com.im.backend.modules.local_life.checkin.dto.CheckinResponse;

import java.util.List;

/**
 * 成就徽章服务接口
 */
public interface BadgeService {

    /**
     * 检查并授予徽章
     */
    List<CheckinResponse.BadgeInfo> checkAndGrantBadges(Long userId, String checkinDate);

    /**
     * 获取用户所有徽章
     */
    List<BadgeDTO> getUserBadges(Long userId);

    /**
     * 领取徽章奖励
     */
    boolean claimBadgeReward(Long userId, String badgeCode);

    /**
     * 获取徽章排行榜
     */
    List<BadgeLeaderboardDTO> getBadgeLeaderboard(String badgeCode, Integer limit);
}
