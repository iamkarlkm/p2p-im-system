package com.im.backend.modules.appointment.dto;

import java.time.LocalDateTime;

/**
 * 排队票号详情DTO
 */
public class QueueTicketDetailDTO {

    private Long id;
    private String queueCode;
    private Long userId;
    private Long merchantId;
    private String merchantName;
    private String merchantLogo;
    private String queueType;
    private String queueTypeName;
    private Integer queueNumber;
    private Integer currentNumber;
    private Integer aheadCount;
    private Integer peopleCount;
    private Integer estimatedWaitTime;
    private String status;
    private String statusText;
    private String tableType;
    private Integer priority;
    private LocalDateTime takeTime;
    private LocalDateTime callTime;
    private LocalDateTime arriveTime;
    private LocalDateTime completeTime;
    private LocalDateTime expireTime;
    private String tableName;
    private String staffName;
    private String contactName;
    private String contactPhone;
    private String remark;
    private Boolean notified;
    private Double distanceWhenTake;
    private String source;
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQueueCode() { return queueCode; }
    public void setQueueCode(String queueCode) { this.queueCode = queueCode; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public String getMerchantLogo() { return merchantLogo; }
    public void setMerchantLogo(String merchantLogo) { this.merchantLogo = merchantLogo; }

    public String getQueueType() { return queueType; }
    public void setQueueType(String queueType) { this.queueType = queueType; }

    public String getQueueTypeName() { return queueTypeName; }
    public void setQueueTypeName(String queueTypeName) { this.queueTypeName = queueTypeName; }

    public Integer getQueueNumber() { return queueNumber; }
    public void setQueueNumber(Integer queueNumber) { this.queueNumber = queueNumber; }

    public Integer getCurrentNumber() { return currentNumber; }
    public void setCurrentNumber(Integer currentNumber) { this.currentNumber = currentNumber; }

    public Integer getAheadCount() { return aheadCount; }
    public void setAheadCount(Integer aheadCount) { this.aheadCount = aheadCount; }

    public Integer getPeopleCount() { return peopleCount; }
    public void setPeopleCount(Integer peopleCount) { this.peopleCount = peopleCount; }

    public Integer getEstimatedWaitTime() { return estimatedWaitTime; }
    public void setEstimatedWaitTime(Integer estimatedWaitTime) { this.estimatedWaitTime = estimatedWaitTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStatusText() { return statusText; }
    public void setStatusText(String statusText) { this.statusText = statusText; }

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

    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

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

    public Double getDistanceWhenTake() { return distanceWhenTake; }
    public void setDistanceWhenTake(Double distanceWhenTake) { this.distanceWhenTake = distanceWhenTake; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
