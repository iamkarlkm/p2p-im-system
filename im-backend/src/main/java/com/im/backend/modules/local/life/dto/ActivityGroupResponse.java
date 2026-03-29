package com.im.backend.modules.local.life.dto;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动群组响应DTO
 */
@Data
public class ActivityGroupResponse {

    private Long id;
    private String groupCode;
    private Long activityId;
    private String activityTitle;

    private String imGroupId;
    private String groupName;
    private String groupAvatar;

    private Long ownerId;
    private Integer memberCount;
    private String status;

    private Boolean locationSharingEnabled;
    private LocalDateTime locationSharingStartTime;
    private LocalDateTime locationSharingEndTime;

    private Double gatheringLongitude;
    private Double gatheringLatitude;
    private String gatheringPlaceName;

    private String announcement;
    private LocalDateTime lastMessageTime;

    private LocalDateTime createTime;
    private Boolean isMember;
}
