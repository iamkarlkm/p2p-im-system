package com.im.backend.modules.location.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 围栏事件DTO
 */
@Data
public class GeofenceEventDTO {

    /**
     * 事件ID
     */
    private Long id;

    /**
     * 触发用户ID
     */
    private Long userId;

    /**
     * 触发用户昵称
     */
    private String userNickname;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 围栏名称
     */
    private String geofenceName;

    /**
     * 触发时纬度
     */
    private Double triggerLat;

    /**
     * 触发时经度
     */
    private Double triggerLng;

    /**
     * 触发时间
     */
    private LocalDateTime triggerTime;

    /**
     * IM消息ID
     */
    private String imMessageId;
}
