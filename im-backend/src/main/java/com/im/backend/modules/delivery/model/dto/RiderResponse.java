package com.im.backend.modules.delivery.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 骑手响应
 */
@Data
public class RiderResponse {
    
    private Long id;
    
    private Long userId;
    
    private String riderNo;
    
    private String realName;
    
    private String phone;
    
    private Integer status;
    
    private Integer workStatus;
    
    private BigDecimal currentLat;
    
    private BigDecimal currentLng;
    
    private LocalDateTime locationUpdateTime;
    
    private Integer rating;
    
    private Integer totalOrders;
    
    private Integer completedOrders;
    
    private BigDecimal ratingScore;
    
    private String vehicleType;
    
    private String workCity;
    
    private Integer todayOrderCount;
    
    private BigDecimal todayIncome;
    
    private LocalDateTime createTime;
}
