package com.im.backend.modules.appointment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 服务时段配置实体 - 商户服务时段管理
 * 支持按天/周配置可预约时段、容量管理
 */
@Entity
@Table(name = "service_schedule", indexes = {
    @Index(name = "idx_merchant_id", columnList = "merchant_id"),
    @Index(name = "idx_service_id", columnList = "service_id"),
    @Index(name = "idx_schedule_date", columnList = "schedule_date"),
    @Index(name = "idx_is_available", columnList = "is_available")
})
public class ServiceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "service_id", nullable = false)
    private Long serviceId;

    @Column(name = "service_name", length = 100)
    private String serviceName;

    @Column(name = "schedule_date", nullable = false)
    private LocalDate scheduleDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "time_slot_duration")
    private Integer timeSlotDuration = 30;

    @Column(name = "max_capacity", nullable = false)
    private Integer maxCapacity;

    @Column(name = "booked_count", nullable = false)
    private Integer bookedCount = 0;

    @Column(name = "is_available", nullable = false)
    private Boolean isAvailable = true;

    @Column(name = "price", precision = 10, scale = 2)
    private java.math.BigDecimal price;

    @Column(name = "deposit", precision = 10, scale = 2)
    private java.math.BigDecimal deposit;

    @Column(name = "staff_ids", length = 500)
    private String staffIds;

    @Column(name = "resource_ids", length = 500)
    private String resourceIds;

    @Column(name = "week_day")
    private Integer weekDay;

    @Column(name = "is_template")
    private Boolean isTemplate = false;

    @Column(name = "template_name", length = 50)
    private String templateName;

    @Column(name = "effective_start_date")
    private LocalDate effectiveStartDate;

    @Column(name = "effective_end_date")
    private LocalDate effectiveEndDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 获取剩余可预约数量
    public Integer getRemainingCapacity() {
        return Math.max(0, maxCapacity - bookedCount);
    }

    // 检查是否已满
    public Boolean isFull() {
        return bookedCount >= maxCapacity;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public LocalDate getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(LocalDate scheduleDate) { this.scheduleDate = scheduleDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getTimeSlotDuration() { return timeSlotDuration; }
    public void setTimeSlotDuration(Integer timeSlotDuration) { this.timeSlotDuration = timeSlotDuration; }

    public Integer getMaxCapacity() { return maxCapacity; }
    public void setMaxCapacity(Integer maxCapacity) { this.maxCapacity = maxCapacity; }

    public Integer getBookedCount() { return bookedCount; }
    public void setBookedCount(Integer bookedCount) { this.bookedCount = bookedCount; }

    public Boolean getIsAvailable() { return isAvailable; }
    public void setIsAvailable(Boolean isAvailable) { this.isAvailable = isAvailable; }

    public java.math.BigDecimal getPrice() { return price; }
    public void setPrice(java.math.BigDecimal price) { this.price = price; }

    public java.math.BigDecimal getDeposit() { return deposit; }
    public void setDeposit(java.math.BigDecimal deposit) { this.deposit = deposit; }

    public String getStaffIds() { return staffIds; }
    public void setStaffIds(String staffIds) { this.staffIds = staffIds; }

    public String getResourceIds() { return resourceIds; }
    public void setResourceIds(String resourceIds) { this.resourceIds = resourceIds; }

    public Integer getWeekDay() { return weekDay; }
    public void setWeekDay(Integer weekDay) { this.weekDay = weekDay; }

    public Boolean getIsTemplate() { return isTemplate; }
    public void setIsTemplate(Boolean isTemplate) { this.isTemplate = isTemplate; }

    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }

    public LocalDate getEffectiveStartDate() { return effectiveStartDate; }
    public void setEffectiveStartDate(LocalDate effectiveStartDate) { this.effectiveStartDate = effectiveStartDate; }

    public LocalDate getEffectiveEndDate() { return effectiveEndDate; }
    public void setEffectiveEndDate(LocalDate effectiveEndDate) { this.effectiveEndDate = effectiveEndDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
