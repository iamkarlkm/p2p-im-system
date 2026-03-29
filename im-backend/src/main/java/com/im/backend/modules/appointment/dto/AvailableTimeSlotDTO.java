package com.im.backend.modules.appointment.dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.math.BigDecimal;

/**
 * 可预约时段DTO
 */
public class AvailableTimeSlotDTO {

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer duration;
    private Integer maxCapacity;
    private Integer bookedCount;
    private Integer remainingCapacity;
    private Boolean isAvailable;
    private Boolean isFull;
    private BigDecimal price;
    private BigDecimal deposit;
    private Long scheduleId;

    // Getters and Setters
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getDuration() { return duration; }
    public void setDuration(Integer duration) { this.duration = duration; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Integer getBookedCount() { return bookedCount; }
    public void setBookedCount(Integer bookedCount) { this.bookedCount = bookedCount; }

    public Integer getRemainingCapacity() { return remainingCapacity; }
    public void setRemainingCapacity(Integer remainingCapacity) { this.remainingCapacity = remainingCapacity; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public Boolean getIsFull() { return isFull; }
    public void setIsFull(Boolean isFull) { this.isFull = isFull; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getDeposit() { return deposit; }
    public void setDeposit(BigDecimal deposit) { this.deposit = deposit; }

    public Long getScheduleId() { return scheduleId; }
    public void setScheduleId(Long scheduleId) { this.scheduleId = scheduleId; }
}
