package com.im.backend.modules.delivery.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 位置更新DTO
 */
@Data
public class LocationUpdateDTO {

    @NotNull(message = "纬度不能为空")
    private BigDecimal lat;

    @NotNull(message = "经度不能为空")
    private BigDecimal lng;

    /** 精度(米) */
    private BigDecimal accuracy;

    /** 海拔 */
    private BigDecimal altitude;

    /** 速度(m/s) */
    private BigDecimal speed;

    /** 方向(0-360) */
    private BigDecimal bearing;

    /** 设备电量 */
    private Integer batteryLevel;

    /** 位置来源 */
    private String source = "GPS";
}
