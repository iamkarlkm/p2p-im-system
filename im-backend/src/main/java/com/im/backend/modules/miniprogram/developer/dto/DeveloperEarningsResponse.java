package com.im.backend.modules.miniprogram.developer.dto;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 开发者收益统计响应
 */
@Data
public class DeveloperEarningsResponse {
    
    private Long developerId;
    private BigDecimal balance;
    private BigDecimal totalEarnings;
    private BigDecimal monthlyEarnings;
    private BigDecimal weeklyEarnings;
}
