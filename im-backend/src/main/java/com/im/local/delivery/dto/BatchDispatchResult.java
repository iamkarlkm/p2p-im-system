package com.im.local.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 批量派单结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchDispatchResult {
    
    private int totalOrders;
    private int successCount;
    private int failedCount;
    private List<DispatchResult> results;
}
