package com.im.backend.modules.local.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 地理围栏实体
 */
@TableName("geofence")
public class Geofence implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    private String name;
    
    private String description;
    
    private Integer type;
    
    private BigDecimal centerLongitude;
    
    private BigDecimal centerLatitude;
    
    private Integer radius;
    
    private String polygonPoints;
    
    private String merchantId;
    
    private Integer level;
    
    private Integer status;
    
    private String adjustStrategy;
    
    private Integer currentOrderCount;
    
    private Integer availableStaffCount;
    
    private Integer capacityLoad;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    @TableLogic
    private Integer deleted;
    
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
    
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
}
