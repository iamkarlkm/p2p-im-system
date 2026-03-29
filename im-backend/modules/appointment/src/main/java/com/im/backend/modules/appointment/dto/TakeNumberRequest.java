package com.im.backend.modules.appointment.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 取号请求
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
public class TakeNumberRequest {

    /**
     * 队列ID
     */
    @NotNull(message = "队列ID不能为空")
    private Long queueId;

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
     * 用户ID
     */
    private Long userId;

    /**
     * 联系人姓名
     */
    private String contactName;

    /**
     * 联系人电话
     */
    private String contactPhone;

    /**
     * 备注
     */
    private String remark;

    /**
     * 预约ID(如果是预约取号)
     */
    private Long appointmentId;
}
