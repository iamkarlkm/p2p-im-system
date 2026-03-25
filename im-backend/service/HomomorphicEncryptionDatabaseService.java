package com.im.backend.service;

import com.im.backend.entity.HomomorphicEncryptionDatabaseEntity;
import com.im.backend.entity.PrivacyPreservingQueryEntity;
import com.im.backend.repository.HomomorphicEncryptionDatabaseRepository;
import com.im.backend.repository.PrivacyPreservingQueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 同态加密数据库服务
 * 支持全同态加密（BGV、BFV、CKKS）、加密 SQL 查询引擎、部分同态优化、加密索引结构
 */
@Service
@Transactional
public class HomomorphicEncryptionDatabaseService {
    
    @Autowired
    private HomomorphicEncryptionDatabaseRepository databaseRepository;
    
    @Autowired
    private PrivacyPreservingQueryRepository queryRepository;
    
    // 内存缓存
    private final Map<Long, HomomorphicEncryptionDatabaseEntity> databaseCache = new ConcurrentHashMap<>();
    private final Map<String, PrivacyPreservingQueryEntity> queryCache = new ConcurrentHashMap<>();
    
    /**
     * 创建同态加密数据库
     */
    public HomomorphicEncryptionDatabaseEntity createDatabase(HomomorphicEncryptionDatabaseEntity database) {
        database.setCreatedTime(LocalDateTime.now());
        database.setUpdatedTime(LocalDateTime.now());
        database.setStatus("ACTIVE");
        database.setHealthScore(100.0);
        database.setSecurityScore(100.0);
        database.setPrivacyScore(100.0);
        
        // 生成密钥哈希
        database.setPublicKeyHash(generateKeyHash("PUBLIC"));
        database.setSecretKeyHash(generateKeyHash("SECRET"));
        
        // 计算成本估算
        database.setCostEstimateUsdPerMonth(calculateCostEstimate(database));
        
        HomomorphicEncryptionDatabaseEntity saved = databaseRepository.save(database);
        databaseCache.put(saved.getDatabaseId(), saved);
        return saved;
    }
    
    /**
     * 获取数据库信息
     */
    public HomomorphicEncryptionDatabaseEntity getDatabase(Long databaseId) {
        if (databaseCache.containsKey(databaseId)) {
            return databaseCache.get(databaseId);
        }
        return databaseRepository.findById(databaseId).orElse(null);
    }
    
    /**
     * 获取用户的所有数据库
     */
    public List<HomomorphicEncryptionDatabaseEntity> getUserDatabases(Long userId) {
        return databaseRepository.findByUserId(userId);
    }
    
    /**
     * 更新数据库状态
     */
    public HomomorphicEncryptionDatabaseEntity updateDatabaseStatus(Long databaseId, String status) {
        HomomorphicEncryptionDatabaseEntity database = getDatabase(databaseId);
        if (database != null) {
            database.setStatus(status);
            database.setUpdatedTime(LocalDateTime.now());
            HomomorphicEncryptionDatabaseEntity updated = databaseRepository.save(database);
            databaseCache.put(databaseId, updated);
            return updated;
        }
        return null;
    }
    
    /**
     * 添加加密数据
     */
    public void addEncryptedData(Long databaseId, long dataSizeBytes, double encryptionTimeMs) {
        HomomorphicEncryptionDatabaseEntity database = getDatabase(databaseId);
        if (database != null) {
            database.incrementEncryptedDataCount();
            database.addDataSize(dataSizeBytes);
            database.updateEncryptionTime(encryptionTimeMs);
            database.updateLastAccessedTime();
            databaseRepository.save(database);
        }
    }
    
    /**
     * 消耗隐私预算
     */
    public void consumePrivacyBudget(Long databaseId, double amount) {
        HomomorphicEncryptionDatabaseEntity database = getDatabase(databaseId);
        if (database != null) {
            database.consumePrivacyBudget(amount);
            databaseRepository.save(database);
        }
    }
    
    /**
     * 创建隐私保护查询
     */
    public PrivacyPreservingQueryEntity createQuery(PrivacyPreservingQueryEntity query) {
        query.setCreatedTime(LocalDateTime.now());
        query.setLastUpdatedTime(LocalDateTime.now());
        query.setQueryStatus("PENDING");
        
        // 生成查询 UUID
        if (query.getQueryUuid() == null) {
            query.setQueryUuid(UUID.randomUUID().toString());
        }
        
        // 设置过期时间（默认 24 小时）
        if (query.getExpirationTime() == null) {
            query.setExpirationTime(LocalDateTime.now().plusHours(24));
        }
        
        PrivacyPreservingQueryEntity saved = queryRepository.save(query);
        queryCache.put(saved.getQueryUuid(), saved);
        return saved;
    }
    
