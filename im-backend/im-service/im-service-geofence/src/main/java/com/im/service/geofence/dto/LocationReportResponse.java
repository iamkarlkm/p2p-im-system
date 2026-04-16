package com.im.service.geofence.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 位置上报响应DTO
 */
public class LocationReportResponse {

    private String userId;
    private Double longitude;
    private Double latitude;
    private List<GeofenceHit> hitGeofences;
    private Integer hitCount;
    private String timestamp;

    public static class GeofenceHit {
        private String geofenceId;
        private String geofenceName;
        private String eventType; // ENTER, EXIT
        private Double distance;

        public String getGeofenceId() { return geofenceId; }
        public void setGeofenceId(String geofenceId) { this.geofenceId = geofenceId; }

        public String getGeofenceName() { return geofenceName; }
        public void setGeofenceName(String geofenceName) { this.geofenceName = geofenceName; }

        public String getEventType() { return eventType; }
        public void setEventType(String eventType) { this.eventType = eventType; }

        public Double getDistance() { return distance; }
        public void setDistance(Double distance) { this.distance = distance; }
    }

    // Getters and Setters

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public List<GeofenceHit> getHitGeofences() { return hitGeofences; }
    public void setHitGeofences(List<GeofenceHit> hitGeofences) { this.hitGeofences = hitGeofences; }

    public Integer getHitCount() { return hitCount; }
    public void setHitCount(Integer hitCount) { this.hitCount = hitCount; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
