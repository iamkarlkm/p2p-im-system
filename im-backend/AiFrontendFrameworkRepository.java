package com.im.system.repository;

import com.im.system.entity.AiFrontendFrameworkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AI 前端框架配置数据访问仓库
 */
@Repository
public interface AiFrontendFrameworkRepository extends JpaRepository<AiFrontendFrameworkEntity, Long> {

    // 基础查询方法
    Optional<AiFrontendFrameworkEntity> findByUserIdAndDeviceId(Long userId, String deviceId);
    
    List<AiFrontendFrameworkEntity> findByUserId(Long userId);
    
    List<AiFrontendFrameworkEntity> findByDeviceId(String deviceId);
    
    List<AiFrontendFrameworkEntity> findByEnabled(Boolean enabled);
    
    List<AiFrontendFrameworkEntity> findByModelLoaded(Boolean modelLoaded);
    
    List<AiFrontendFrameworkEntity> findByPrivacyMode(Boolean privacyMode);
    
    List<AiFrontendFrameworkEntity> findByOfflineMode(Boolean offlineMode);
    
    // 按框架版本查询
    List<AiFrontendFrameworkEntity> findByFrameworkVersion(String frameworkVersion);
    
    // 按模型引擎查询
    List<AiFrontendFrameworkEntity> findByLocalModelEngine(String localModelEngine);
    
    // 按推理后端查询
    List<AiFrontendFrameworkEntity> findByInferenceBackend(String inferenceBackend);
    
    // 按性能级别查询
    List<AiFrontendFrameworkEntity> findByPerformanceLevel(String performanceLevel);
    
    // 功能启用状态查询
    List<AiFrontendFrameworkEntity> findByFeatureEnabledSmartReply(Boolean enabled);
    
    List<AiFrontendFrameworkEntity> findByFeatureEnabledMessageSummary(Boolean enabled);
    
    List<AiFrontendFrameworkEntity> findByFeatureEnabledSentimentAnalysis(Boolean enabled);
    
    // 复杂查询方法
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.userId = :userId AND a.enabled = true")
    List<AiFrontendFrameworkEntity> findActiveByUserId(@Param("userId") Long userId);
    
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.deviceId = :deviceId AND a.modelLoaded = true")
    List<AiFrontendFrameworkEntity> findWithLoadedModelByDeviceId(@Param("deviceId") String deviceId);
    
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.privacyMode = true AND a.offlineMode = false")
    List<AiFrontendFrameworkEntity> findPrivacyEnabledDevices();
    
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.modelLoadTime < :threshold AND a.modelLoaded = true")
    List<AiFrontendFrameworkEntity> findModelsLoadedBefore(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.lastModelUpdate < :threshold")
    List<AiFrontendFrameworkEntity> findModelsNotUpdatedSince(@Param("threshold") LocalDateTime threshold);
    
    // 统计查询
    @Query("SELECT COUNT(a) FROM AiFrontendFrameworkEntity a WHERE a.enabled = true")
    long countActiveFrameworks();
    
    @Query("SELECT COUNT(a) FROM AiFrontendFrameworkEntity a WHERE a.modelLoaded = true")
    long countLoadedModels();
    
    @Query("SELECT COUNT(a) FROM AiFrontendFrameworkEntity a WHERE a.privacyMode = true")
    long countPrivacyEnabled();
    
    @Query("SELECT COUNT(a) FROM AiFrontendFrameworkEntity a WHERE a.offlineMode = true")
    long countOfflineDevices();
    
    @Query("SELECT COUNT(DISTINCT a.userId) FROM AiFrontendFrameworkEntity a WHERE a.enabled = true")
    long countUniqueActiveUsers();
    
    @Query("SELECT COUNT(DISTINCT a.deviceId) FROM AiFrontendFrameworkEntity a WHERE a.enabled = true")
    long countUniqueActiveDevices();
    
    // 按功能统计
    @Query("SELECT COUNT(a) FROM AiFrontendFrameworkEntity a WHERE a.featureEnabledSmartReply = true AND a.enabled = true")
    long countSmartReplyEnabled();
    
    @Query("SELECT COUNT(a) FROM AiFrontendFrameworkEntity a WHERE a.featureEnabledMessageSummary = true AND a.enabled = true")
    long countMessageSummaryEnabled();
    
    @Query("SELECT COUNT(a) FROM AiFrontendFrameworkEntity a WHERE a.featureEnabledSentimentAnalysis = true AND a.enabled = true")
    long countSentimentAnalysisEnabled();
    
    // 性能统计查询
    @Query("SELECT AVG(a.inferenceStatsAvgLatencyMs) FROM AiFrontendFrameworkEntity a WHERE a.inferenceStatsTotal > 0")
    Double getAverageInferenceLatency();
    
    @Query("SELECT SUM(a.inferenceStatsTotal) FROM AiFrontendFrameworkEntity a")
    Long getTotalInferenceCount();
    
    @Query("SELECT SUM(a.inferenceStatsSuccess) FROM AiFrontendFrameworkEntity a")
    Long getTotalSuccessInferenceCount();
    
    @Query("SELECT a.inferenceBackend, COUNT(a) FROM AiFrontendFrameworkEntity a GROUP BY a.inferenceBackend")
    List<Object[]> countByInferenceBackend();
    
    @Query("SELECT a.localModelEngine, COUNT(a) FROM AiFrontendFrameworkEntity a GROUP BY a.localModelEngine")
    List<Object[]> countByModelEngine();
    
    @Query("SELECT a.performanceLevel, COUNT(a) FROM AiFrontendFrameworkEntity a GROUP BY a.performanceLevel")
    List<Object[]> countByPerformanceLevel();
    
    // 分页查询
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.enabled = true ORDER BY a.updatedAt DESC")
    Page<AiFrontendFrameworkEntity> findActiveFrameworks(Pageable pageable);
    
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.userId = :userId ORDER BY a.updatedAt DESC")
    Page<AiFrontendFrameworkEntity> findByUserIdPaged(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.modelLoaded = true ORDER BY a.modelLoadTime DESC")
    Page<AiFrontendFrameworkEntity> findLoadedModelsPaged(Pageable pageable);
    
    // 搜索查询
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE " +
           "a.deviceId LIKE %:keyword% OR " +
           "a.modelName LIKE %:keyword% OR " +
           "a.frameworkVersion LIKE %:keyword% OR " +
           "a.localModelEngine LIKE %:keyword%")
    Page<AiFrontendFrameworkEntity> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.userId = :userId AND " +
           "(a.deviceId LIKE %:keyword% OR a.modelName LIKE %:keyword%)")
    Page<AiFrontendFrameworkEntity> searchByUserIdAndKeyword(
            @Param("userId") Long userId, 
            @Param("keyword") String keyword, 
            Pageable pageable);
    
