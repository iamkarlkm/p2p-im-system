package com.im.local.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 接单结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AcceptResult {
    
    private boolean success;
    private Long orderId;
    private Long riderId;
    private LocalDateTime estimatedArrival;
    private String message;
}
