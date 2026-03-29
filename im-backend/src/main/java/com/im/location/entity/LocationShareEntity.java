package com.im.location.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "location_share")
public class LocationShareEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;
    
    @Column(name = "conversation_id")
    private UUID conversationId;
    
    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private Double latitude;
    
    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private Double longitude;
    
    @Column(name = "accuracy", precision = 6, scale = 2)
    private Double accuracy;
    
    @Column(name = "altitude", precision = 8, scale = 2)
    private Double altitude;
    
    @Column(name = "bearing", precision = 5, scale = 2)
    private Double bearing;
    
    @Column(name = "speed", precision = 5, scale = 2)
    private Double speed;
    
    @Column(name = "address")
    private String address;
    
    @Column(name = "location_name")
    private String locationName;
    
    @Column(name = "share_type", nullable = false)
    private String shareType; // STATIC, REALTIME, GEOFENCE
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "update_interval_seconds")
    private Integer updateIntervalSeconds;
    
    @Column(name = "last_update_at")
    private LocalDateTime lastUpdateAt;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "ended_at")
    private LocalDateTime endedAt;
    
    @Column(name = "privacy_level", nullable = false)
    private String privacyLevel; // PRECISE, APPROXIMATE, CITY_ONLY
    
    @Column(name = "background_tracking", nullable = false)
    private Boolean backgroundTracking;
    
    @Column(name = "battery_optimization", nullable = false)
    private Boolean batteryOptimization;
    
    @Column(name = "notification_enabled", nullable = false)
    private Boolean notificationEnabled;
    
    public LocationShareEntity() {
        this.isActive = true;
        this.privacyLevel = "PRECISE";
        this.backgroundTracking = false;
        this.batteryOptimization = true;
        this.notificationEnabled = true;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public UUID getRecipientId() { return recipientId; }
    public void setRecipientId(UUID recipientId) { this.recipientId = recipientId; }
    
    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }
    
    public Double getAltitude() { return altitude; }
    public void setAltitude(Double altitude) { this.altitude = altitude; }
    
    public Double getBearing() { return bearing; }
    public void setBearing(Double bearing) { this.bearing = bearing; }
    
    public Double getSpeed() { return speed; }
    public void setSpeed(Double speed) { this.speed = speed; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    
    public String getShareType() { return shareType; }
    public void setShareType(String shareType) { this.shareType = shareType; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    
    public Integer getUpdateIntervalSeconds() { return updateIntervalSeconds; }
    public void setUpdateIntervalSeconds(Integer updateIntervalSeconds) { this.updateIntervalSeconds = updateIntervalSeconds; }
    
    public LocalDateTime getLastUpdateAt() { return lastUpdateAt; }
    public void setLastUpdateAt(LocalDateTime lastUpdateAt) { this.lastUpdateAt = lastUpdateAt; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
    
    public String getPrivacyLevel() { return privacyLevel; }
    public void setPrivacyLevel(String privacyLevel) { this.privacyLevel = privacyLevel; }
    
    public Boolean getBackgroundTracking() { return backgroundTracking; }
    public void setBackgroundTracking(Boolean backgroundTracking) { this.backgroundTracking = backgroundTracking; }
    
    public Boolean getBatteryOptimization() { return batteryOptimization; }
    public void setBatteryOptimization(Boolean batteryOptimization) { this.batteryOptimization = batteryOptimization; }
    
    public Boolean getNotificationEnabled() { return notificationEnabled; }
    public void setNotificationEnabled(Boolean notificationEnabled) { this.notificationEnabled = notificationEnabled; }
    
    @PreUpdate
    public void preUpdate() {
        this.lastUpdateAt = LocalDateTime.now();
    }
}