package com.im.backend.modules.logistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手位置上报请求DTO
 */
@Data
@Schema(description = "骑手位置上报请求")
public class RiderLocationReportRequest {

    @Schema(description = "骑手ID", required = true)
    private Long riderId;

    @Schema(description = "订单ID")
    private Long orderId;

    @Schema(description = "经度", required = true)
    private BigDecimal longitude;

    @Schema(description = "纬度", required = true)
    private BigDecimal latitude;

    @Schema(description = "速度(km/h)")
    private BigDecimal speed;

    @Schema(description = "方向(0-360度)")
    private Integer direction;

    @Schema(description = "精度(米)")
    private BigDecimal accuracy;

    @Schema(description = "海拔(米)")
    private BigDecimal altitude;

    @Schema(description = "地址描述")
    private String address;

    @Schema(description = "上报时间")
    private LocalDateTime reportTime;
}
