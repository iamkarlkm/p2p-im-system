package com.im.service.geofence.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 创建地理围栏请求DTO
 */
public class CreateGeofenceRequest {

    @NotBlank(message = "围栏名称不能为空")
    private String name;

    private String description;

    @NotBlank(message = "围栏类型不能为空")
    private String fenceType; // CIRCLE, POLYGON, POLYLINE

    private String merchantId;

    private String poiId;

    private Double centerLatitude;

    private Double centerLongitude;

    private Integer radius;

    private List<Map<String, Double>> polygonPoints; // [{"lat": xxx, "lng": xxx}, ...]

    private Integer level;

    private String parentId;

    private String triggerCondition; // ENTER, EXIT, DWELL

    private Integer dwellTime;

    private String effectiveStartTime;

    private String effectiveEndTime;

    private String businessHours;

    private String effectiveWeekdays;

    private Boolean enabled;

    // Getters and Setters

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getFenceType() { return fenceType; }
    public void setFenceType(String fenceType) { this.fenceType = fenceType; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getPoiId() { return poiId; }
    public void setPoiId(String poiId) { this.poiId = poiId; }

    public Double getCenterLatitude() { return centerLatitude; }
    public void setCenterLatitude(Double centerLatitude) { this.centerLatitude = centerLatitude; }

    public Double getCenterLongitude() { return centerLongitude; }
    public void setCenterLongitude(Double centerLongitude) { this.centerLongitude = centerLongitude; }

    public Integer getRadius() { return radius; }
    public void setRadius(Integer radius) { this.radius = radius; }

    public List<Map<String, Double>> getPolygonPoints() { return polygonPoints; }
    public void setPolygonPoints(List<Map<String, Double>> polygonPoints) { this.polygonPoints = polygonPoints; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }

    public String getTriggerCondition() { return triggerCondition; }
    public void setTriggerCondition(String triggerCondition) { this.triggerCondition = triggerCondition; }

    public Integer getDwellTime() { return dwellTime; }
    public void setDwellTime(Integer dwellTime) { this.dwellTime = dwellTime; }

    public String getEffectiveStartTime() { return effectiveStartTime; }
    public void setEffectiveStartTime(String effectiveStartTime) { this.effectiveStartTime = effectiveStartTime; }

    public String getEffectiveEndTime() { return effectiveEndTime; }
    public void setEffectiveEndTime(String effectiveEndTime) { this.effectiveEndTime = effectiveEndTime; }

    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }

    public String getEffectiveWeekdays() { return effectiveWeekdays; }
    public void setEffectiveWeekdays(String effectiveWeekdays) { this.effectiveWeekdays = effectiveWeekdays; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
}
