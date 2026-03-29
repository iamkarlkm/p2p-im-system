package com.im.local.geofence.dto;

import com.im.local.geofence.enums.*;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 创建地理围栏请求
 */
@Data
public class CreateGeofenceRequest {
    
    @NotBlank(message = "围栏名称不能为空")
    private String name;
    
    @NotNull(message = "围栏类型不能为空")
    private GeofenceType type;
    
    @NotNull(message = "围栏形状不能为空")
    private GeofenceShape shape;
    
    // 圆形围栏参数
    private Double centerLatitude;
    private Double centerLongitude;
    private Double radius;
    
    // 多边形围栏参数
    private List<GeoCoordinateDTO> coordinates;
    
    private Long ownerId;
    private OwnerType ownerType;
    private Long targetId;
    private TargetType targetType;
    
    private List<GeofenceEventType> triggerEvents;
    private List<TriggerAction> triggerActions;
    
    private Integer dwellTimeMinutes;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
