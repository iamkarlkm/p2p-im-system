package com.im.backend.modules.local.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 地理围栏响应
 */
@Schema(description = "地理围栏响应")
public class GeofenceResponse {
    
    @Schema(description = "围栏ID")
    private String id;
    
    @Schema(description = "围栏名称")
    private String name;
    
    @Schema(description = "围栏描述")
    private String description;
    
    @Schema(description = "围栏类型：1-圆形，2-多边形")
    private Integer type;
    
    @Schema(description = "圆心经度")
    private BigDecimal centerLongitude;
    
    @Schema(description = "圆心纬度")
    private BigDecimal centerLatitude;
    
    @Schema(description = "半径(米)")
    private Integer radius;
    
    @Schema(description = "多边形顶点")
    private String polygonPoints;
    
    @Schema(description = "关联商户ID")
    private String merchantId;
    
    @Schema(description = "围栏层级")
    private Integer level;
    
    @Schema(description = "当前状态：0-禁用，1-启用")
    private Integer status;
    
    @Schema(description = "动态调整策略")
    private String adjustStrategy;
    
    @Schema(description = "当前订单数")
    private Integer currentOrderCount;
    
    @Schema(description = "可用服务人员数")
    private Integer availableStaffCount;
    
    @Schema(description = "围栏内运力负载")
    private Integer capacityLoad;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
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
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    
    public String getAdjustStrategy() { return adjustStrategy; }
    public void setAdjustStrategy(String adjustStrategy) { this.adjustStrategy = adjustStrategy; }
    
    public Integer getCurrentOrderCount() { return currentOrderCount; }
    public void setCurrentOrderCount(Integer currentOrderCount) { this.currentOrderCount = currentOrderCount; }
    
    public Integer getAvailableStaffCount() { return availableStaffCount; }
    public void setAvailableStaffCount(Integer availableStaffCount) { this.availableStaffCount = availableStaffCount; }
    
    public Integer getCapacityLoad() { return capacityLoad; }
    public void setCapacityLoad(Integer capacityLoad) { this.capacityLoad = capacityLoad; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
