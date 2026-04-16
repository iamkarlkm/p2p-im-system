package com.im.service.geofence.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 地理围栏实体
 * 支持圆形、多边形、路线围栏
 * 
 * @author IM Development Team
 * @since 2026-04-12
 */
@Entity
@Table(name = "im_geofence")
public class Geofence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "geofence_id", unique = true, length = 64)
    private String geofenceId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * 围栏类型: CIRCLE-圆形, POLYGON-多边形, POLYLINE-路线
     */
    @Column(name = "fence_type", length = 20)
    private String fenceType;

    /**
     * 关联商户ID
     */
    @Column(name = "merchant_id", length = 64)
    private String merchantId;

    /**
     * 关联POI ID
     */
    @Column(name = "poi_id", length = 64)
    private String poiId;

    /**
     * 围栏中心纬度
     */
    @Column(name = "center_latitude")
    private Double centerLatitude;

    /**
     * 围栏中心经度
     */
    @Column(name = "center_longitude")
    private Double centerLongitude;

    /**
     * 半径(米)，仅圆形围栏使用
     */
    @Column(name = "radius")
    private Integer radius;

    /**
     * 多边形/路线坐标点，JSON格式: [{"lat": xxx, "lng": xxx}, ...]
     */
    @Column(name = "coordinates", columnDefinition = "TEXT")
    private String coordinates;

    /**
     * GeoHash编码
     */
    @Column(name = "geo_hash", length = 20)
    private String geoHash;

    /**
     * 围栏层级
     */
    @Column(name = "level")
    private Integer level;

    /**
     * 父围栏ID
     */
    @Column(name = "parent_id", length = 64)
    private String parentId;

    /**
     * 触发条件: ENTER-进入, EXIT-离开, DWELL-停留
     */
    @Column(name = "trigger_condition", length = 20)
    private String triggerCondition;

    /**
     * 停留时间(秒)，仅DWELL类型使用
     */
    @Column(name = "dwell_time")
    private Integer dwellTime;

    /**
     * 生效开始时间
     */
    @Column(name = "effective_start_time")
    private LocalDateTime effectiveStartTime;

    /**
     * 生效结束时间
     */
    @Column(name = "effective_end_time")
    private LocalDateTime effectiveEndTime;

    /**
     * 营业时间，JSON格式: {"Mon": "09:00-21:00", ...}
     */
    @Column(name = "business_hours", length = 500)
    private String businessHours;

    /**
     * 有效工作日，逗号分隔: "Mon,Tue,Wed,Thu,Fri"
     */
    @Column(name = "effective_weekdays", length = 50)
    private String effectiveWeekdays;

    /**
     * 围栏状态: ACTIVE-激活, INACTIVE-未激活, PAUSED-暂停, EXPIRED-过期
     */
    @Column(length = 20)
    private String status;

    /**
     * 是否启用
     */
    @Column(name = "is_enabled")
    private Boolean enabled;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getCoordinates() { return coordinates; }
    public void setCoordinates(String coordinates) { this.coordinates = coordinates; }

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

    public LocalDateTime getEffectiveStartTime() { return effectiveStartTime; }
    public void setEffectiveStartTime(LocalDateTime effectiveStartTime) { this.effectiveStartTime = effectiveStartTime; }

    public LocalDateTime getEffectiveEndTime() { return effectiveEndTime; }
    public void setEffectiveEndTime(LocalDateTime effectiveEndTime) { this.effectiveEndTime = effectiveEndTime; }

    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }

    public String getEffectiveWeekdays() { return effectiveWeekdays; }
    public void setEffectiveWeekdays(String effectiveWeekdays) { this.effectiveWeekdays = effectiveWeekdays; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }

    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
