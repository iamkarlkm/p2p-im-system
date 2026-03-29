package com.im.location.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 地理围栏响应
 */
@Data
public class GeofenceResponse {
    
    /**
     * 围栏ID
     */
    private String geofenceId;
    
    /**
     * 围栏名称
     */
    private String name;
    
    /**
     * 围栏类型
     */
    private Integer geofenceType;
    
    /**
     * 围栏类型描述
     */
    private String geofenceTypeDesc;
    
    /**
     * 中心点经度
     */
    private Double centerLongitude;
    
    /**
     * 中心点纬度
     */
    private Double centerLatitude;
    
    /**
     * 半径
     */
    private Integer radius;
    
    /**
     * 围栏用途
     */
    private Integer purpose;
    
    /**
     * 围栏用途描述
     */
    private String purposeDesc;
    
    /**
     * 触发事件
     */
    private Integer triggerEvent;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
