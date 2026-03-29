package com.im.backend.modules.appointment.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * 创建预约请求
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class CreateAppointmentRequest {

    /**
     * 商户ID
     */
    @NotNull(message = "商户ID不能为空")
    private Long merchantId;

    /**
     * 门店ID
     */
    @NotNull(message = "门店ID不能为空")
    private Long storeId;

    /**
     * 服务ID
     */
    @NotNull(message = "服务ID不能为空")
    private Long serviceId;

    /**
     * 服务人员ID(可选)
     */
    private Long staffId;

    /**
     * 预约日期
     */
    @NotNull(message = "预约日期不能为空")
    private LocalDate appointmentDate;

    /**
     * 预约开始时间
     */
    @NotNull(message = "预约时间不能为空")
    private LocalTime startTime;

    /**
     * 预约结束时间
     */
    @NotNull(message = "预约结束时间不能为空")
    private LocalTime endTime;

    /**
     * 预约人数
     */
    private Integer peopleCount = 1;

    /**
     * 联系人姓名
     */
    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    /**
     * 联系人电话
     */
    @NotBlank(message = "联系人电话不能为空")
    private String contactPhone;

    /**
     * 联系人备注
     */
    private String contactRemark;

    /**
     * 预约备注
     */
    private String remark;

    /**
     * 预约来源
     */
    private Integer source = 1;

    /**
     * 服务项目列表(可选，默认主服务)
     */
    private List<AppointmentItemRequest> items;
}
