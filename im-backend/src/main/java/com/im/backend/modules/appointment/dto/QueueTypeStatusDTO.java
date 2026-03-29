package com.im.backend.modules.appointment.dto;

/**
 * 队列类型状态DTO
 */
public class QueueTypeStatusDTO {

    private String queueType;
    private String queueTypeName;
    private Integer waitingCount;
    private Integer estimatedWaitMinutes;

    // Getters and Setters
    public String getQueueType() { return queueType; }
    public void setQueueType(String queueType) { this.queueType = queueType; }

    public String getQueueTypeName() { return queueTypeName; }
    public void setQueueTypeName(String queueTypeName) { this.queueTypeName = queueTypeName; }

    public Integer getWaitingCount() { return waitingCount; }
    public void setWaitingCount(Integer waitingCount) { this.waitingCount = waitingCount; }

    public Integer getEstimatedWaitMinutes() { return estimatedWaitMinutes; }
    public void setEstimatedWaitMinutes(Integer estimatedWaitMinutes) { this.estimatedWaitMinutes = estimatedWaitMinutes; }
}
