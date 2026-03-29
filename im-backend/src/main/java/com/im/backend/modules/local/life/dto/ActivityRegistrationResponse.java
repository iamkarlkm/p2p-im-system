package com.im.backend.modules.local.life.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 活动报名记录响应DTO
 */
@Data
public class ActivityRegistrationResponse {

    private Long id;
    private String registrationCode;
    private Long activityId;
    private String activityTitle;
    private String activityCover;

    private Long userId;
    private String userNickname;
    private String userAvatar;

    private String status;
    private String statusName;

    private Integer participantCount;
    private String participantNames;
    private String contactPhone;
    private String remark;

    private String paymentStatus;
    private BigDecimal paymentAmount;
    private LocalDateTime paymentTime;

    private Boolean checkedIn;
    private LocalDateTime checkInTime;

    private String ratingStatus;
    private Integer rating;
    private String ratingContent;

    private LocalDateTime createTime;
}
