package com.im.backend.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "do_not_disturb_periods")
public class DoNotDisturbPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "start_hour", nullable = false)
    private Integer startHour;

    @Column(name = "start_minute", nullable = false)
    private Integer startMinute;

    @Column(name = "end_hour", nullable = false)
    private Integer endHour;

    @Column(name = "end_minute", nullable = false)
    private Integer endMinute;

    @ElementCollection
    @CollectionTable(
        name = "do_not_disturb_active_days",
        joinColumns = @JoinColumn(name = "period_id")
    )
    @Column(name = "day_of_week")
    private List<Integer> activeDays;

    @Column(name = "is_enabled", nullable = false)
    private Boolean isEnabled = true;

    @Column(name = "allow_calls", nullable = false)
    private Boolean allowCalls = false;

    @Column(name = "allow_mentions", nullable = false)
    private Boolean allowMentions = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStartHour() {
        return startHour;
    }

    public void setStartHour(Integer startHour) {
        this.startHour = startHour;
    }

    public Integer getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(Integer startMinute) {
        this.startMinute = startMinute;
    }

    public Integer getEndHour() {
        return endHour;
    }

    public void setEndHour(Integer endHour) {
        this.endHour = endHour;
    }

    public Integer getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(Integer endMinute) {
        this.endMinute = endMinute;
    }

    public List<Integer> getActiveDays() {
        return activeDays;
    }

    public void setActiveDays(List<Integer> activeDays) {
        this.activeDays = activeDays;
    }

    public Boolean getIsEnabled() {
        return isEnabled;
    }

    public void setIsEnabled(Boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public Boolean getAllowCalls() {
        return allowCalls;
    }

    public void setAllowCalls(Boolean allowCalls) {
        this.allowCalls = allowCalls;
    }

    public Boolean getAllowMentions() {
        return allowMentions;
    }

    public void setAllowMentions(Boolean allowMentions) {
        this.allowMentions = allowMentions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getFormattedTimeRange() {
        return String.format("%02d:%02d - %02d:%02d",
            startHour, startMinute, endHour, endMinute);
    }

    public boolean isCurrentlyActive() {
        if (!isEnabled) return false;
        
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        int currentDay = now.getDayOfWeek().getValue();
        
        if (activeDays == null || !activeDays.contains(currentDay)) {
            return false;
        }
        
        int currentMinutes = now.getHour() * 60 + now.getMinute();
        int startMinutes = startHour * 60 + startMinute;
        int endMinutes = endHour * 60 + endMinute;
        
        if (startMinutes <= endMinutes) {
            return currentMinutes >= startMinutes && currentMinutes <= endMinutes;
        } else {
            return currentMinutes >= startMinutes || currentMinutes <= endMinutes;
        }
    }
}
