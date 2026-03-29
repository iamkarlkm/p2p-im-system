package com.im.backend.modules.local_life.checkin.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 签到记录DTO
 */
@Data
public class CheckinRecordDTO {

    private Long id;
    private String poiId;
    private String poiName;
    private String poiType;
    private Integer earnedPoints;
    private Integer streakDays;
    private Boolean firstTimeAtPoi;
    private String checkinDate;
    private LocalDateTime checkinTime;
    private String imageUrl;
    private String remark;
}
