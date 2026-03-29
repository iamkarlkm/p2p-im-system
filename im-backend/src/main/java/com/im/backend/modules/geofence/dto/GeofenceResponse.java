package com.im.backend.modules.geofence.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地理围栏响应
 */
@Data
public class GeofenceResponse {

    private Long id;
    private Long merchantId;
    private Long storeId;
    private String name;
    private String type;
    private Double centerLongitude;
    private Double centerLatitude;
    private Double radius;
    private List<Point> polygonPoints;
    private String purpose;
    private String triggerCondition;
    private Integer minTriggerDistance;
    private Integer dwellTimeSeconds;
    private String status;
    private LocalDateTime createTime;

    @Data
    public static class Point {
        private Double lng;
        private Double lat;
    }
}
