package com.im.backend.modules.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 位置点VO
 */
@Data
public class LocationPointVO {

    private BigDecimal lat;
    private BigDecimal lng;
    private BigDecimal accuracy;
    private BigDecimal speed;
    private LocalDateTime timestamp;
}
