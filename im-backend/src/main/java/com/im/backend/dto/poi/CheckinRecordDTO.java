package com.im.backend.dto.poi;

import java.time.LocalDateTime;

/**
 * 签到记录DTO
 */
public class CheckinRecordDTO {
    
    private Long id;
    private Long userId;
    private String poiId;
    private String poiName;
    private String poiAddress;
    private Double longitude;
    private Double latitude;
    private LocalDateTime checkinTime;
    private Integer pointsEarned;
    private String checkinContent;
    private String imageUrls;
    private Integer consecutiveDays;
    private Boolean isFirstCheckin;
    private Integer likeCount;
    private Integer commentCount;

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

    public LocalDateTime getCheckinTime() { return checkinTime; }
    public void setCheckinTime(LocalDateTime checkinTime) { this.checkinTime = checkinTime; }

    public Integer getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(Integer pointsEarned) { this.pointsEarned = pointsEarned; }

    public String getCheckinContent() { return checkinContent; }
    public void setCheckinContent(String checkinContent) { this.checkinContent = checkinContent; }

    public String getImageUrls() { return imageUrls; }
    public void setImageUrls(String imageUrls) { this.imageUrls = imageUrls; }

    public Integer getConsecutiveDays() { return consecutiveDays; }
    public void setConsecutiveDays(Integer consecutiveDays) { this.consecutiveDays = consecutiveDays; }

    public Boolean getIsFirstCheckin() { return isFirstCheckin; }
    public void setIsFirstCheckin(Boolean isFirstCheckin) { this.isFirstCheckin = isFirstCheckin; }

    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }

    public Integer getCommentCount() { return commentCount; }
    public void setCommentCount(Integer commentCount) { this.commentCount = commentCount; }
}
