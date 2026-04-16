package com.im.service.admin.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 系统统计响应DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatisticsResponse {

    /**
     * 总日志数
     */
    private long totalLogs;

    /**
     * 今日日志数
     */
    private long todayLogs;

    /**
     * 失败操作数
     */
    private long failedOperations;

    /**
     * 按模块统计
     */
    private java.util.Map<String, Long> logsByModule;

    /**
     * 按操作统计
     */
    private java.util.Map<String, Long> logsByOperation;

    /**
     * 按结果统计
     */
    private java.util.Map<String, Long> logsByResult;

    /**
     * 平均操作耗时
     */
    private double averageDuration;

    /**
     * 最旧日志时间
     */
    private java.time.LocalDateTime oldestLogTime;

    /**
     * 最新日志时间
     */
    private java.time.LocalDateTime newestLogTime;

    /**
     * 活跃管理员列表
     */
    private java.util.List<ActiveAdminResponse> activeAdmins;

    /**
     * 活跃管理员信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActiveAdminResponse {
        private Long adminId;
        private String username;
        private long operationCount;
        private java.time.LocalDateTime lastOperationTime;
    }
}
