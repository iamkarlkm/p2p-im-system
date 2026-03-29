package com.im.backend.modules.local_life.checkin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户积分账户响应DTO
 */
@Data
public class PointAccountResponse {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 当前积分余额
     */
    private Integer balance;

    /**
     * 累计获得积分
     */
    private Integer totalEarned;

    /**
     * 累计消耗积分
     */
    private Integer totalUsed;

    /**
     * 用户等级
     */
    private Integer level;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 等级颜色
     */
    private String levelColor;

    /**
     * 连续签到天数
     */
    private Integer streakDays;

    /**
     * 最长连续签到记录
     */
    private Integer maxStreakDays;

    /**
     * 累计签到天数
     */
    private Integer totalCheckinDays;

    /**
     * 最后签到日期
     */
    private String lastCheckinDate;

    /**
     * 等级进度百分比
     */
    private Integer levelProgressPercent;

    /**
     * 当前等级最小积分
     */
    private Integer currentLevelMinPoints;

    /**
     * 下一等级最小积分
     */
    private Integer nextLevelMinPoints;

    /**
     * 升级还需积分
     */
    private Integer pointsToNextLevel;
}
