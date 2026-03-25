package com.im.location.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "geofence")
public class GeofenceEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "latitude", nullable = false, precision = 10, scale = 8)
    private Double latitude;
    
    @Column(name = "longitude", nullable = false, precision = 11, scale = 8)
    private Double longitude;
    
    @Column(name = "radius_meters", nullable = false)
    private Double radiusMeters;
    
    @Column(name = "geofence_type", nullable = false)
    private String geofenceType; // CIRCLE, POLYGON
    
    @Column(name = "polygon_coordinates")
    private String polygonCoordinates; // JSON array of lat/lon pairs
    
    @Column(name = "trigger_type", nullable = false)
    private String triggerType; // ENTER, EXIT, DWELL
    
    @Column(name = "dwell_time_minutes")
    private Integer dwellTimeMinutes;
    
    @Column(name = "notification_message")
    private String notificationMessage;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive;
    
    @Column(name = "priority", nullable = false)
    private Integer priority;
    
    @Column(name = "notify_on_enter", nullable = false)
    private Boolean notifyOnEnter;
    
    @Column(name = "notify_on_exit", nullable = false)
    private Boolean notifyOnExit;
    
    @Column(name = "notify_on_dwell", nullable = false)
    private Boolean notifyOnDwell;
    
    @Column(name = "recipients")
    private String recipients; // JSON array of user IDs
    
    @Column(name = "conversation_id")
    private UUID conversationId;
    
    @Column(name = "webhook_url")
    private String webhookUrl;
    
    @Column(name = "webhook_enabled", nullable = false)
    private Boolean webhookEnabled;
    
    @Column(name = "last_triggered_at")
    private LocalDateTime lastTriggeredAt;
    
    @Column(name = "trigger_count", nullable = false)
    private Integer triggerCount;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @Column(name = "color")
    private String color;
    
    @Column(name = "icon")
    private String icon;
    
    public GeofenceEntity() {
        this.isActive = true;
        this.priority = 0;
        this.notifyOnEnter = true;
        this.notifyOnExit = false;
        this.notifyOnDwell = false;
        this.triggerCount = 0;
        this.webhookEnabled = false;
        this.geofenceType = "CIRCLE";
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public Double getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(Double radiusMeters) { this.radiusMeters = radiusMeters; }
    
    public String getGeofenceType() { return geofenceType; }
    public void setGeofenceType(String geofenceType) { this.geofenceType = geofenceType; }
    
    public String getPolygonCoordinates() { return polygonCoordinates; }
    public void setPolygonCoordinates(String polygonCoordinates) { this.polygonCoordinates = polygonCoordinates; }
    
    public String getTriggerType() { return triggerType; }
    public void setTriggerType(String triggerType) { this.triggerType = triggerType; }
    
    public Integer getDwellTimeMinutes() { return dwellTimeMinutes; }
    public void setDwellTimeMinutes(Integer dwellTimeMinutes) { this.dwellTimeMinutes = dwellTimeMinutes; }
    
    public String getNotificationMessage() { return notificationMessage; }
    public void setNotificationMessage(String notificationMessage) { this.notificationMessage = notificationMessage; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    
    public Boolean getNotifyOnEnter() { return notifyOnEnter; }
    public void setNotifyOnEnter(Boolean notifyOnEnter) { this.notifyOnEnter = notifyOnEnter; }
    
    public Boolean getNotifyOnExit() { return notifyOnExit; }
    public void setNotifyOnExit(Boolean notifyOnExit) { this.notifyOnExit = notifyOnExit; }
    
    public Boolean getNotifyOnDwell() { return notifyOnDwell; }
    public void setNotifyOnDwell(Boolean notifyOnDwell) { this.notifyOnDwell = notifyOnDwell; }
    
    public String getRecipients() { return recipients; }
    public void setRecipients(String recipients) { this.recipients = recipients; }
    
    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }
    
    public String getWebhookUrl() { return webhookUrl; }
    public void setWebhookUrl(String webhookUrl) { this.webhookUrl = webhookUrl; }
    
    public Boolean getWebhookEnabled() { return webhookEnabled; }
    public void setWebhookEnabled(Boolean webhookEnabled) { this.webhookEnabled = webhookEnabled; }
    
    public LocalDateTime getLastTriggeredAt() { return lastTriggeredAt; }
    public void setLastTriggeredAt(LocalDateTime lastTriggeredAt) { this.lastTriggeredAt = lastTriggeredAt; }
    
    public Integer getTriggerCount() { return triggerCount; }
    public void setTriggerCount(Integer triggerCount) { this.triggerCount = triggerCount; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}