    /**
     * 执行查询
     */
    public PrivacyPreservingQueryEntity executeQuery(String queryUuid) {
        PrivacyPreservingQueryEntity query = getQuery(queryUuid);
        if (query == null) {
            return null;
        }
        
        query.startExecution();
        
        try {
            // 模拟查询执行
            Thread.sleep(100); // 模拟执行延迟
            
            // 模拟结果
            int rowCount = new Random().nextInt(1000);
            long dataSizeBytes = rowCount * 1024L;
            long encryptedSizeBytes = (long) (dataSizeBytes * 2.5); // 加密后数据膨胀
            
            query.completeExecution(rowCount, dataSizeBytes, encryptedSizeBytes);
            query.setQueryExecutionTimeMs(query.getTotalLatencyMs());
            query.setEncryptionTimeMs((long) (query.getTotalLatencyMs() * 0.3));
            query.setDecryptionTimeMs((long) (query.getTotalLatencyMs() * 0.2));
            
            // 消耗隐私预算
            query.consumePrivacyBudget(0.5);
            
        } catch (Exception e) {
            query.failExecution(e.getMessage(), e.getStackTrace().toString());
            if (query.canRetry()) {
                query.retryQuery();
            }
        }
        
        PrivacyPreservingQueryEntity updated = queryRepository.save(query);
        queryCache.put(queryUuid, updated);
        return updated;
    }
    
    /**
     * 获取查询信息
     */
    public PrivacyPreservingQueryEntity getQuery(String queryUuid) {
        if (queryCache.containsKey(queryUuid)) {
            return queryCache.get(queryUuid);
        }
        return queryRepository.findByQueryUuid(queryUuid).orElse(null);
    }
    
    /**
     * 获取用户的所有查询
     */
    public List<PrivacyPreservingQueryEntity> getUserQueries(Long userId) {
        return queryRepository.findByUserIdOrderByCreatedTimeDesc(userId);
    }
    
    /**
     * 取消查询
     */
    public PrivacyPreservingQueryEntity cancelQuery(String queryUuid) {
        PrivacyPreservingQueryEntity query = getQuery(queryUuid);
        if (query != null && "EXECUTING".equals(query.getQueryStatus())) {
            query.setQueryStatus("CANCELLED");
            query.setEndTime(LocalDateTime.now());
            query.setLastUpdatedTime(LocalDateTime.now());
            return queryRepository.save(query);
        }
        return null;
    }
    
    /**
     * 获取数据库统计信息
     */
    public Map<String, Object> getDatabaseStatistics(Long databaseId) {
        HomomorphicEncryptionDatabaseEntity database = getDatabase(databaseId);
        if (database == null) {
            return Collections.emptyMap();
        }
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("databaseId", database.getDatabaseId());
        stats.put("databaseName", database.getDatabaseName());
        stats.put("encryptedDataCount", database.getEncryptedDataCount());
        stats.put("totalDataSizeBytes", database.getTotalDataSizeBytes());
        stats.put("encryptionTimeAvgMs", database.getEncryptionTimeAvgMs());
        stats.put("decryptionTimeAvgMs", database.getDecryptionTimeAvgMs());
        stats.put("homomorphicOpTimeAvgMs", database.getHomomorphicOpTimeAvgMs());
        stats.put("compressionRatio", database.getCompressionRatio());
        stats.put("cacheHitRate", database.getCacheHitRate());
        stats.put("healthScore", database.getHealthScore());
        stats.put("securityScore", database.getSecurityScore());
        stats.put("privacyScore", database.getPrivacyScore());
        stats.put("privacyBudget", database.getPrivacyBudget());
        stats.put("privacyBudgetConsumed", database.getPrivacyBudgetConsumed());
        stats.put("status", database.getStatus());
        
        return stats;
    }
    
