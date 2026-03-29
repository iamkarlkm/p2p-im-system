package com.im.backend.modules.appointment.dto;

import java.util.List;

/**
 * 查询可预约时段请求DTO
 */
public class QueryAvailableSlotsRequestDTO {

    private Long merchantId;
    private Long serviceId;
    private Integer days = 7;

    // Getters and Setters
    public Long getMerchantId() { return merchantId; }
    public void setMerchantId(Long merchantId) { this.merchantId = merchantId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public Integer getDays() { return days; }
    public void setDays(Integer days) { this.days = days; }
}
