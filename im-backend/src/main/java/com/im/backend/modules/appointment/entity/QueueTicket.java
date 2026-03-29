package com.im.backend.modules.appointment.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 排队叫号实体 - 商户排队叫号系统
 * 支持远程取号、实时排队进度、多队列管理
 */
@Entity
@Table(name = "queue_ticket", indexes = {
    @Index(name = "idx_merchant_id", columnList = "merchant_id"),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_queue_code", columnList = "queue_code"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_queue_type", columnList = "queue_type")
})
public class QueueTicket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "queue_code", nullable = false, unique = true, length = 20)
    private String queueCode;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "merchant_id", nullable = false)
    private Long merchantId;

    @Column(name = "queue_type", nullable = false, length = 50)
    private String queueType;

    @Column(name = "queue_type_name", length = 100)
    private String queueTypeName;

    @Column(name = "queue_number", nullable = false)
    private Integer queueNumber;

    @Column(name = "current_number")
    private Integer currentNumber;

    @Column(name = "people_count", nullable = false)
    private Integer peopleCount;

    @Column(name = "estimated_wait_time")
    private Integer estimatedWaitTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private QueueStatus status = QueueStatus.WAITING;

    @Column(name = "table_type", length = 50)
    private String tableType;

    @Column(name = "priority")
    private Integer priority = 0;

    @Column(name = "take_time", nullable = false)
    private LocalDateTime takeTime;

    @Column(name = "call_time")
    private LocalDateTime callTime;

    @Column(name = "arrive_time")
    private LocalDateTime arriveTime;

    @Column(name = "complete_time")
    private LocalDateTime completeTime;

    @Column(name = "cancel_time")
    private LocalDateTime cancelTime;

    @Column(name = "cancel_reason", length = 200)
    private String cancelReason;

    @Column(name = "expire_time")
    private LocalDateTime expireTime;

    @Column(name = "table_id")
    private Long tableId;

    @Column(name = "table_name", length = 50)
    private String tableName;

    @Column(name = "staff_id")
    private Long staffId;

    @Column(name = "staff_name", length = 50)
    private String staffName;

    @Column(name = "contact_name", length = 50)
    private String contactName;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "remark", length = 200)
    private String remark;

    @Column(name = "notified")
    private Boolean notified = false;

    @Column(name = "notification_count")
    private Integer notificationCount = 0;

    @Column(name = "distance_when_take")
    private Double distanceWhenTake;

    @Column(name = "source", length = 20)
    private String source;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // 枚举：排队状态
    public enum QueueStatus {
        WAITING,        // 等待中
        CALLED,         // 已叫号
        ARRIVED,        // 已到达
        SERVING,        // 服务中
        COMPLETED,      // 已完成
        CANCELLED,      // 已取消
        EXPIRED,        // 已过期
        PASSED          // 已过号
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQueueCode() { return queueCode; }
    public void setQueueCode(String queueCode) { this.queueCode = queueCode; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public String getQueueType() { return queueType; }
    public void setQueueType(String queueType) { this.queueType = queueType; }

    public String getQueueTypeName() { return queueTypeName; }
    public void setQueueTypeName(String queueTypeName) { this.queueTypeName = queueTypeName; }

    public Integer getQueueNumber() { return queueNumber; }
    public void setQueueNumber(Integer queueNumber) { this.queueNumber = queueNumber; }

    public Integer getCurrentNumber() { return currentNumber; }
    public void setCurrentNumber(Integer currentNumber) { this.currentNumber = currentNumber; }

    public Integer getPeopleCount() { return peopleCount; }
    public void setPeopleCount(Integer peopleCount) { this.peopleCount = peopleCount; }

    public Integer getEstimatedWaitTime() { return estimatedWaitTime; }
    public void setEstimatedWaitTime(Integer estimatedWaitTime) { this.estimatedWaitTime = estimatedWaitTime; }

    public QueueStatus getStatus() { return status; }
    public void setStatus(QueueStatus status) { this.status = status; }

    public String getTableType() { return tableType; }
    public void setTableType(String tableType) { this.tableType = tableType; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public LocalDateTime getTakeTime() { return takeTime; }
    public void setTakeTime(LocalDateTime takeTime) { this.takeTime = takeTime; }

    public LocalDateTime getCallTime() { return callTime; }
    public void setCallTime(LocalDateTime callTime) { this.callTime = callTime; }

    public LocalDateTime getArriveTime() { return arriveTime; }
    public void setArriveTime(LocalDateTime arriveTime) { this.arriveTime = arriveTime; }

    public LocalDateTime getCompleteTime() { return completeTime; }
    public void setCompleteTime(LocalDateTime completeTime) { this.completeTime = completeTime; }

    public LocalDateTime getCancelTime() { return cancelTime; }
    public void setCancelTime(LocalDateTime cancelTime) { this.cancelTime = cancelTime; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }

    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }

    public Long getTableId() { return tableId; }
    public void setTableId(Long tableId) { this.tableId = tableId; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Boolean getNotified() { return notified; }
    public void setNotified(Boolean notified) { this.notified = notified; }

    public Integer getNotificationCount() { return notificationCount; }
    public void setNotificationCount(Integer notificationCount) { this.notificationCount = notificationCount; }

    public Double getDistanceWhenTake() { return distanceWhenTake; }
    public void setDistanceWhenTake(Double distanceWhenTake) { this.distanceWhenTake = distanceWhenTake; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // 计算前面等待人数
    public Integer getAheadCount() {
        if (currentNumber == null || queueNumber == null) {
            return null;
        }
        return Math.max(0, queueNumber - currentNumber);
    }
}
