package com.im.backend.modules.local.life.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动统计响应DTO
 */
@Data
public class ActivityStatisticsResponse {

    private Long activityId;
    private String activityTitle;

    private Integer viewCount;
    private Integer shareCount;
    private Integer likeCount;
    private Integer commentCount;

    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer confirmedCount;
    private Integer cancelledCount;

    private Integer checkInCount;
    private Integer ratedCount;
    private Double averageRating;

    private Double conversionRate;
    private Double checkInRate;

    private LocalDateTime statisticsTime;
}
