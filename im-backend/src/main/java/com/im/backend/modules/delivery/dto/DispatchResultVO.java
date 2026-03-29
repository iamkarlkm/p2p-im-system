package com.im.backend.modules.delivery.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 派单结果VO
 */
@Data
public class DispatchResultVO {

    private Long orderId;
    private String orderNo;
    private Long riderId;
    private String riderName;
    private String riderPhone;
    private String dispatchType;
    private String status;
    private String reason;
    private Integer distanceToMerchant;
    private Integer etaToMerchant;
    private BigDecimal riderLat;
    private BigDecimal riderLng;
    private LocalDateTime dispatchTime;
    private String message;
}
