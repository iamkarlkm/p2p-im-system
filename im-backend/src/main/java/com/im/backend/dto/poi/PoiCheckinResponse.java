package com.im.backend.dto.poi;

import java.time.LocalDateTime;

/**
 * POI签到响应DTO
 */
public class PoiCheckinResponse {
    
    private Long checkinId;
    private String poiId;
    private String poiName;
    private String poiAddress;
    private LocalDateTime checkinTime;
    private Integer pointsEarned;
    private Integer consecutiveDays;
    private Boolean isFirstCheckin;
    private String achievementUnlocked;
    private String message;
    private Boolean success;

    // Getters and Setters
    public Long getCheckinId() { return checkinId; }
    public void setCheckinId(Long checkinId) { this.checkinId = checkinId; }

    public String getPoiId() { return poiId; }
    public void setPoiId(String poiId) { this.poiId = poiId; }

    public String getPoiName() { return poiName; }
    public void setPoiName(String poiName) { this.poiName = poiName; }

    public String getPoiAddress() { return poiAddress; }
    public void setPoiAddress(String poiAddress) { this.poiAddress = poiAddress; }

    public LocalDateTime getCheckinTime() { return checkinTime; }
    public void setCheckinTime(LocalDateTime checkinTime) { this.checkinTime = checkinTime; }

    public Integer getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(Integer pointsEarned) { this.pointsEarned = pointsEarned; }

    public Integer getConsecutiveDays() { return consecutiveDays; }
    public void setConsecutiveDays(Integer consecutiveDays) { this.consecutiveDays = consecutiveDays; }

    public Boolean getIsFirstCheckin() { return isFirstCheckin; }
    public void setIsFirstCheckin(Boolean isFirstCheckin) { this.isFirstCheckin = isFirstCheckin; }

    public String getAchievementUnlocked() { return achievementUnlocked; }
    public void setAchievementUnlocked(String achievementUnlocked) { this.achievementUnlocked = achievementUnlocked; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
}
