package com.im.backend.modules.geofencing.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 位置上报DTO
 */
@Data
@Schema(description = "位置上报请求")
public class LocationReportDTO {
    
    @NotNull(message = "用户ID不能为空")
    @Schema(description = "用户ID")
    private Long userId;
    
    @NotNull(message = "经度不能为空")
    @Schema(description = "经度")
    private BigDecimal longitude;
    
    @NotNull(message = "纬度不能为空")
    @Schema(description = "纬度")
    private BigDecimal latitude;
    
    @Schema(description = "定位精度（米）")
    private BigDecimal accuracy;
    
    @Schema(description = "定位来源: GPS, NETWORK, WIFI, PASSIVE")
    private String source;
    
    @Schema(description = "海拔高度（米）")
    private BigDecimal altitude;
    
    @Schema(description = "速度（米/秒）")
    private BigDecimal speed;
    
    @Schema(description = "方向（度）")
    private BigDecimal bearing;
    
    @Schema(description = "设备ID")
    private String deviceId;
    
    @Schema(description = "应用版本")
    private String appVersion;
}
