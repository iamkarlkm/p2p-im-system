package com.im.backend.modules.appointment.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商户预约查询请求
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MerchantAppointmentQueryRequest extends AppointmentQueryRequest {

    /**
     * 服务人员ID
     */
    private Long staffId;

    /**
     * 服务ID
     */
    private Long serviceId;

    /**
     * 来源
     */
    private Integer source;

    /**
     * 预约单号
     */
    private String appointmentNo;
}
