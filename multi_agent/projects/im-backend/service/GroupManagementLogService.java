package com.im.backend.service;

import com.im.backend.dto.GroupManagementLogDTO;
import com.im.backend.entity.GroupManagementLogEntity;
import com.im.backend.repository.GroupManagementLogRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 群管理日志服务
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GroupManagementLogService {

    private final GroupManagementLogRepository logRepository;
    private final ObjectMapper objectMapper;

    /**
     * 记录群管理操作日志
     */
    @Transactional
    public GroupManagementLogEntity logOperation(GroupManagementLogDTO logDTO) {
        try {
            GroupManagementLogEntity logEntity = GroupManagementLogEntity.builder()
                .groupId(logDTO.getGroupId())
                .operatorId(logDTO.getOperatorId())
                .operatorType(logDTO.getOperatorType())
                .targetUserId(logDTO.getTargetUserId())
                .actionType(logDTO.getActionType())
                .actionSubType(logDTO.getActionSubType())
                .description(logDTO.getDescription())
                .details(convertToJson(logDTO.getDetails()))
                .beforeState(convertToJson(logDTO.getBeforeState()))
                .afterState(convertToJson(logDTO.getAfterState()))
                .ipAddress(logDTO.getIpAddress())
                .userAgent(logDTO.getUserAgent())
                .deviceInfo(logDTO.getDeviceInfo())
                .result(logDTO.getResult())
                .errorMessage(logDTO.getErrorMessage())
                .important(logDTO.getImportant())
                .needNotification(logDTO.getNeedNotification())
                .tenantId(logDTO.getTenantId())
                .build();

            GroupManagementLogEntity savedLog = logRepository.save(logEntity);
            log.info("Group management log recorded: id={}, groupId={}, actionType={}", 
                     savedLog.getId(), savedLog.getGroupId(), savedLog.getActionType());
            
            // 如果标记为需要通知，触发通知处理
            if (Boolean.TRUE.equals(savedLog.getNeedNotification())) {
                handleNotification(savedLog);
            }
            
            return savedLog;
        } catch (Exception e) {
            log.error("Failed to record group management log", e);
            throw new RuntimeException("Failed to record group management log", e);
        }
    }

    /**
     * 批量记录日志
     */
    @Transactional
    public List<GroupManagementLogEntity> batchLogOperations(List<GroupManagementLogDTO> logDTOs) {
        List<GroupManagementLogEntity> logEntities = new ArrayList<>();
        for (GroupManagementLogDTO logDTO : logDTOs) {
            try {
                GroupManagementLogEntity logEntity = GroupManagementLogEntity.builder()
                    .groupId(logDTO.getGroupId())
                    .operatorId(logDTO.getOperatorId())
                    .operatorType(logDTO.getOperatorType())
                    .targetUserId(logDTO.getTargetUserId())
                    .actionType(logDTO.getActionType())
                    .actionSubType(logDTO.getActionSubType())
                    .description(logDTO.getDescription())
                    .details(convertToJson(logDTO.getDetails()))
                    .beforeState(convertToJson(logDTO.getBeforeState()))
                    .afterState(convertToJson(logDTO.getAfterState()))
                    .ipAddress(logDTO.getIpAddress())
                    .userAgent(logDTO.getUserAgent())
                    .deviceInfo(logDTO.getDeviceInfo())
                    .result(logDTO.getResult())
                    .errorMessage(logDTO.getErrorMessage())
                    .important(logDTO.getImportant())
                    .needNotification(logDTO.getNeedNotification())
                    .tenantId(logDTO.getTenantId())
                    .build();
                logEntities.add(logEntity);
            } catch (Exception e) {
                log.error("Failed to create log entity for DTO: {}", logDTO, e);
            }
        }
        
        List<GroupManagementLogEntity> savedLogs = logRepository.saveAll(logEntities);
        log.info("Batch group management logs recorded: count={}", savedLogs.size());
        
        // 处理需要通知的日志
        savedLogs.stream()
            .filter(log -> Boolean.TRUE.equals(log.getNeedNotification()))
            .forEach(this::handleNotification);
            
        return savedLogs;
    }

    /**
     * 根据ID查询日志
     */
    public Optional<GroupManagementLogEntity> findById(UUID id) {
        return logRepository.findById(id);
    }

    /**
     * 根据群组ID查询日志
     */
    public List<GroupManagementLogEntity> findByGroupId(UUID groupId) {
        return logRepository.findByGroupIdOrderByCreatedAtDesc(groupId);
    }

    /**
     * 根据群组ID分页查询日志
     */
    public Page<GroupManagementLogEntity> findByGroupId(UUID groupId, Pageable pageable) {
        return logRepository.findByGroupId(groupId, pageable);
    }

    /**
     * 根据操作者ID查询日志
     */
    public List<GroupManagementLogEntity> findByOperatorId(UUID operatorId) {
        return logRepository.findByOperatorIdOrderByCreatedAtDesc(operatorId);
    }

    /**
     * 根据目标用户ID查询日志
     */
    public List<GroupManagementLogEntity> findByTargetUserId(UUID targetUserId) {
        return logRepository.findByTargetUserIdOrderByCreatedAtDesc(targetUserId);
    }

    /**
     * 根据操作类型查询日志
     */
    public List<GroupManagementLogEntity> findByActionType(String actionType) {
        return logRepository.findByActionTypeOrderByCreatedAtDesc(actionType);
    }

    /**
     * 查询群组中特定操作类型的日志
     */
    public List<GroupManagementLogEntity> findByGroupIdAndActionType(UUID groupId, String actionType) {
        return logRepository.findByGroupIdAndActionTypeOrderByCreatedAtDesc(groupId, actionType);
    }

    /**
     * 查询操作者对目标用户的操作日志
     */
    public List<GroupManagementLogEntity> findByOperatorIdAndTargetUserId(UUID operatorId, UUID targetUserId) {
        return logRepository.findByOperatorIdAndTargetUserIdOrderByCreatedAtDesc(operatorId, targetUserId);
    }

    /**
     * 根据时间范围查询日志
     */
    public List<GroupManagementLogEntity> findByTimeRange(LocalDateTime start, LocalDateTime end) {
        return logRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
    }

    /**
     * 查询群组在时间范围内的日志
     */
    public List<GroupManagementLogEntity> findByGroupIdAndTimeRange(UUID groupId, LocalDateTime start, LocalDateTime end) {
        return logRepository.findByGroupIdAndCreatedAtBetweenOrderByCreatedAtDesc(groupId, start, end);
    }

    /**
     * 搜索日志
     */
    public List<GroupManagementLogEntity> searchLogs(
        UUID groupId, UUID operatorId, UUID targetUserId, 
        String actionType, String result, Boolean important,
        LocalDateTime startDate, LocalDateTime endDate
    ) {
        return logRepository.searchLogs(groupId, operatorId, targetUserId, actionType, result, important, startDate, endDate);
    }

    /**
     * 高级搜索日志
     */
    public Page<GroupManagementLogEntity> advancedSearch(
        UUID groupId, UUID operatorId, UUID targetUserId,
        String actionType, String actionSubType, String result,
        Boolean important, Boolean needNotification, Boolean notified,
        LocalDateTime startDate, LocalDateTime endDate,
        Pageable pageable
    ) {
        Specification<GroupManagementLogEntity> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (groupId != null) {
                predicates.add(criteriaBuilder.equal(root.get("groupId"), groupId));
            }
            if (operatorId != null) {
                predicates.add(criteriaBuilder.equal(root.get("operatorId"), operatorId));
            }
            if (targetUserId != null) {
                predicates.add(criteriaBuilder.equal(root.get("targetUserId"), targetUserId));
            }
            if (actionType != null && !actionType.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("actionType"), actionType));
            }
            if (actionSubType != null && !actionSubType.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("actionSubType"), actionSubType));
            }
            if (result != null && !result.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("result"), result));
            }
            if (important != null) {
                predicates.add(criteriaBuilder.equal(root.get("important"), important));
            }
            if (needNotification != null) {
                predicates.add(criteriaBuilder.equal(root.get("needNotification"), needNotification));
            }
            if (notified != null) {
                predicates.add(criteriaBuilder.equal(root.get("notified"), notified));
            }
            if (startDate != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), startDate));
            }
            if (endDate != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), endDate));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        
        return logRepository.findAll(spec, pageable);
    }

    /**
     * 获取群组最近的操作日志
     */
    public List<GroupManagementLogEntity> getRecentLogsByGroupId(UUID groupId, int limit) {
        return logRepository.findRecentByGroupId(groupId, limit);
    }

    /**
     * 获取操作者最近的操作日志
     */
    public List<GroupManagementLogEntity> getRecentLogsByOperatorId(UUID operatorId, int limit) {
        return logRepository.findRecentByOperatorId(operatorId, limit);
    }

    /**
     * 获取需要通知的日志
     */
    public List<GroupManagementLogEntity> getPendingNotificationLogs() {
        return logRepository.findByNeedNotificationTrueAndNotifiedFalse();
    }

    /**
     * 获取重要操作日志
     */
    public List<GroupManagementLogEntity> getImportantLogs() {
        return logRepository.findByImportantTrueOrderByCreatedAtDesc();
    }

    /**
     * 统计操作次数
     */
    public Long countByGroupId(UUID groupId) {
        return logRepository.countByGroupId(groupId);
    }

    /**
     * 统计操作者操作次数
     */
    public Long countByOperatorId(UUID operatorId) {
        return logRepository.countByOperatorId(operatorId);
    }

    /**
     * 获取操作统计信息
     */
    public Map<String, Object> getStatistics() {
        Object[] stats = logRepository.getStatistics();
        if (stats == null || stats.length == 0) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", stats[0]);
        result.put("success", stats[1]);
        result.put("failed", stats[2]);
        result.put("partial", stats[3]);
        result.put("important", stats[4]);
        result.put("pendingNotification", stats[5]);
        return result;
    }

    /**
     * 获取群组操作统计信息
     */
    public Map<String, Object> getStatisticsByGroupId(UUID groupId) {
        Object[] stats = logRepository.getStatisticsByGroupId(groupId);
        if (stats == null || stats.length == 0) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", stats[0]);
        result.put("success", stats[1]);
        result.put("failed", stats[2]);
        result.put("important", stats[3]);
        return result;
    }

    /**
     * 获取热门操作类型
     */
    public List<Map<String, Object>> getTopActionTypes() {
        return logRepository.getTopActionTypes().stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("actionType", row[0]);
                map.put("count", row[1]);
                return map;
            })
            .collect(Collectors.toList());
    }

    /**
     * 获取活跃操作者
     */
    public List<Map<String, Object>> getTopOperators() {
        return logRepository.getTopOperators().stream()
            .map(row -> {
                Map<String, Object> map = new HashMap<>();
                map.put("operatorId", row[0]);
                map.put("count", row[1]);
                return map;
            })
            .collect(Collectors.toList());
    }

    /**
     * 标记日志为已通知
     */
    @Transactional
    public int markAsNotified(List<UUID> logIds) {
        return logRepository.markAsNotified(logIds);
    }

    /**
     * 批量归档日志
     */
    @Transactional
    public int archiveLogs(List<UUID> logIds) {
        return logRepository.archiveLogs(logIds);
    }

    /**
     * 删除已归档的旧日志
     */
    @Transactional
    public int cleanupArchivedLogs(LocalDateTime cutoffDate) {
        return logRepository.deleteArchivedBefore(cutoffDate);
    }

    /**
     * 导出日志为CSV格式
     */
    public String exportToCsv(List<GroupManagementLogEntity> logs) {
        StringBuilder csv = new StringBuilder();
        csv.append("ID,GroupID,OperatorID,OperatorType,TargetUserID,ActionType,ActionSubType,Description,Result,Important,NeedNotification,Notified,CreatedAt\n");
        
        for (GroupManagementLogEntity log : logs) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s,%s\n",
                log.getId(),
                log.getGroupId(),
                log.getOperatorId(),
                log.getOperatorType(),
                log.getTargetUserId() != null ? log.getTargetUserId() : "",
                log.getActionType(),
                log.getActionSubType() != null ? log.getActionSubType() : "",
                escapeCsv(log.getDescription()),
                log.getResult(),
                log.getImportant(),
                log.getNeedNotification(),
                log.getNotified(),
                log.getCreatedAt()
            ));
        }
        
        return csv.toString();
    }

    /**
     * 导出日志为JSON格式
     */
    public String exportToJson(List<GroupManagementLogEntity> logs) throws JsonProcessingException {
        List<Map<String, Object>> logList = logs.stream()
            .map(log -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", log.getId().toString());
                map.put("groupId", log.getGroupId().toString());
                map.put("operatorId", log.getOperatorId().toString());
                map.put("operatorType", log.getOperatorType());
                map.put("targetUserId", log.getTargetUserId() != null ? log.getTargetUserId().toString() : null);
                map.put("actionType", log.getActionType());
                map.put("actionSubType", log.getActionSubType());
                map.put("description", log.getDescription());
                map.put("result", log.getResult());
                map.put("important", log.getImportant());
                map.put("needNotification", log.getNeedNotification());
                map.put("notified", log.getNotified());
                map.put("createdAt", log.getCreatedAt().toString());
                map.put("updatedAt", log.getUpdatedAt().toString());
                return map;
            })
            .collect(Collectors.toList());
            
        return objectMapper.writeValueAsString(logList);
    }

    /**
     * 检查重复操作
     */
    public boolean checkDuplicateOperation(UUID groupId, UUID operatorId, String actionType, UUID targetUserId, LocalDateTime within) {
        return logRepository.existsByGroupIdAndOperatorIdAndActionTypeAndTargetUserIdAndCreatedAtAfter(
            groupId, operatorId, actionType, targetUserId, within
        );
    }

    /**
     * 处理通知
     */
    private void handleNotification(GroupManagementLogEntity logEntity) {
        // 这里实现通知逻辑，可以通过WebSocket、邮件、推送等方式通知相关人员
        log.info("Handling notification for group management log: id={}, actionType={}", 
                 logEntity.getId(), logEntity.getActionType());
        
        // 标记为已通知
        logEntity.setNotified(true);
        logEntity.setUpdatedAt(LocalDateTime.now());
        logRepository.save(logEntity);
    }

    /**
     * 转换对象为JSON字符串
     */
    private String convertToJson(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("Failed to convert object to JSON", e);
            return null;
        }
    }

    /**
     * CSV转义处理
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}