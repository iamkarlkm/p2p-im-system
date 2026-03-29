package com.im.backend.modules.delivery.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手信息响应
 */
@Data
public class RiderResponse {
    private Long id;
    private Long userId;
    private String riderName;
    private String phone;
    private String avatar;
    
    private Integer status;
    private Integer workStatus;
    private BigDecimal rating;
    private Integer totalOrders;
    private Integer successOrders;
    
    private BigDecimal currentLat;
    private BigDecimal currentLng;
    private String currentLocation;
    private LocalDateTime locationUpdateTime;
    
    private Long currentOrderId;
    private Integer todayOrderCount;
    private BigDecimal todayIncome;
}
