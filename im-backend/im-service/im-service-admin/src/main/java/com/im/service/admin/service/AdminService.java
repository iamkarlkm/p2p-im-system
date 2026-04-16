package com.im.service.admin.service;

import com.im.service.admin.entity.AdminLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 管理服务接口
 */
public interface AdminService {

    /**
     * 记录操作日志
     * @param log 日志实体
     * @return 保存后的日志
     */
    AdminLog logOperation(AdminLog log);

    /**
     * 记录操作日志（便捷方法）
     */
    AdminLog logOperation(Long adminId, String adminUsername, String operationType, 
                         String module, String description);

    /**
     * 获取日志详情
     * @param logId 日志ID
     * @return 日志详情
     */
    Optional<AdminLog> getLogById(Long logId);

    /**
     * 分页查询日志
     * @param adminId 管理员ID
     * @param module 模块
     * @param operationType 操作类型
     * @param result 结果
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 日志分页
     */
    Page<AdminLog> getLogs(Long adminId, String module, String operationType, 
                          String result, LocalDateTime startTime, LocalDateTime endTime,
                          Pageable pageable);

    /**
     * 获取管理员操作日志
     * @param adminId 管理员ID
     * @param pageable 分页参数
     * @return 日志分页
     */
    Page<AdminLog> getAdminLogs(Long adminId, Pageable pageable);

    /**
     * 获取最近的日志
     * @param limit 数量限制
     * @return 日志列表
     */
    List<AdminLog> getRecentLogs(int limit);

    /**
     * 获取最近登录记录
     * @param limit 数量限制
     * @return 登录日志列表
     */
    List<AdminLog> getRecentLogins(int limit);

    /**
     * 获取失败的操作
     * @param pageable 分页参数
     * @return 失败日志分页
     */
    List<AdminLog> getFailedOperations(Pageable pageable);

    /**
     * 统计操作（按模块）
     * @param since 起始时间
     * @return 统计数据
     */
    Map<String, Long> getOperationCountByModule(LocalDateTime since);

    /**
     * 统计操作（按类型）
     * @param since 起始时间
     * @return 统计数据
     */
    Map<String, Long> getOperationCountByType(LocalDateTime since);

    /**
     * 统计操作结果
     * @param since 起始时间
     * @return 统计数据
     */
    Map<String, Long> getOperationResultCount(LocalDateTime since);

    /**
     * 获取平均操作耗时（毫秒）
     * @param since 起始时间
     * @return 平均耗时
     */
    Double getAverageDuration(LocalDateTime since);

    /**
     * 获取管理员的最后一次登录
     * @param adminId 管理员ID
     * @return 最后一次登录日志
     */
    Optional<AdminLog> getLastLogin(Long adminId);

    /**
     * 获取管理员的操作统计
     * @param adminId 管理员ID
     * @return 统计数据
     */
    AdminStatistics getAdminStatistics(Long adminId);

    /**
     * 删除旧日志
     * @param beforeTime 删除该时间之前的日志
     * @return 删除数量
     */
    int deleteOldLogs(LocalDateTime beforeTime);

    /**
     * 获取系统统计信息
     * @return 系统统计
     */
    SystemStatistics getSystemStatistics();

    /**
     * 管理员操作统计
     */
    class AdminStatistics {
        private long totalOperations;
        private long successCount;
        private long failureCount;
        private Map<String, Long> byModule;
        private Map<String, Long> byOperationType;
        private Double averageDuration;
        private LocalDateTime lastOperationTime;

        // Getters and Setters
        public long getTotalOperations() { return totalOperations; }
        public void setTotalOperations(long totalOperations) { this.totalOperations = totalOperations; }
        public long getSuccessCount() { return successCount; }
        public void setSuccessCount(long successCount) { this.successCount = successCount; }
        public long getFailureCount() { return failureCount; }
        public void setFailureCount(long failureCount) { this.failureCount = failureCount; }
        public Map<String, Long> getByModule() { return byModule; }
        public void setByModule(Map<String, Long> byModule) { this.byModule = byModule; }
        public Map<String, Long> getByOperationType() { return byOperationType; }
        public void setByOperationType(Map<String, Long> byOperationType) { this.byOperationType = byOperationType; }
        public Double getAverageDuration() { return averageDuration; }
        public void setAverageDuration(Double averageDuration) { this.averageDuration = averageDuration; }
        public LocalDateTime getLastOperationTime() { return lastOperationTime; }
        public void setLastOperationTime(LocalDateTime lastOperationTime) { this.lastOperationTime = lastOperationTime; }
    }

    /**
     * 系统统计信息
     */
    class SystemStatistics {
        private long totalLogs;
        private long todayLogs;
        private long failedOperations;
        private Map<String, Long> logsByModule;
        private Map<String, Long> logsByOperation;
        private Map<String, Long> logsByResult;
        private double averageDuration;
        private LocalDateTime oldestLogTime;
        private LocalDateTime newestLogTime;
        private List<ActiveAdmin> activeAdmins;

        // Getters and Setters
        public long getTotalLogs() { return totalLogs; }
        public void setTotalLogs(long totalLogs) { this.totalLogs = totalLogs; }
        public long getTodayLogs() { return todayLogs; }
        public void setTodayLogs(long todayLogs) { this.todayLogs = todayLogs; }
        public long getFailedOperations() { return failedOperations; }
        public void setFailedOperations(long failedOperations) { this.failedOperations = failedOperations; }
        public Map<String, Long> getLogsByModule() { return logsByModule; }
        public void setLogsByModule(Map<String, Long> logsByModule) { this.logsByModule = logsByModule; }
        public Map<String, Long> getLogsByOperation() { return logsByOperation; }
        public void setLogsByOperation(Map<String, Long> logsByOperation) { this.logsByOperation = logsByOperation; }
        public Map<String, Long> getLogsByResult() { return logsByResult; }
        public void setLogsByResult(Map<String, Long> logsByResult) { this.logsByResult = logsByResult; }
        public double getAverageDuration() { return averageDuration; }
        public void setAverageDuration(double averageDuration) { this.averageDuration = averageDuration; }
        public LocalDateTime getOldestLogTime() { return oldestLogTime; }
        public void setOldestLogTime(LocalDateTime oldestLogTime) { this.oldestLogTime = oldestLogTime; }
        public LocalDateTime getNewestLogTime() { return newestLogTime; }
        public void setNewestLogTime(LocalDateTime newestLogTime) { this.newestLogTime = newestLogTime; }
        public List<ActiveAdmin> getActiveAdmins() { return activeAdmins; }
        public void setActiveAdmins(List<ActiveAdmin> activeAdmins) { this.activeAdmins = activeAdmins; }
    }

    /**
     * 活跃管理员信息
     */
    class ActiveAdmin {
        private Long adminId;
        private String username;
        private long operationCount;
        private LocalDateTime lastOperationTime;

        // Getters and Setters
        public Long getAdminId() { return adminId; }
        public void setAdminId(Long adminId) { this.adminId = adminId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public long getOperationCount() { return operationCount; }
        public void setOperationCount(long operationCount) { this.operationCount = operationCount; }
        public LocalDateTime getLastOperationTime() { return lastOperationTime; }
        public void setLastOperationTime(LocalDateTime lastOperationTime) { this.lastOperationTime = lastOperationTime; }
    }
}
