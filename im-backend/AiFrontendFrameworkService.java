package com.im.system.service;

import com.im.system.entity.AiFrontendFrameworkEntity;
import com.im.system.repository.AiFrontendFrameworkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * AI 前端框架配置服务
 */
@Service
@Transactional
public class AiFrontendFrameworkService {

    @Autowired
    private AiFrontendFrameworkRepository repository;

    // 基础 CRUD 操作
    
    public AiFrontendFrameworkEntity createFramework(Long userId, String deviceId, String frameworkVersion) {
        AiFrontendFrameworkEntity entity = new AiFrontendFrameworkEntity(userId, deviceId, frameworkVersion);
        return repository.save(entity);
    }
    
    public Optional<AiFrontendFrameworkEntity> getFramework(Long id) {
        return repository.findById(id);
    }
    
    public Optional<AiFrontendFrameworkEntity> getFrameworkByUserAndDevice(Long userId, String deviceId) {
        return repository.findByUserIdAndDeviceId(userId, deviceId);
    }
    
    public List<AiFrontendFrameworkEntity> getFrameworksByUser(Long userId) {
        return repository.findByUserId(userId);
    }
    
    public List<AiFrontendFrameworkEntity> getFrameworksByDevice(String deviceId) {
        return repository.findByDeviceId(deviceId);
    }
    
