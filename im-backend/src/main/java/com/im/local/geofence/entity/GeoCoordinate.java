package com.im.local.geofence.entity;

import lombok.Data;

import javax.persistence.Embeddable;

/**
 * 地理坐标
 */
@Data
@Embeddable
public class GeoCoordinate {
    
    private Double latitude;
    private Double longitude;
    private Integer sequence;
}
