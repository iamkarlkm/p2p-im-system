package com.im.backend.model.poi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * POI签到记录实体
 * 记录用户在特定POI地点的签到行为
 */
@Entity
@Table(name = "poi_checkin_records", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_poi_id", columnList = "poiId"),
    @Index(name = "idx_checkin_time", columnList = "checkinTime"),
    @Index(name = "idx_geohash", columnList = "geoHash")
})
public class PoiCheckinRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false, length = 64)
    private String poiId;

    @Column(nullable = false, length = 200)
    private String poiName;

    @Column(length = 500)
    private String poiAddress;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false, length = 16)
    private String geoHash;

    @Column(nullable = false)
    private LocalDateTime checkinTime;

    @Column
    private Integer pointsEarned;

    @Column(length = 50)
    private String checkinType;

    @Column(length = 500)
    private String checkinContent;

    @Column(length = 200)
    private String imageUrls;

    @Column(nullable = false)
    private Boolean isPublic = true;

    @Column
    private Integer likeCount = 0;

    @Column
    private Integer commentCount = 0;

    @Column(length = 100)
    private String deviceFingerprint;

    @Column(nullable = false)
    private Boolean isValid = true;

    @Column(length = 200)
    private String invalidReason;

    @Column
    private Integer consecutiveDays = 1;

    @Column
    private Boolean isFirstCheckin = false;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (checkinTime == null) {
            checkinTime = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getPoiId() { return poiId; }
    public void setPoiId(String poiId) { this.poiId = poiId; }

    public String getPoiName() { return poiName; }
    public void setPoiName(String poiName) { this.poiName = poiName; }

    public String getPoiAddress() { return poiAddress; }
    public void setPoiAddress(String poiAddress) { this.poiAddress = poiAddress; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public String getGeoHash() { return geoHash; }
    public void setGeoHash(String geoHash) { this.geoHash = geoHash; }

    public LocalDateTime getCheckinTime() { return checkinTime; }
    public void setCheckinTime(LocalDateTime checkinTime) { this.checkinTime = checkinTime; }

    public Integer getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(Integer pointsEarned) { this.pointsEarned = pointsEarned; }

    public String getCheckinType() { return checkinType; }
    public void setCheckinType(String checkinType) { this.checkinType = checkinType; }

    public String getCheckinContent() { return checkinContent; }
    public void setCheckinContent(String checkinContent) { this.checkinContent = checkinContent; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public Boolean getIsPublic() { return isPublic; }
    public void setIsPublic(Boolean isPublic) { this.isPublic = isPublic; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }

    public String getDeviceFingerprint() { return deviceFingerprint; }
    public void setDeviceFingerprint(String deviceFingerprint) { this.deviceFingerprint = deviceFingerprint; }

    public Boolean getIsValid() { return isValid; }
    public void setIsValid(Boolean isValid) { this.isValid = isValid; }

    public String getInvalidReason() { return invalidReason; }
    public void setInvalidReason(String invalidReason) { this.invalidReason = invalidReason; }

    public Integer getConsecutiveDays() { return consecutiveDays; }
    public void setConsecutiveDays(Integer consecutiveDays) { this.consecutiveDays = consecutiveDays; }

    public Boolean getIsFirstCheckin() { return isFirstCheckin; }
    public void setIsFirstCheckin(Boolean isFirstCheckin) { this.isFirstCheckin = isFirstCheckin; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
