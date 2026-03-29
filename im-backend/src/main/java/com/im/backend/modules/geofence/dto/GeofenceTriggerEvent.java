package com.im.backend.modules.geofence.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 围栏触发事件响应
 */
@Data
public class GeofenceTriggerEvent {

    private Long geofenceId;
    private Long merchantId;
    private Long storeId;
    private String geofenceName;
    private String triggerType;
    private Double longitude;
    private Double latitude;
    private Double distanceFromBoundary;
    private Integer confidence;
    private LocalDateTime triggerTime;
    private Boolean isEnter;

    /** 个性化服务信息 */
    private PersonalizedService service;

    @Data
    public static class PersonalizedService {
        private String welcomeMessage;
        private String couponCode;
        private String memberLevel;
        private List<String> recommendations;
    }
}
