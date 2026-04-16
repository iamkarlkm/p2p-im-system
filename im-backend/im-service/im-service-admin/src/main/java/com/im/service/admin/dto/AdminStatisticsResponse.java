package com.im.service.admin.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 管理员统计响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatisticsResponse {

    /**
     * 总操作数
     */
    private long totalOperations;

    /**
     * 成功次数
     */
    private long successCount;

    /**
     * 失败次数
     */
    private long failureCount;

    /**
     * 成功率
     */
    private double successRate;

    /**
     * 按模块统计
     */
    private java.util.Map<String, Long> byModule;

    /**
     * 按操作类型统计
     */
    private java.util.Map<String, Long> byOperationType;

    /**
     * 平均操作耗时（毫秒）
     */
    private Double averageDuration;

    /**
     * 最后操作时间
     */
    private java.time.LocalDateTime lastOperationTime;
}
