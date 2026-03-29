package com.im.backend.modules.appointment.dto;

/**
 * 队列状态DTO
 */
public class QueueStatusDTO {

    private String queueType;
    private String queueTypeName;
    private Integer currentNumber;
    private Integer waitingCount;
    private Integer averageWaitTime;
    private Boolean isActive;

    // Getters and Setters
    public String getQueueType() { return queueType; }
    public void setQueueType(String queueType) { this.queueType = queueType; }

    public String getQueueTypeName() { return queueTypeName; }
    public void setQueueTypeName(String queueTypeName) { this.queueTypeName = queueTypeName; }

    public Integer getCurrentNumber() { return currentNumber; }
    public void setCurrentNumber(Integer currentNumber) { this.currentNumber = currentNumber; }

    public Integer getWaitingCount() { return waitingCount; }
    public void setWaitingCount(Integer waitingCount) { this.waitingCount = waitingCount; }

    public Integer getAverageWaitTime() { return averageWaitTime; }
    public void setAverageWaitTime(Integer averageWaitTime) { this.averageWaitTime = averageWaitTime; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
