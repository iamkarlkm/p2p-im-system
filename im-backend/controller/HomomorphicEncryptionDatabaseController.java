package com.im.backend.controller;

import com.im.backend.entity.HomomorphicEncryptionDatabaseEntity;
import com.im.backend.entity.PrivacyPreservingQueryEntity;
import com.im.backend.service.HomomorphicEncryptionDatabaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 同态加密数据库 REST API 控制器
 * 提供加密数据库管理、隐私保护查询等 API 端点
 */
@RestController
@RequestMapping("/api/v1/homomorphic-encryption")
@CrossOrigin(origins = "*")
public class HomomorphicEncryptionDatabaseController {
    
    @Autowired
    private HomomorphicEncryptionDatabaseService databaseService;
    
    /**
     * 创建同态加密数据库
     * POST /api/v1/homomorphic-encryption/database
     */
    @PostMapping("/database")
    public ResponseEntity<Map<String, Object>> createDatabase(@RequestBody Map<String, Object> request) {
        try {
            HomomorphicEncryptionDatabaseEntity database = new HomomorphicEncryptionDatabaseEntity();
            
            // 从请求中提取参数
            database.setUserId(getLongFromMap(request, "userId"));
            database.setSessionId(getStringFromMap(request, "sessionId"));
            database.setDatabaseName(getStringFromMap(request, "databaseName", "default_db"));
            database.setDatabaseType(getStringFromMap(request, "databaseType", "MESSAGE"));
            database.setEncryptionScheme(getStringFromMap(request, "encryptionScheme", "CKKS"));
            database.setSecurityLevel(getStringFromMap(request, "securityLevel", "HIGH"));
            database.setKeySize(getIntegerFromMap(request, "keySize", 4096));
            database.setModulusSize(getIntegerFromMap(request, "modulusSize", 16384));
            database.setPlaintextModulus(getLongFromMap(request, "plaintextModulus", 65537L));
            database.setNoiseBudget(getIntegerFromMap(request, "noiseBudget", 100));
            
            // 压缩配置
            database.setCompressionEnabled(getBooleanFromMap(request, "compressionEnabled", true));
            database.setCompressionAlgorithm(getStringFromMap(request, "compressionAlgorithm", "ZSTD"));
            
            // 索引配置
            database.setIndexingEnabled(getBooleanFromMap(request, "indexingEnabled", true));
            database.setIndexType(getStringFromMap(request, "indexType", "BALANCED_TREE"));
            
            // 缓存配置
            database.setQueryCacheEnabled(getBooleanFromMap(request, "queryCacheEnabled", true));
            
            // 并行配置
            database.setParallelismEnabled(getBooleanFromMap(request, "parallelismEnabled", true));
            database.setMaxParallelThreads(getIntegerFromMap(request, "maxParallelThreads", 8));
            
            // 硬件加速
            database.setHardwareAcceleration(getBooleanFromMap(request, "hardwareAcceleration", false));
            
            // 隐私配置
            database.setPrivacyBudget(getDoubleFromMap(request, "privacyBudget", 100.0));
            database.setDifferentialPrivacyEnabled(getBooleanFromMap(request, "differentialPrivacyEnabled", true));
            database.setDpEpsilon(getDoubleFromMap(request, "dpEpsilon", 1.0));
            database.setDpDelta(getDoubleFromMap(request, "dpDelta", 0.00001));
            
            // 数据保留
            database.setDataRetentionDays(getIntegerFromMap(request, "dataRetentionDays", 365));
            database.setAuditLoggingEnabled(getBooleanFromMap(request, "auditLoggingEnabled", true));
            database.setAuditRetentionDays(getIntegerFromMap(request, "auditRetentionDays", 90));
            
            HomomorphicEncryptionDatabaseEntity created = databaseService.createDatabase(database);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "同态加密数据库创建成功");
            response.put("data", created.toMap());
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("创建数据库失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取数据库信息
     * GET /api/v1/homomorphic-encryption/database/{databaseId}
     */
    @GetMapping("/database/{databaseId}")
    public ResponseEntity<Map<String, Object>> getDatabase(@PathVariable Long databaseId) {
        try {
            HomomorphicEncryptionDatabaseEntity database = databaseService.getDatabase(databaseId);
            
            if (database == null) {
                return buildErrorResponse("数据库不存在", 404);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", database.toMap());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("获取数据库失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的所有数据库
     * GET /api/v1/homomorphic-encryption/user/{userId}/databases
     */
    @GetMapping("/user/{userId}/databases")
    public ResponseEntity<Map<String, Object>> getUserDatabases(@PathVariable Long userId) {
        try {
            List<HomomorphicEncryptionDatabaseEntity> databases = databaseService.getUserDatabases(userId);
            
            List<Map<String, Object>> databaseMaps = new ArrayList<>();
            for (HomomorphicEncryptionDatabaseEntity db : databases) {
                databaseMaps.add(db.toMap());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", databases.size());
            response.put("data", databaseMaps);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("获取数据库列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新数据库状态
     * PUT /api/v1/homomorphic-encryption/database/{databaseId}/status
     */
    @PutMapping("/database/{databaseId}/status")
    public ResponseEntity<Map<String, Object>> updateDatabaseStatus(
            @PathVariable Long databaseId,
            @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null || status.isEmpty()) {
                return buildErrorResponse("状态不能为空");
            }
            
            HomomorphicEncryptionDatabaseEntity updated = databaseService.updateDatabaseStatus(databaseId, status);
            
            if (updated == null) {
                return buildErrorResponse("数据库不存在", 404);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "数据库状态更新成功");
            response.put("data", updated.toMap());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("更新数据库状态失败：" + e.getMessage());
        }
    }
    
    /**
     * 创建隐私保护查询
     * POST /api/v1/homomorphic-encryption/query
     */
    @PostMapping("/query")
    public ResponseEntity<Map<String, Object>> createQuery(@RequestBody Map<String, Object> request) {
        try {
            PrivacyPreservingQueryEntity query = new PrivacyPreservingQueryEntity();
            
            // 从请求中提取参数
            query.setDatabaseId(getLongFromMap(request, "databaseId"));
            query.setUserId(getLongFromMap(request, "userId"));
            query.setSessionId(getStringFromMap(request, "sessionId"));
            query.setQueryType(getStringFromMap(request, "queryType", "SELECT"));
            query.setQuerySql(getStringFromMap(request, "querySql"));
            query.setPrivacyLevel(getStringFromMap(request, "privacyLevel", "HIGH"));
            query.setEncryptionMethod(getStringFromMap(request, "encryptionMethod", "HOMOMORPHIC"));
            
            // 查询参数
            query.setQueryParameters(getStringFromMap(request, "queryParameters"));
            query.setQueryFilters(getStringFromMap(request, "queryFilters"));
            query.setProjectionFields(getStringFromMap(request, "projectionFields"));
            query.setSortFields(getStringFromMap(request, "sortFields"));
            
            // 分页
            query.setLimitValue(getIntegerFromMap(request, "limit", 100));
            query.setOffsetValue(getIntegerFromMap(request, "offset", 0));
            
            // 隐私保护
            query.setDifferentialPrivacyEnabled(getBooleanFromMap(request, "differentialPrivacyEnabled", true));
            query.setResultEncryptionEnabled(getBooleanFromMap(request, "resultEncryptionEnabled", true));
            
            // 优化配置
            query.setQueryOptimizationEnabled(getBooleanFromMap(request, "queryOptimizationEnabled", true));
            query.setParallelExecutionEnabled(getBooleanFromMap(request, "parallelExecutionEnabled", true));
            query.setParallelDegree(getIntegerFromMap(request, "parallelDegree", 4));
            query.setCacheEnabled(getBooleanFromMap(request, "cacheEnabled", true));
            
            // 验证配置
            query.setResultVerificationEnabled(getBooleanFromMap(request, "resultVerificationEnabled", true));
            query.setVerificationMethod(getStringFromMap(request, "verificationMethod", "MERKLE_TREE"));
            
            // 审计
            query.setAuditTrailEnabled(getBooleanFromMap(request, "auditTrailEnabled", true));
            query.setAccessControlEnforced(getBooleanFromMap(request, "accessControlEnforced", true));
            query.setComplianceCheckEnabled(getBooleanFromMap(request, "complianceCheckEnabled", true));
            
            PrivacyPreservingQueryEntity created = databaseService.createQuery(query);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "隐私保护查询创建成功");
            response.put("queryUuid", created.getQueryUuid());
            response.put("data", created.toMap());
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("创建查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 执行查询
     * POST /api/v1/homomorphic-encryption/query/{queryUuid}/execute
     */
    @PostMapping("/query/{queryUuid}/execute")
    public ResponseEntity<Map<String, Object>> executeQuery(@PathVariable String queryUuid) {
        try {
            PrivacyPreservingQueryEntity result = databaseService.executeQuery(queryUuid);
            
            if (result == null) {
                return buildErrorResponse("查询不存在", 404);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询执行完成");
            response.put("queryStatus", result.getQueryStatus());
            response.put("resultRowCount", result.getResultRowCount());
            response.put("resultDataSizeBytes", result.getResultDataSizeBytes());
            response.put("executionTimeMs", result.getQueryExecutionTimeMs());
            response.put("privacyScore", result.getPrivacyScore());
            response.put("accuracyScore", result.getAccuracyScore());
            response.put("data", result.toMap());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("执行查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取查询信息
     * GET /api/v1/homomorphic-encryption/query/{queryUuid}
     */
    @GetMapping("/query/{queryUuid}")
    public ResponseEntity<Map<String, Object>> getQuery(@PathVariable String queryUuid) {
        try {
            PrivacyPreservingQueryEntity query = databaseService.getQuery(queryUuid);
            
            if (query == null) {
                return buildErrorResponse("查询不存在", 404);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", query.toMap());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("获取查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的所有查询
     * GET /api/v1/homomorphic-encryption/user/{userId}/queries
     */
    @GetMapping("/user/{userId}/queries")
    public ResponseEntity<Map<String, Object>> getUserQueries(@PathVariable Long userId) {
        try {
            List<PrivacyPreservingQueryEntity> queries = databaseService.getUserQueries(userId);
            
            List<Map<String, Object>> queryMaps = new ArrayList<>();
            for (PrivacyPreservingQueryEntity q : queries) {
                queryMaps.add(q.toMap());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", queries.size());
            response.put("data", queryMaps);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("获取查询列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 取消查询
     * POST /api/v1/homomorphic-encryption/query/{queryUuid}/cancel
     */
    @PostMapping("/query/{queryUuid}/cancel")
    public ResponseEntity<Map<String, Object>> cancelQuery(@PathVariable String queryUuid) {
        try {
            PrivacyPreservingQueryEntity cancelled = databaseService.cancelQuery(queryUuid);
            
            if (cancelled == null) {
                return buildErrorResponse("查询不存在或无法取消", 404);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "查询已取消");
            response.put("data", cancelled.toMap());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("取消查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取数据库统计信息
     * GET /api/v1/homomorphic-encryption/database/{databaseId}/statistics
     */
    @GetMapping("/database/{databaseId}/statistics")
    public ResponseEntity<Map<String, Object>> getDatabaseStatistics(@PathVariable Long databaseId) {
        try {
            Map<String, Object> stats = databaseService.getDatabaseStatistics(databaseId);
            
            if (stats.isEmpty()) {
                return buildErrorResponse("数据库不存在", 404);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("获取统计信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取查询统计信息
     * GET /api/v1/homomorphic-encryption/database/{databaseId}/query-statistics
     */
    @GetMapping("/database/{databaseId}/query-statistics")
    public ResponseEntity<Map<String, Object>> getQueryStatistics(@PathVariable Long databaseId) {
        try {
            Map<String, Object> stats = databaseService.getQueryStatistics(databaseId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("获取查询统计失败：" + e.getMessage());
        }
    }
    
    /**
     * 批量执行查询
     * POST /api/v1/homomorphic-encryption/query/batch-execute
     */
    @PostMapping("/query/batch-execute")
    public ResponseEntity<Map<String, Object>> batchExecuteQueries(@RequestBody List<String> queryUuids) {
        try {
            List<PrivacyPreservingQueryEntity> results = databaseService.batchExecuteQueries(queryUuids);
            
            List<Map<String, Object>> resultMaps = new ArrayList<>();
            for (PrivacyPreservingQueryEntity r : results) {
                if (r != null) {
                    resultMaps.add(r.toMap());
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", resultMaps.size());
            response.put("data", resultMaps);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("批量执行查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 优化数据库性能
     * POST /api/v1/homomorphic-encryption/database/{databaseId}/optimize
     */
    @PostMapping("/database/{databaseId}/optimize")
    public ResponseEntity<Map<String, Object>> optimizeDatabase(@PathVariable Long databaseId) {
        try {
            databaseService.optimizeDatabase(databaseId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "数据库优化完成");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("优化数据库失败：" + e.getMessage());
        }
    }
    
    /**
     * 重新生成密钥
     * POST /api/v1/homomorphic-encryption/database/{databaseId}/rekey
     */
    @PostMapping("/database/{databaseId}/rekey")
    public ResponseEntity<Map<String, Object>> rekeyDatabase(@PathVariable Long databaseId) {
        try {
            databaseService.rekeyDatabase(databaseId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "密钥重新生成成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("重新生成密钥失败：" + e.getMessage());
        }
    }
    
    /**
     * 备份数据库
     * POST /api/v1/homomorphic-encryption/database/{databaseId}/backup
     */
    @PostMapping("/database/{databaseId}/backup")
    public ResponseEntity<Map<String, Object>> backupDatabase(@PathVariable Long databaseId) {
        try {
            databaseService.backupDatabase(databaseId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "数据库备份成功");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return buildErrorResponse("备份数据库失败：" + e.getMessage());
        }
    }
    
    // 辅助方法
    private Long getLongFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) return null;
        if (value instanceof Number) return ((Number) value).longValue();
        try { return Long.parseLong(value.toString()); } catch (Exception e) { return null; }
    }
    
    private Integer getIntegerFromMap(Map<String, Object> map, String key, Integer defaultValue) {
        Object value = map.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Number) return ((Number) value).intValue();
        try { return Integer.parseInt(value.toString()); } catch (Exception e) { return defaultValue; }
    }
    
    private Double getDoubleFromMap(Map<String, Object> map, String key, Double defaultValue) {
        Object value = map.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try { return Double.parseDouble(value.toString()); } catch (Exception e) { return defaultValue; }
    }
    
    private Boolean getBooleanFromMap(Map<String, Object> map, String key, Boolean defaultValue) {
        Object value = map.get(key);
        if (value == null) return defaultValue;
        if (value instanceof Boolean) return (Boolean) value;
        try { return Boolean.parseBoolean(value.toString()); } catch (Exception e) { return defaultValue; }
    }
    
    private String getStringFromMap(Map<String, Object> map, String key, String defaultValue) {
        Object value = map.get(key);
        return value != null ? value.toString() : defaultValue;
    }
    
    private String getStringFromMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message) {
        return buildErrorResponse(message, 500);
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, int statusCode) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        error.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(statusCode).body(error);
    }
}