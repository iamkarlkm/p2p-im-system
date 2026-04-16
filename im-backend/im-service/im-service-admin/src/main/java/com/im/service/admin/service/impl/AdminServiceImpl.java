package com.im.service.admin.service.impl;

import com.im.service.admin.entity.AdminLog;
import com.im.service.admin.repository.AdminLogRepository;
import com.im.service.admin.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminLogRepository adminLogRepository;

    @Override
    @Transactional
    public AdminLog logOperation(AdminLog adminLog) {
        log.info("Logging admin operation: {} - {} - {}", 
                adminLog.getAdminId(), adminLog.getOperationType(), adminLog.getModule());
        return adminLogRepository.save(adminLog);
    }

    @Override
    @Transactional
    public AdminLog logOperation(Long adminId, String adminUsername, String operationType, 
                                 String module, String description) {
        AdminLog log = AdminLog.builder()
                .adminId(adminId)
                .adminUsername(adminUsername)
                .operationType(operationType)
                .module(module)
                .description(description)
                .result(AdminLog.Result.SUCCESS.name())
                .build();
        return adminLogRepository.save(log);
    }

    @Override
    public Optional<AdminLog> getLogById(Long logId) {
        return adminLogRepository.findById(logId);
    }

    @Override
    public Page<AdminLog> getLogs(Long adminId, String module, String operationType, 
                                  String result, LocalDateTime startTime, LocalDateTime endTime,
                                  Pageable pageable) {
        return adminLogRepository.findByConditions(
                adminId, module, operationType, result, startTime, endTime, pageable);
    }

    @Override
    public Page<AdminLog> getAdminLogs(Long adminId, Pageable pageable) {
        return adminLogRepository.findByAdminId(adminId, pageable);
    }

    @Override
    public List<AdminLog> getRecentLogs(int limit) {
        return adminLogRepository.findRecentOperations(PageRequest.of(0, limit));
    }

    @Override
    public List<AdminLog> getRecentLogins(int limit) {
        return adminLogRepository.findRecentLogins(PageRequest.of(0, limit));
    }

    @Override
    public List<AdminLog> getFailedOperations(Pageable pageable) {
        return adminLogRepository.findFailedOperations(pageable);
    }

    @Override
    public Map<String, Long> getOperationCountByModule(LocalDateTime since) {
        List<Object[]> results = adminLogRepository.countByModule(since);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Override
    public Map<String, Long> getOperationCountByType(LocalDateTime since) {
        List<Object[]> results = adminLogRepository.countByOperationType(since);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Override
    public Map<String, Long> getOperationResultCount(LocalDateTime since) {
        List<Object[]> results = adminLogRepository.countByResult(since);
        return results.stream()
                .collect(Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Override
    public Double getAverageDuration(LocalDateTime since) {
        return adminLogRepository.getAverageDuration(since);
    }

    @Override
    public Optional<AdminLog> getLastLogin(Long adminId) {
        return adminLogRepository.findLastLogin(adminId);
    }

    @Override
    public AdminStatistics getAdminStatistics(Long adminId) {
        AdminStatistics stats = new AdminStatistics();
        
        // 总操作数
        stats.setTotalOperations(adminLogRepository.countByAdminId(adminId));
        
        // 按结果统计
        Map<String, Long> resultCount = getOperationResultCount(
                LocalDateTime.now().minusDays(30));
        stats.setSuccessCount(resultCount.getOrDefault("SUCCESS", 0L));
        stats.setFailureCount(resultCount.getOrDefault("FAILURE", 0L));
        
        // 按模块统计
        stats.setByModule(getOperationCountByModule(LocalDateTime.now().minusDays(30)));
        
        // 按操作类型统计
        stats.setByOperationType(getOperationCountByType(LocalDateTime.now().minusDays(30)));
        
        // 平均耗时
        stats.setAverageDuration(getAverageDuration(LocalDateTime.now().minusDays(30)));
        
        // 最后操作时间
        Page<AdminLog> recentLogs = adminLogRepository.findByAdminId(
                adminId, PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")));
        if (!recentLogs.isEmpty()) {
            stats.setLastOperationTime(recentLogs.getContent().get(0).getCreatedAt());
        }
        
        return stats;
    }

    @Override
    @Transactional
    public int deleteOldLogs(LocalDateTime beforeTime) {
        int count = adminLogRepository.deleteOldLogs(beforeTime);
        log.info("Deleted {} old admin logs before {}", count, beforeTime);
        return count;
    }

    @Override
    public SystemStatistics getSystemStatistics() {
        SystemStatistics stats = new SystemStatistics();
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        
        // 总日志数
        stats.setTotalLogs(adminLogRepository.count());
        
        // 今日日志数
        List<AdminLog> todayLogs = adminLogRepository.findByTimeRange(todayStart, now);
        stats.setTodayLogs(todayLogs.size());
        
        // 失败操作数
        stats.setFailedOperations(adminLogRepository.countByResultEquals("FAILURE"));
        
        // 按模块统计
        stats.setLogsByModule(getOperationCountByModule(now.minusDays(30)));
        
        // 按操作统计
        stats.setLogsByOperation(getOperationCountByType(now.minusDays(30)));
        
        // 按结果统计
        stats.setLogsByResult(getOperationResultCount(now.minusDays(30)));
        
        // 平均耗时
        Double avgDuration = adminLogRepository.getAverageDuration(now.minusDays(30));
        stats.setAverageDuration(avgDuration != null ? avgDuration : 0.0);
        
        // 最旧和最新日志时间
        if (adminLogRepository.count() > 0) {
            Page<AdminLog> oldestPage = adminLogRepository.findAll(
                    PageRequest.of(0, 1, Sort.by(Sort.Direction.ASC, "createdAt")));
            Page<AdminLog> newestPage = adminLogRepository.findAll(
                    PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdAt")));
            
            if (!oldestPage.isEmpty()) {
                stats.setOldestLogTime(oldestPage.getContent().get(0).getCreatedAt());
            }
            if (!newestPage.isEmpty()) {
                stats.setNewestLogTime(newestPage.getContent().get(0).getCreatedAt());
            }
        }
        
        // 活跃管理员
        stats.setActiveAdmins(getActiveAdmins(10));
        
        return stats;
    }

    private List<ActiveAdmin> getActiveAdmins(int limit) {
        // 获取最近有操作的管理员
        List<AdminLog> recentLogs = adminLogRepository.findRecentOperations(PageRequest.of(0, 100));
        
        Map<Long, AdminLog> adminLatestLog = new HashMap<>();
        Map<Long, Long> adminOperationCount = new HashMap<>();
        
        for (AdminLog log : recentLogs) {
            Long adminId = log.getAdminId();
            adminLatestLog.putIfAbsent(adminId, log);
            adminOperationCount.merge(adminId, 1L, Long::sum);
        }
        
        return adminLatestLog.entrySet().stream()
                .map(entry -> {
                    ActiveAdmin admin = new ActiveAdmin();
                    admin.setAdminId(entry.getKey());
                    admin.setUsername(entry.getValue().getAdminUsername());
                    admin.setOperationCount(adminOperationCount.get(entry.getKey()));
                    admin.setLastOperationTime(entry.getValue().getCreatedAt());
                    return admin;
                })
                .sorted(Comparator.comparing(ActiveAdmin::getLastOperationTime).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
}
