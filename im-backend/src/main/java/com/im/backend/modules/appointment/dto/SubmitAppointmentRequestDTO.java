package com.im.backend.modules.appointment.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 提交预约请求DTO
 */
public class SubmitAppointmentRequestDTO {

    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    @NotNull(message = "服务ID不能为空")
    private Long serviceId;

    @NotNull(message = "预约日期不能为空")
    @FutureOrPresent(message = "预约日期不能是过去日期")
    private LocalDate appointmentDate;

    @NotNull(message = "开始时间不能为空")
    private LocalTime startTime;

    private LocalTime endTime;

    @NotBlank(message = "联系人姓名不能为空")
    @Size(max = 50, message = "联系人姓名长度不能超过50")
    private String contactName;

    @NotBlank(message = "联系人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号")
    private String contactPhone;

    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;

    @Min(value = 1, message = "人数至少为1")
    @Max(value = 100, message = "人数不能超过100")
    private Integer numberOfPeople = 1;

    private Long staffId;

    private Long seatId;

    private String source;

    // Getters and Setters
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public LocalDate getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(LocalDate appointmentDate) { this.appointmentDate = appointmentDate; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public String getContactName() { return contactName; }
    public void setContactName(String contactName) { this.contactName = contactName; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public Integer getNumberOfPeople() { return numberOfPeople; }
    public void setNumberOfPeople(Integer numberOfPeople) { this.numberOfPeople = numberOfPeople; }

    public Long getStaffId() { return staffId; }
    public void setStaffId(Long staffId) { this.staffId = staffId; }

    public Long getSeatId() { return seatId; }
    public void setSeatId(Long seatId) { this.seatId = seatId; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
