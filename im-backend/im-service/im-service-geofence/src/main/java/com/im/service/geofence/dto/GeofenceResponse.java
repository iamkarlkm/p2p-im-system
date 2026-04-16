package com.im.service.geofence.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 地理围栏响应DTO
 */
public class GeofenceResponse {

    private String id;
    private String geofenceId;
    private String name;
    private String description;
    private String fenceType;
    private String merchantId;
    private String poiId;
    private Double centerLatitude;
    private Double centerLongitude;
    private Integer radius;
    private List<Coordinate> polygonPoints;
    private String geoHash;
    private Integer level;
    private String parentId;
    private String triggerCondition;
    private Integer dwellTime;
    private String effectiveStartTime;
    private String effectiveEndTime;
    private String businessHours;
    private String effectiveWeekdays;
    private String status;
    private Boolean enabled;
    private String createTime;
    private String updateTime;
    private List<GeofenceResponse> subGeofences;

    // 内部类：坐标点
    public static class Coordinate {
        private Double lat;
        private Double lng;

        public Coordinate() {}
        public Coordinate(Double lat, Double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        public Double getLat() { return lat; }
        public void setLat(Double lat) { this.lat = lat; }
        public Double getLng() { return lng; }
        public void setLng(Double lng) { this.lng = lng; }
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getGeofenceId() { return geofenceId; }
    public void setGeofenceId(String geofenceId) { this.geofenceId = geofenceId; }

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

    public List<Coordinate> getPolygonPoints() { return polygonPoints; }
    public void setPolygonPoints(List<Coordinate> polygonPoints) { this.polygonPoints = polygonPoints; }

    public String getGeoHash() { return geoHash; }
    public void setGeoHash(String geoHash) { this.geoHash = geoHash; }

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

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }

    public String getUpdateTime() { return updateTime; }
    public void setUpdateTime(String updateTime) { this.updateTime = updateTime; }

    public List<GeofenceResponse> getSubGeofences() { return subGeofences; }
    public void setSubGeofences(List<GeofenceResponse> subGeofences) { this.subGeofences = subGeofences; }
}
