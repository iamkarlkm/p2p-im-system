package com.im.backend.modules.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手信息VO
 */
@Data
public class RiderVO {

    private Long id;
    private Long userId;
    private String riderNo;
    private String realName;
    private String phone;
    private String status;
    private BigDecimal currentLat;
    private BigDecimal currentLng;
    private LocalDateTime locationUpdatedAt;
    private Integer currentOrderCount;
    private Integer maxOrderCount;
    private String geoHash;
    private Long stationId;
    private Integer todayOrderCount;
    private Integer todayDistance;
    private BigDecimal rating;
    private Integer totalOrders;
    private Boolean enabled;
    private LocalDateTime lastOnlineAt;
    private LocalDateTime createdAt;

    /** 距离（用于附近搜索） */
    private Double distance;
}
