package com.im.backend.modules.delivery.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 配送完成DTO
 */
@Data
public class DeliveryCompleteDTO {

    @NotNull(message = "送达纬度不能为空")
    private Double lat;

    @NotNull(message = "送达经度不能为空")
    private Double lng;

    /** 签收码 */
    private String deliveryCode;

    /** 备注 */
    private String remark;

    /** 签收照片 */
    private String photoUrl;
}
