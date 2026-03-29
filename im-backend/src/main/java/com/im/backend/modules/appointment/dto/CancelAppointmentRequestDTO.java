package com.im.backend.modules.appointment.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 取消预约请求DTO
 */
public class CancelAppointmentRequestDTO {

    @NotNull(message = "预约ID不能为空")
    private Long appointmentId;

    @Size(max = 200, message = "取消原因长度不能超过200")
    private String cancelReason;

    // Getters and Setters
    public Long getAppointmentId() { return appointmentId; }
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }

    public String getCancelReason() { return cancelReason; }
    public void setCancelReason(String cancelReason) { this.cancelReason = cancelReason; }
}
