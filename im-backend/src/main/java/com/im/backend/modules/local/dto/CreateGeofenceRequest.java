package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 创建地理围栏请求
 */
@Schema(description = "创建地理围栏请求")
public class CreateGeofenceRequest {
    
    @NotBlank(message = "围栏名称不能为空")
    @Schema(description = "围栏名称")
    private String name;
    
    @Schema(description = "围栏描述")
    private String description;
    
    @NotNull(message = "围栏类型不能为空")
    @Schema(description = "围栏类型：1-圆形，2-多边形")
    private Integer type;
    
    @Schema(description = "圆心经度(圆形围栏)")
    private BigDecimal centerLongitude;
    
    @Schema(description = "圆心纬度(圆形围栏)")
    private BigDecimal centerLatitude;
    
    @Schema(description = "半径(米，圆形围栏)")
    private Integer radius;
    
    @Schema(description = "多边形顶点(多边形围栏)")
    private String polygonPoints;
    
    @NotBlank(message = "商户ID不能为空")
    @Schema(description = "关联商户ID")
    private String merchantId;
    
    @Schema(description = "围栏层级")
    private Integer level;
    
    @Schema(description = "动态调整策略")
    private String adjustStrategy;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
    
    public BigDecimal getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(BigDecimal centerLongitude) { this.centerLongitude = centerLongitude; }
    
    public BigDecimal getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(BigDecimal centerLatitude) { this.centerLatitude = centerLatitude; }
    
    public Integer getRadius() { return radius; }
    public void setRadius(Integer radius) { this.radius = radius; }
    
    public String getPolygonPoints() { return polygonPoints; }
    public void setPolygonPoints(String polygonPoints) { this.polygonPoints = polygonPoints; }
    
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    
    public String getAdjustStrategy() { return adjustStrategy; }
    public void setAdjustStrategy(String adjustStrategy) { this.adjustStrategy = adjustStrategy; }
}