    // 批量操作查询
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.id IN :ids")
    List<AiFrontendFrameworkEntity> findByIds(@Param("ids") List<Long> ids);
    
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.userId IN :userIds")
    List<AiFrontendFrameworkEntity> findByUserIds(@Param("userIds") List<Long> userIds);
    
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.deviceId IN :deviceIds")
    List<AiFrontendFrameworkEntity> findByDeviceIds(@Param("deviceIds") List<String> deviceIds);
    
    // 清理查询
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.updatedAt < :threshold AND a.enabled = false")
    List<AiFrontendFrameworkEntity> findInactiveBefore(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT a FROM AiFrontendFrameworkEntity a WHERE a.modelLoadTime < :threshold AND a.modelLoaded = false")
    List<AiFrontendFrameworkEntity> findUnloadedModelsBefore(@Param("threshold") LocalDateTime threshold);
    
    // 设备统计查询
    @Query("SELECT a.deviceId, COUNT(a) FROM AiFrontendFrameworkEntity a GROUP BY a.deviceId HAVING COUNT(a) > 1")
    List<Object[]> findDuplicateDevices();
    
    @Query("SELECT a.userId, COUNT(DISTINCT a.deviceId) FROM AiFrontendFrameworkEntity a WHERE a.enabled = true GROUP BY a.userId")
    List<Object[]> countDevicesPerUser();
    
    // 模型使用统计
    @Query("SELECT a.modelName, COUNT(a), AVG(a.inferenceStatsAvgLatencyMs), SUM(a.inferenceStatsTotal) " +
           "FROM AiFrontendFrameworkEntity a " +
           "WHERE a.modelName IS NOT NULL " +
           "GROUP BY a.modelName " +
           "ORDER BY SUM(a.inferenceStatsTotal) DESC")
    List<Object[]> getModelUsageStatistics();
    
    // 时间范围统计
    @Query("SELECT COUNT(a) FROM AiFrontendFrameworkEntity a WHERE a.createdAt BETWEEN :start AND :end")
    long countCreatedBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(a) FROM AiFrontendFrameworkEntity a WHERE a.updatedAt BETWEEN :start AND :end AND a.enabled = true")
    long countUpdatedActiveBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // 检查是否存在
    boolean existsByUserIdAndDeviceId(Long userId, String deviceId);
    
    boolean existsByUserIdAndModelLoaded(Long userId, Boolean modelLoaded);
    
    boolean existsByDeviceIdAndModelLoaded(String deviceId, Boolean modelLoaded);
    
    // 删除方法
    void deleteByUserIdAndDeviceId(Long userId, String deviceId);
    
    void deleteByUserId(Long userId);
    
    void deleteByDeviceId(String deviceId);
    
    long deleteByEnabled(Boolean enabled);
    
    long deleteByModelLoaded(Boolean modelLoaded);
    
    @Query("DELETE FROM AiFrontendFrameworkEntity a WHERE a.updatedAt < :threshold AND a.enabled = false")
    int deleteInactiveBefore(@Param("threshold") LocalDateTime threshold);
    
    @Query("DELETE FROM AiFrontendFrameworkEntity a WHERE a.modelLoadTime < :threshold AND a.modelLoaded = false")
    int deleteUnloadedModelsBefore(@Param("threshold") LocalDateTime threshold);
}