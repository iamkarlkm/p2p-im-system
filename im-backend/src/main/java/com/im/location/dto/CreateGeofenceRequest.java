package com.im.location.dto;

import lombok.Data;

/**
 * 创建地理围栏请求
 */
@Data
public class CreateGeofenceRequest {
    
    /**
     * 围栏名称
     */
    private String name;
    
    /**
     * 围栏类型: 1-圆形 2-多边形
     */
    private Integer geofenceType;
    
    /**
     * 关联会话ID
     */
    private String sessionId;
    
    /**
     * 中心点经度(圆形围栏)
     */
    private Double centerLongitude;
    
    /**
     * 中心点纬度(圆形围栏)
     */
    private Double centerLatitude;
    
    /**
     * 半径(米，圆形围栏)
     */
    private Integer radius;
    
    /**
     * 多边形坐标JSON(多边形围栏)
     */
    private String polygonCoordinates;
    
    /**
     * 围栏用途: 1-目的地 2-安全区 3-禁入区 4-提醒点
     */
    private Integer purpose;
    
    /**
     * 触发事件: 1-进入 2-离开 3-停留
     */
    private Integer triggerEvent;
    
    /**
     * 停留触发时长(分钟，停留触发时)
     */
    private Integer dwellTime;
}
