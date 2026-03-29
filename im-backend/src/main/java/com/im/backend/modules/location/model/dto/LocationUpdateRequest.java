package com.im.backend.modules.location.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 位置更新请求DTO
 */
@Data
public class LocationUpdateRequest {

    /**
     * 会话ID
     */
    @NotBlank(message = "会话ID不能为空")
    private String sessionId;

    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    private Double latitude;

    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    private Double longitude;

    /**
     * 精度(米)
     */
    private Double accuracy;

    /**
     * 海拔(米)
     */
    private Double altitude;

    /**
     * 速度(m/s)
     */
    private Double speed;

    /**
     * 方向(0-360度)
     */
    private Double bearing;

    /**
     * 电池电量(百分比)
     */
    private Integer batteryLevel;

    /**
     * 网络类型: WIFI-4G-5G
     */
    private String networkType;

    /**
     * 定位方式: GPS-卫星, NETWORK-网络, HYBRID-混合
     */
    private String locationProvider;

    /**
     * 是否移动中
     */
    private Boolean isMoving;
}
