package com.im.backend.modules.merchant.order.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 上报配送异常请求
 */
@Data
public class ReportDeliveryExceptionRequest {

    /**
     * 订单ID
     */
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    /**
     * 异常类型: 1-联系不上顾客, 2-地址错误, 3-顾客拒收, 4-车辆故障, 5-交通事故, 6-天气原因, 7-其他
     */
    @NotNull(message = "异常类型不能为空")
    private Integer exceptionType;

    /**
     * 异常描述
     */
    private String description;

    /**
     * 异常照片URL(多图逗号分隔)
     */
    private String exceptionPhotos;
}
