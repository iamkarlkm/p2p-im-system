package com.im.backend.modules.appointment.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 预约列表响应
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class AppointmentListResponse {

    /**
     * 预约ID
     */
    private Long id;

    /**
     * 预约单号
     */
    private String appointmentNo;

    /**
     * 商户名称
     */
    private String merchantName;

    /**
     * 门店名称
     */
    private String storeName;

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务图片
     */
    private String serviceImage;

    /**
     * 服务人员姓名
     */
    private String staffName;

    /**
     * 预约日期
     */
    private LocalDate appointmentDate;

    /**
     * 开始时间
     */
    private LocalTime startTime;

    /**
     * 结束时间
     */
    private LocalTime endTime;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态名称
     */
    private String statusName;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 服务价格
     */
    private java.math.BigDecimal servicePrice;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
