package com.im.backend.modules.geofence.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 上报位置请求
 */
@Data
public class LocationReportRequest {

    @NotNull(message = "经度不能为空")
    private Double longitude;

    @NotNull(message = "纬度不能为空")
    private Double latitude;

    /** 定位精度(米) */
    private Double accuracy;

    /** 速度(m/s) */
    private Double speed;

    /** 方向(0-360度) */
    private Double bearing;

    /** 海拔高度 */
    private Double altitude;

    /** 设备信息 */
    private String deviceInfo;

    /** 时间戳 */
    private Long timestamp;
}
