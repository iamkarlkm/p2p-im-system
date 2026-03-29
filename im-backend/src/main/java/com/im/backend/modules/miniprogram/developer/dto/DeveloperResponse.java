package com.im.backend.modules.miniprogram.developer.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 开发者信息响应
 */
@Data
public class DeveloperResponse {
    
    private Long id;
    private Long userId;
    private String developerType;
    private String developerName;
    private String contactPhone;
    private String contactEmail;
    private Integer verifyStatus;
    private String verifyStatusDesc;
    private Integer level;
    private String levelDesc;
    private Integer points;
    private BigDecimal balance;
    private BigDecimal totalEarnings;
    private Integer componentCount;
    private Integer programCount;
    private LocalDateTime createTime;
}
