package com.im.backend.modules.geofence.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 到店记录响应
 */
@Data
public class ArrivalRecordResponse {

    private Long id;
    private Long userId;
    private Long merchantId;
    private Long storeId;
    private String merchantName;
    private String storeName;
    private LocalDateTime enterTime;
    private LocalDateTime leaveTime;
    private Integer stayDurationMinutes;
    private Integer arrivalCount;
    private String memberLevel;
    private String customerTag;
    private Boolean servicePushed;
    private LocalDateTime createTime;
}
