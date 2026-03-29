package com.im.backend.modules.local_life.checkin.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 签到请求DTO
 */
@Data
public class CheckinRequest {

    /**
     * POI ID
     */
    @NotBlank(message = "POI ID不能为空")
    private String poiId;

    /**
     * POI名称
     */
    @NotBlank(message = "POI名称不能为空")
    private String poiName;

    /**
     * POI类型
     */
    private String poiType;

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
     * 设备ID
     */
    private String deviceId;

    /**
     * 签到备注
     */
    private String remark;

    /**
     * 签到图片Base64
     */
    private String imageBase64;
}
