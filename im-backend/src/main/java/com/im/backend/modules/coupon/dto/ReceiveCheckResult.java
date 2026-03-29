package com.im.backend.modules.coupon.dto;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 领取检查结果DTO
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Data
public class ReceiveCheckResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean canReceive;
    private String reason;
    private Integer remainingStock;
    private Integer receivedCount;
    private Integer limitCount;
    private Long countdownSeconds;
}
