package com.im.backend.model.poi;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 商户签到活动配置实体
 * 商家可配置的签到活动规则
 */
@Entity
@Table(name = "merchant_checkin_activities", indexes = {
    @Index(name = "idx_merchant_id", columnList = "merchantId"),
    @Index(name = "idx_status", columnList = "status")
})
public class MerchantCheckinActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long merchantId;

    @Column(nullable = false, length = 64)
    private String poiId;

    @Column(nullable = false, length = 200)
    private String activityName;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private Integer basePoints = 10;

    @Column(nullable = false)
    private Integer doublePointsDays = 0;

    @Column(length = 50)
    private String bonusDays;

    @Column(nullable = false)
    private Integer dailyLimit = 1;

    @Column(nullable = false)
    private Integer totalLimit = 0;

    @Column(nullable = false)
    private Integer checkinCount = 0;

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(length = 200)
    private String couponReward;

    @Column
    private Integer minDistanceMeters = 100;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public String getPoiId() { return poiId; }
    public void setPoiId(String poiId) { this.poiId = poiId; }

    public String getActivityName() { return activityName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getBasePoints() { return basePoints; }
    public void setBasePoints(Integer basePoints) { this.basePoints = basePoints; }

    public Integer getDoublePointsDays() { return doublePointsDays; }
    public void setDoublePointsDays(Integer doublePointsDays) { this.doublePointsDays = doublePointsDays; }

    public String getBonusDays() { return bonusDays; }
    public void setBonusDays(String bonusDays) { this.bonusDays = bonusDays; }

    public Integer getDailyLimit() { return dailyLimit; }
    public void setDailyLimit(Integer dailyLimit) { this.dailyLimit = dailyLimit; }

    public Integer getTotalLimit() { return totalLimit; }
    public void setTotalLimit(Integer totalLimit) { this.totalLimit = totalLimit; }

    public Integer getCheckinCount() { return checkinCount; }
    public void setCheckinCount(Integer checkinCount) { this.checkinCount = checkinCount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCouponReward() { return couponReward; }
    public void setCouponReward(String couponReward) { this.couponReward = couponReward; }

    public Integer getMinDistanceMeters() { return minDistanceMeters; }
    public void setMinDistanceMeters(Integer minDistanceMeters) { this.minDistanceMeters = minDistanceMeters; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
