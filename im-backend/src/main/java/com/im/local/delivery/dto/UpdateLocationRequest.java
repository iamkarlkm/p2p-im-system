package com.im.local.delivery.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 更新位置请求
 */
@Data
public class UpdateLocationRequest {
    
    @NotNull(message = "纬度不能为空")
    private Double latitude;
    
    @NotNull(message = "经度不能为空")
    private Double longitude;
    
    private Double accuracy;
    private Double speed;
    private Double heading;
}