    public AiFrontendFrameworkEntity updateFramework(Long id, Map<String, Object> updates) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findById(id);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            applyUpdates(entity, updates);
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }
    
    public AiFrontendFrameworkEntity updateFrameworkByUserAndDevice(Long userId, String deviceId, Map<String, Object> updates) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            applyUpdates(entity, updates);
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }
    
    public boolean deleteFramework(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public boolean deleteFrameworkByUserAndDevice(Long userId, String deviceId) {
        if (repository.existsByUserIdAndDeviceId(userId, deviceId)) {
            repository.deleteByUserIdAndDeviceId(userId, deviceId);
            return true;
        }
        return false;
    }
    
    // 配置管理
    
    public AiFrontendFrameworkEntity configureModel(Long userId, String deviceId, 
                                                    String modelName, String modelVersion, 
                                                    String localModelEngine, String inferenceBackend) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            entity.setModelName(modelName);
            entity.setModelVersion(modelVersion);
            entity.setLocalModelEngine(localModelEngine);
            entity.setInferenceBackend(inferenceBackend);
            entity.setModelLoaded(false);
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }
    
    public AiFrontendFrameworkEntity markModelLoaded(Long userId, String deviceId, Integer modelSizeMb) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            entity.setModelLoaded(true);
            entity.setModelLoadTime(LocalDateTime.now());
            entity.setModelSizeMb(modelSizeMb);
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }
    
    public AiFrontendFrameworkEntity markModelUnloaded(Long userId, String deviceId) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            entity.setModelLoaded(false);
            entity.setModelLoadTime(null);
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }
    
    // 功能管理
    
    public AiFrontendFrameworkEntity enableFeature(Long userId, String deviceId, String feature, boolean enable) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            entity.enableFeature(feature, enable);
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }
    
    public AiFrontendFrameworkEntity setPerformanceLevel(Long userId, String deviceId, String performanceLevel) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            entity.setPerformanceLevel(performanceLevel);
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }
    
    public AiFrontendFrameworkEntity setPrivacyMode(Long userId, String deviceId, boolean privacyMode) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            entity.setPrivacyMode(privacyMode);
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }
    
    public AiFrontendFrameworkEntity setOfflineMode(Long userId, String deviceId, boolean offlineMode) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            entity.setOfflineMode(offlineMode);
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }
    
    // 推理统计
    
    public AiFrontendFrameworkEntity recordInference(Long userId, String deviceId, boolean success, long latencyMs) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            entity.incrementInferenceStats(success, latencyMs);
            return repository.save(entity);
        }
        return null;
    }
    
    public AiFrontendFrameworkEntity resetInferenceStats(Long userId, String deviceId) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            entity.setInferenceStatsTotal(0L);
            entity.setInferenceStatsSuccess(0L);
            entity.setInferenceStatsAvgLatencyMs(0.0);
            entity.setUpdatedAt(LocalDateTime.now());
            return repository.save(entity);
        }
        return null;
    }
    
    // 批量操作
    
    public List<AiFrontendFrameworkEntity> batchUpdate(List<Long> ids, Map<String, Object> updates) {
        List<AiFrontendFrameworkEntity> entities = repository.findByIds(ids);
        for (AiFrontendFrameworkEntity entity : entities) {
            applyUpdates(entity, updates);
            entity.setUpdatedAt(LocalDateTime.now());
        }
        return repository.saveAll(entities);
    }
    
    public int batchEnableFeature(List<Long> userIds, String feature, boolean enable) {
        List<AiFrontendFrameworkEntity> entities = repository.findByUserIds(userIds);
        for (AiFrontendFrameworkEntity entity : entities) {
            entity.enableFeature(feature, enable);
            entity.setUpdatedAt(LocalDateTime.now());
        }
        repository.saveAll(entities);
        return entities.size();
    }
    
    public int batchSetPerformanceLevel(List<String> deviceIds, String performanceLevel) {
        List<AiFrontendFrameworkEntity> entities = repository.findByDeviceIds(deviceIds);
        for (AiFrontendFrameworkEntity entity : entities) {
            entity.setPerformanceLevel(performanceLevel);
            entity.setUpdatedAt(LocalDateTime.now());
        }
        repository.saveAll(entities);
        return entities.size();
    }
    
    // 查询服务
    
    public Page<AiFrontendFrameworkEntity> getActiveFrameworks(Pageable pageable) {
        return repository.findActiveFrameworks(pageable);
    }
    
    public Page<AiFrontendFrameworkEntity> getFrameworksByUserPaged(Long userId, Pageable pageable) {
        return repository.findByUserIdPaged(userId, pageable);
    }
    
    public Page<AiFrontendFrameworkEntity> getLoadedModelsPaged(Pageable pageable) {
        return repository.findLoadedModelsPaged(pageable);
    }
    
    public Page<AiFrontendFrameworkEntity> searchFrameworks(String keyword, Pageable pageable) {
        return repository.searchByKeyword(keyword, pageable);
    }
    
    public Page<AiFrontendFrameworkEntity> searchUserFrameworks(Long userId, String keyword, Pageable pageable) {
        return repository.searchByUserIdAndKeyword(userId, keyword, pageable);
    }
    
    public List<AiFrontendFrameworkEntity> getActiveFrameworksByUser(Long userId) {
        return repository.findActiveByUserId(userId);
    }
    
    public List<AiFrontendFrameworkEntity> getFrameworksWithLoadedModel(String deviceId) {
        return repository.findWithLoadedModelByDeviceId(deviceId);
    }
    
    public List<AiFrontendFrameworkEntity> getPrivacyEnabledDevices() {
        return repository.findPrivacyEnabledDevices();
    }
    
    // 统计服务
    
    public Map<String, Object> getFrameworkStatistics() {
        long activeFrameworks = repository.countActiveFrameworks();
        long loadedModels = repository.countLoadedModels();
        long privacyEnabled = repository.countPrivacyEnabled();
        long offlineDevices = repository.countOfflineDevices();
        long uniqueUsers = repository.countUniqueActiveUsers();
        long uniqueDevices = repository.countUniqueActiveDevices();
        
        Double avgLatency = repository.getAverageInferenceLatency();
        Long totalInferences = repository.getTotalInferenceCount();
        Long successInferences = repository.getTotalSuccessInferenceCount();
        
        long smartReplyEnabled = repository.countSmartReplyEnabled();
        long messageSummaryEnabled = repository.countMessageSummaryEnabled();
        long sentimentAnalysisEnabled = repository.countSentimentAnalysisEnabled();
        
        return Map.of(
            "activeFrameworks", activeFrameworks,
            "loadedModels", loadedModels,
            "privacyEnabled", privacyEnabled,
            "offlineDevices", offlineDevices,
            "uniqueUsers", uniqueUsers,
            "uniqueDevices", uniqueDevices,
            "averageInferenceLatencyMs", avgLatency != null ? avgLatency : 0.0,
            "totalInferences", totalInferences != null ? totalInferences : 0L,
            "successInferences", successInferences != null ? successInferences : 0L,
            "successRate", totalInferences != null && totalInferences > 0 ? 
                          (double) successInferences / totalInferences : 0.0,
            "smartReplyEnabled", smartReplyEnabled,
            "messageSummaryEnabled", messageSummaryEnabled,
            "sentimentAnalysisEnabled", sentimentAnalysisEnabled
        );
    }
    
    public List<Object[]> getInferenceBackendDistribution() {
        return repository.countByInferenceBackend();
    }
    
    public List<Object[]> getModelEngineDistribution() {
        return repository.countByModelEngine();
    }
    
    public List<Object[]> getPerformanceLevelDistribution() {
        return repository.countByPerformanceLevel();
    }
    
    public List<Object[]> getModelUsageStatistics() {
        return repository.getModelUsageStatistics();
    }
    
    public List<Object[]> getDeviceDistribution() {
        return repository.countDevicesPerUser();
    }
    
    // 清理服务
    
    public int cleanupInactiveFrameworks(LocalDateTime threshold) {
        return repository.deleteInactiveBefore(threshold);
    }
    
    public int cleanupUnloadedModels(LocalDateTime threshold) {
        return repository.deleteUnloadedModelsBefore(threshold);
    }
    
    public List<AiFrontendFrameworkEntity> findInactiveFrameworks(LocalDateTime threshold) {
        return repository.findInactiveBefore(threshold);
    }
    
    public List<AiFrontendFrameworkEntity> findUnloadedModels(LocalDateTime threshold) {
        return repository.findUnloadedModelsBefore(threshold);
    }
    
    // 验证服务
    
    public boolean validateFrameworkConfiguration(Long userId, String deviceId) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        if (optional.isPresent()) {
            AiFrontendFrameworkEntity entity = optional.get();
            return entity.getEnabled() && entity.getModelLoaded();
        }
        return false;
    }
    
    public boolean isFeatureEnabled(Long userId, String deviceId, String feature) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        return optional.map(entity -> entity.isFeatureEnabled(feature)).orElse(false);
    }
    
    public boolean isPrivacyModeEnabled(Long userId, String deviceId) {
        Optional<AiFrontendFrameworkEntity> optional = repository.findByUserIdAndDeviceId(userId, deviceId);
        return optional.map(AiFrontendFrameworkEntity::getPrivacyMode).orElse(true);
    }
    
    // 辅助方法
    
    private void applyUpdates(AiFrontendFrameworkEntity entity, Map<String, Object> updates) {
        if (updates.containsKey("enabled")) {
            entity.setEnabled((Boolean) updates.get("enabled"));
        }
        if (updates.containsKey("frameworkVersion")) {
            entity.setFrameworkVersion((String) updates.get("frameworkVersion"));
        }
        if (updates.containsKey("localModelEngine")) {
            entity.setLocalModelEngine((String) updates.get("localModelEngine"));
        }
        if (updates.containsKey("modelName")) {
            entity.setModelName((String) updates.get("modelName"));
        }
        if (updates.containsKey("modelVersion")) {
            entity.setModelVersion((String) updates.get("modelVersion"));
        }
        if (updates.containsKey("inferenceBackend")) {
            entity.setInferenceBackend((String) updates.get("inferenceBackend"));
        }
        if (updates.containsKey("maxMemoryMb")) {
            entity.setMaxMemoryMb((Integer) updates.get("maxMemoryMb"));
        }
        if (updates.containsKey("performanceLevel")) {
            entity.setPerformanceLevel((String) updates.get("performanceLevel"));
        }
        if (updates.containsKey("inferenceBatchSize")) {
            entity.setInferenceBatchSize((Integer) updates.get("inferenceBatchSize"));
        }
        if (updates.containsKey("modelUpdateFrequency")) {
            entity.setModelUpdateFrequency((String) updates.get("modelUpdateFrequency"));
        }
        if (updates.containsKey("customConfig")) {
            entity.setCustomConfig((Map<String, Object>) updates.get("customConfig"));
        }
        if (updates.containsKey("modelMetadata")) {
            entity.setModelMetadata((Map<String, Object>) updates.get("modelMetadata"));
        }
        if (updates.containsKey("featureEnabledSmartReply")) {
            entity.setFeatureEnabledSmartReply((Boolean) updates.get("featureEnabledSmartReply"));
        }
        if (updates.containsKey("featureEnabledMessageSummary")) {
            entity.setFeatureEnabledMessageSummary((Boolean) updates.get("featureEnabledMessageSummary"));
        }
        if (updates.containsKey("featureEnabledSentimentAnalysis")) {
            entity.setFeatureEnabledSentimentAnalysis((Boolean) updates.get("featureEnabledSentimentAnalysis"));
        }
        if (updates.containsKey("privacyMode")) {
            entity.setPrivacyMode((Boolean) updates.get("privacyMode"));
        }
        if (updates.containsKey("offlineMode")) {
            entity.setOfflineMode((Boolean) updates.get("offlineMode"));
        }
    }
}