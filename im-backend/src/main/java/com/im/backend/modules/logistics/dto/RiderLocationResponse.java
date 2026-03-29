package com.im.backend.modules.logistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手位置响应DTO
 */
@Data
@Schema(description = "骑手位置响应")
public class RiderLocationResponse {

    @Schema(description = "骑手ID")
    private Long riderId;

    @Schema(description = "骑手姓名")
    private String riderName;

    @Schema(description = "经度")
    private BigDecimal longitude;

    @Schema(description = "纬度")
    private BigDecimal latitude;

    @Schema(description = "速度(km/h)")
    private BigDecimal speed;

    @Schema(description = "方向(0-360度)")
    private Integer direction;

    @Schema(description = "地址描述")
    private String address;

    @Schema(description = "位置更新时间")
    private LocalDateTime locationUpdateTime;
}
