package com.im.service.geofence.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 地理围栏事件记录
 * 记录用户进入/离开围栏的事件
 * 
 * @author IM Development Team
 * @since 2026-04-12
 */
@Entity
@Table(name = "im_geofence_event")
public class GeofenceEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", unique = true, length = 64)
    private String eventId;

    @Column(name = "geofence_id", nullable = false, length = 64)
    private String geofenceId;

    @Column(name = "user_id", nullable = false, length = 64)
    private String userId;

    /**
     * 事件类型: ENTER-进入, EXIT-离开, DWELL-停留
     */
    @Column(name = "event_type", length = 20)
    private String eventType;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column
    private Double accuracy;

    @Column
    private Double speed;

    @Column
    private Double bearing;

    @Column(name = "device_id", length = 64)
    private String deviceId;

    @Column(name = "app_version", length = 20)
    private String appVersion;

    /**
     * 事件时间
     */
    @Column(name = "event_time")
    private LocalDateTime eventTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getGeofenceId() { return geofenceId; }
    public void setGeofenceId(String geofenceId) { this.geofenceId = geofenceId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }

    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }

    public Double getBearing() { return bearing; }
    public void setBearing(Double bearing) { this.bearing = bearing; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getAppVersion() { return appVersion; }
    public void setAppVersion(String appVersion) { this.appVersion = appVersion; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }

    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}
