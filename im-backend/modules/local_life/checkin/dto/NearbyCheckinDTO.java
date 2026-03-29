package com.im.backend.modules.local_life.checkin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 附近签到DTO
 */
@Data
public class NearbyCheckinDTO {

    private Long checkinId;
    private Long userId;
    private String userNickname;
    private String userAvatar;
    private String poiName;
    private String poiType;
    private Double longitude;
    private Double latitude;
    private String content;
    private String imageUrl;
    private LocalDateTime checkinTime;
    private Double distance;
}
