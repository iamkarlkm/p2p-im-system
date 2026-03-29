package com.im.local.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 派单结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DispatchResult {
    
    private boolean success;
    private String message;
    private Long riderId;
    private Long dispatchId;
    
    public static DispatchResult success(Long riderId, Long dispatchId) {
        return DispatchResult.builder()
            .success(true)
            .message("派单成功")
            .riderId(riderId)
            .dispatchId(dispatchId)
            .build();
    }
    
    public static DispatchResult failed(String message) {
        return DispatchResult.builder()
            .success(false)
            .message(message)
            .build();
    }
}
