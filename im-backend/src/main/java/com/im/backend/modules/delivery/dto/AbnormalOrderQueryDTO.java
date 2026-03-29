package com.im.backend.modules.delivery.dto;

import lombok.Data;

/**
 * 异常订单查询DTO
 */
@Data
public class AbnormalOrderQueryDTO {

    private String abnormalType; // TIMEOUT-超时, REJECTED-被拒, REASSIGNED-重新分配
    private Long riderId;
    private Long stationId;
    private String startTime;
    private String endTime;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
