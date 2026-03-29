package com.im.backend.modules.geofence.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 创建地理围栏请求
 */
@Data
public class CreateGeofenceRequest {

    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    private Long storeId;

    @NotBlank(message = "围栏名称不能为空")
    private String name;

    @NotBlank(message = "围栏类型不能为空")
    private String type;

    /** 圆形围栏中心点 */
    private Double centerLongitude;
    private Double centerLatitude;
    private Double radius;

    /** 多边形围栏顶点 */
    private List<Point> polygonPoints;

    @NotBlank(message = "围栏用途不能为空")
    private String purpose;

    @NotBlank(message = "触发条件不能为空")
    private String triggerCondition;

    private Integer minTriggerDistance = 10;

    private Integer dwellTimeSeconds;

    @Data
    public static class Point {
        private Double lng;
        private Double lat;
    }
}
