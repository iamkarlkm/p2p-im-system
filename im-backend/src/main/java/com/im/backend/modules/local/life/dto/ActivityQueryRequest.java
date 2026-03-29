package com.im.backend.modules.local.life.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动查询请求DTO
 */
@Data
public class ActivityQueryRequest {

    private String keyword;
    private String category;
    private String status;
    private Long publisherId;

    private BigDecimal longitude;
    private BigDecimal latitude;
    private Integer radius; // 单位: 米
    private String cityCode;

    private LocalDateTime startTimeFrom;
    private LocalDateTime startTimeTo;

    private String paymentType;
    private Boolean onlyFree;

    private Boolean nearby;
    private Boolean hot;
    private Boolean recommended;

    private List<String> tags;

    private Integer pageNum = 1;
    private Integer pageSize = 20;

    private String sortBy = "heat"; // heat-热度, time-时间, distance-距离
    private String sortOrder = "desc"; // asc, desc
}
