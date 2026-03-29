package com.im.backend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DoNotDisturbPeriodDTO {

    private String id;
    private String userId;
    private String name;
    private Integer startHour;
    private Integer startMinute;
    private Integer endHour;
    private Integer endMinute;
    private List<Integer> activeDays;
    private Boolean isEnabled;
    private Boolean allowCalls;
    private Boolean allowMentions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isCurrentlyActive;

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

    public Boolean getIsCurrentlyActive() {
        return isCurrentlyActive;
    }

    public void setIsCurrentlyActive(Boolean isCurrentlyActive) {
        this.isCurrentlyActive = isCurrentlyActive;
    }
}
