package com.im.backend.modules.local.life.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动响应DTO
 */
@Data
public class ActivityResponse {

    private Long id;
    private String activityCode;
    private String title;
    private String description;
    private String coverImage;
    private String category;
    private String categoryName;
    private String status;
    private String statusName;

    private Long publisherId;
    private String publisherNickname;
    private String publisherAvatar;

    private String poiId;
    private String poiName;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime registrationDeadline;

    private Integer maxParticipants;
    private Integer currentParticipants;
    private Integer remainingSlots;

    private String paymentType;
    private String paymentTypeName;
    private BigDecimal perCapitaFee;

    private Boolean createImGroup;
    private String imGroupId;

    private Integer viewCount;
    private Integer shareCount;
    private Integer likeCount;
    private Integer commentCount;
    private Double heatScore;

    private Boolean recommended;
    private Boolean requireApproval;

    private List<String> tags;
    private List<ActivityMediaDTO> mediaList;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    private Boolean isRegistered;
    private String registrationStatus;
}
