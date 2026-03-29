package com.im.location.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 更新位置请求
 */
@Data
public class UpdateLocationRequest {
    
    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;
    
    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    private Double longitude;
    
    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    private Double latitude;
    
    /**
     * 位置精度(米)
     */
    private Double accuracy;
    
    /**
     * 海拔高度
     */
    private Double altitude;
    
    /**
     * 移动速度(m/s)
     */
    private Double speed;
    
    /**
     * 移动方向(0-360度)
     */
    private Double bearing;
    
    /**
     * 电量百分比
     */
    private Integer batteryLevel;
}
