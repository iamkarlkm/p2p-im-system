package com.im.local.geofence.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 位置更新请求
 */
@Data
public class LocationUpdateRequest {
    
    @NotNull(message = "纬度不能为空")
    private Double latitude;
    
    @NotNull(message = "经度不能为空")
    private Double longitude;
    
    private Double accuracy;
    private Double speed;
    private Double heading;
    
    public LocationUpdate toLocationUpdate() {
        return LocationUpdate.builder()
            .latitude(latitude)
            .longitude(longitude)
            .accuracy(accuracy)
            .speed(speed)
            .heading(heading)
            .build();
    }
}
