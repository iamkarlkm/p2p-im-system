package com.im.backend.modules.delivery.dto;

import lombok.Data;

/**
 * 订单查询DTO
 */
@Data
public class OrderQueryDTO {

    private Long riderId;
    private Long userId;
    private Long merchantId;
    private String status;
    private String bizType;
    private String startTime;
    private String endTime;
    private String keyword;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
