package com.im.backend.modules.delivery.dto;

import lombok.Data;

/**
 * 骑手查询DTO
 */
@Data
public class RiderQueryDTO {

    private Long stationId;
    private String status;
    private String keyword;
    private Boolean enabled;
    private Integer pageNum = 1;
    private Integer pageSize = 20;
}
