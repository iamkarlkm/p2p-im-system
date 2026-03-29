package com.im.backend.modules.local_life.checkin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到响应DTO
 */
@Data
public class CheckinResponse {

    /**
     * 签到ID
     */
    private Long checkinId;

    /**
     * 签到状态
     */
    private String status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 获得积分
     */
    private Integer earnedPoints;

    /**
     * 连续签到天数
     */
    private Integer streakDays;

    /**
     * 是否是首次在该POI签到
     */
    private Boolean firstTimeAtPoi;

    /**
     * 用户当前等级
     */
    private String userLevel;

    /**
     * 等级进度
     */
    private Integer levelProgress;

    /**
     * 下一等级所需积分
     */
    private Integer nextLevelPoints;

    /**
     * 新获得的徽章列表
     */
    private java.util.List<BadgeInfo> newBadges;

    /**
     * 签到时间
     */
    private LocalDateTime checkinTime;

    @Data
    public static class BadgeInfo {
        private String badgeCode;
        private String badgeName;
        private String iconUrl;
        private Integer bonusPoints;
    }
}
