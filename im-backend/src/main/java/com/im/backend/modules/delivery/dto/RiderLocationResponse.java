package com.im.backend.modules.delivery.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 骑手位置响应
 */
@Data
public class RiderLocationResponse {
    private Long riderId;
    private String riderName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private BigDecimal speed;
    private String location;
}