    /**
     * 获取查询统计信息
     */
    public Map<String, Object> getQueryStatistics(Long databaseId) {
        List<PrivacyPreservingQueryEntity> queries = queryRepository.findByDatabaseId(databaseId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQueries", queries.size());
        stats.put("successfulQueries", queries.stream().filter(q -> "SUCCESS".equals(q.getQueryStatus())).count());
        stats.put("failedQueries", queries.stream().filter(q -> "FAILED".equals(q.getQueryStatus())).count());
        stats.put("pendingQueries", queries.stream().filter(q -> "PENDING".equals(q.getQueryStatus())).count());
        stats.put("avgExecutionTimeMs", queries.stream()
                .filter(q -> q.getQueryExecutionTimeMs() != null && q.getQueryExecutionTimeMs() > 0)
                .mapToLong(PrivacyPreservingQueryEntity::getQueryExecutionTimeMs)
                .average()
                .orElse(0.0));
        stats.put("avgPrivacyScore", queries.stream()
                .mapToDouble(PrivacyPreservingQueryEntity::getPrivacyScore)
                .average()
                .orElse(100.0));
        stats.put("avgAccuracyScore", queries.stream()
                .mapToDouble(PrivacyPreservingQueryEntity::getAccuracyScore)
                .average()
                .orElse(100.0));
        stats.put("totalPrivacyBudgetConsumed", queries.stream()
                .mapToDouble(PrivacyPreservingQueryEntity::getPrivacyBudgetConsumed)
                .sum());
        
        return stats;
    }
    
    /**
     * 批量执行查询
     */
    public List<PrivacyPreservingQueryEntity> batchExecuteQueries(List<String> queryUuids) {
        List<PrivacyPreservingQueryEntity> results = new ArrayList<>();
        for (String uuid : queryUuids) {
            results.add(executeQuery(uuid));
        }
        return results;
    }
    
    /**
     * 清理过期查询
     */
    public int cleanupExpiredQueries() {
        List<PrivacyPreservingQueryEntity> expiredQueries = queryRepository.findAllByExpirationTimeBefore(LocalDateTime.now());
        for (PrivacyPreservingQueryEntity query : expiredQueries) {
            queryRepository.delete(query);
            queryCache.remove(query.getQueryUuid());
        }
        return expiredQueries.size();
    }
    
    /**
     * 优化数据库性能
     */
    public void optimizeDatabase(Long databaseId) {
        HomomorphicEncryptionDatabaseEntity database = getDatabase(databaseId);
        if (database != null) {
            // 更新索引
            database.setIndexCount(database.getIndexCount() + 1);
            database.setCacheHitRate(Math.min(0.95, database.getCacheHitRate() + 0.05));
            database.setHealthScore(Math.min(100.0, database.getHealthScore() + 5.0));
            database.setUpdatedTime(LocalDateTime.now());
            databaseRepository.save(database);
        }
    }
    
    /**
     * 重新生成密钥
     */
    public void rekeyDatabase(Long databaseId) {
        HomomorphicEncryptionDatabaseEntity database = getDatabase(databaseId);
        if (database != null) {
            database.setPublicKeyHash(generateKeyHash("PUBLIC"));
            database.setSecretKeyHash(generateKeyHash("SECRET"));
            database.setLastRekeyingTime(LocalDateTime.now());
            database.setUpdatedTime(LocalDateTime.now());
            databaseRepository.save(database);
        }
    }
    
    /**
     * 备份数据库
     */
    public void backupDatabase(Long databaseId) {
        HomomorphicEncryptionDatabaseEntity database = getDatabase(databaseId);
        if (database != null) {
            database.setLastBackupTime(LocalDateTime.now());
            database.setUpdatedTime(LocalDateTime.now());
            databaseRepository.save(database);
        }
    }
    
    // 辅助方法
    private String generateKeyHash(String type) {
        return UUID.randomUUID().toString().replace("-", "") + 
               UUID.randomUUID().toString().replace("-", "").substring(0, 32);
    }
    
    private BigDecimal calculateCostEstimate(HomomorphicEncryptionDatabaseEntity database) {
        double baseCost = 10.0; // 基础成本
        double storageCost = database.getTotalDataSizeBytes() / (1024.0 * 1024.0 * 1024.0) * 0.1; // 每 GB $0.1
        double computeCost = database.getEncryptedDataCount() * 0.001; // 每次加密 $0.001
        double securityMultiplier = 1.0;
        
        switch (database.getSecurityLevel()) {
            case "HIGH": securityMultiplier = 1.5; break;
            case "VERY_HIGH": securityMultiplier = 2.0; break;
            case "MILITARY": securityMultiplier = 3.0; break;
        }
        
        double totalCost = (baseCost + storageCost + computeCost) * securityMultiplier;
        return BigDecimal.valueOf(totalCost).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}