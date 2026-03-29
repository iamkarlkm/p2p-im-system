package com.im.backend.repository;

import com.im.backend.entity.MultimodalConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * 多模态配置仓储接口
 */
@Repository
public interface MultimodalConfigRepository extends JpaRepository<MultimodalConfigEntity, Long> {
    
    // 根据名称查找配置
    Optional<MultimodalConfigEntity> findByName(String name);
    
    // 查找所有启用的配置
    List<MultimodalConfigEntity> findByEnabledTrue();
    
    // 根据文本启用状态查找
    List<MultimodalConfigEntity> findByTextEnabledTrue();
    
    // 根据图像启用状态查找
    List<MultimodalConfigEntity> findByImageEnabledTrue();
    
    // 根据音频启用状态查找
    List<MultimodalConfigEntity> findByAudioEnabledTrue();
    
    // 根据视频启用状态查找
    List<MultimodalConfigEntity> findByVideoEnabledTrue();
    
    // 查找支持多模态融合的配置
    List<MultimodalConfigEntity> findByMultimodalFusionEnabledTrue();
    
    // 统计启用的配置数量
    @Query("SELECT COUNT(c) FROM MultimodalConfigEntity c WHERE c.enabled = true")
    long countEnabledConfigs();
    
    // 查找最新的配置版本
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.enabled = true ORDER BY c.version DESC")
    List<MultimodalConfigEntity> findLatestConfigs();
    
    // 根据模型名称查找配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.textModel = :model OR c.imageModel = :model OR c.audioModel = :model OR c.videoModel = :model")
    List<MultimodalConfigEntity> findByModel(@Param("model") String model);
    
    // 查找支持特定语言文本分析的配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.textEnabled = true AND c.textLanguages LIKE %:language%")
    List<MultimodalConfigEntity> findBySupportedLanguage(@Param("language") String language);
    
    // 查找支持特定格式图像的配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.imageEnabled = true AND c.imageSupportedFormats LIKE %:format%")
    List<MultimodalConfigEntity> findBySupportedImageFormat(@Param("format") String format);
    
    // 查找支持特定格式音频的配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.audioEnabled = true AND c.audioSupportedFormats LIKE %:format%")
    List<MultimodalConfigEntity> findBySupportedAudioFormat(@Param("format") String format);
    
    // 批量删除非启用的配置
    long deleteByEnabledFalse();
    
    // 查找名称匹配的配置（模糊搜索）
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MultimodalConfigEntity> findByNameContaining(@Param("keyword") String keyword);
    
    // 根据描述查找配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<MultimodalConfigEntity> findByDescriptionContaining(@Param("keyword") String keyword);
    
    // 统计文本分析配置数量
    @Query("SELECT COUNT(c) FROM MultimodalConfigEntity c WHERE c.textEnabled = true")
    long countTextAnalysisConfigs();
    
    // 统计图像分析配置数量
    @Query("SELECT COUNT(c) FROM MultimodalConfigEntity c WHERE c.imageEnabled = true")
    long countImageAnalysisConfigs();
    
    // 统计音频分析配置数量
    @Query("SELECT COUNT(c) FROM MultimodalConfigEntity c WHERE c.audioEnabled = true")
    long countAudioAnalysisConfigs();
    
    // 统计视频分析配置数量
    @Query("SELECT COUNT(c) FROM MultimodalConfigEntity c WHERE c.videoEnabled = true")
    long countVideoAnalysisConfigs();
    
    // 查找启用了缓存的配置
    List<MultimodalConfigEntity> findByCacheEnabledTrue();
    
    // 查找启用了监控的配置
    List<MultimodalConfigEntity> findByMetricsEnabledTrue();
    
    // 查找启用了隐私保护的配置
    List<MultimodalConfigEntity> findByPrivacyEnabledTrue();
    
    // 根据置信度阈值查找配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.confidenceThreshold >= :threshold")
    List<MultimodalConfigEntity> findByConfidenceThresholdGreaterThanEqual(@Param("threshold") Double threshold);
    
    // 根据超时时间查找配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.timeoutMs <= :timeout")
    List<MultimodalConfigEntity> findByTimeoutLessThanEqual(@Param("timeout") Integer timeout);
    
    // 查找支持批量处理的配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.batchSize > 1")
    List<MultimodalConfigEntity> findBatchProcessingConfigs();
    
    // 查找支持并发处理的配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.concurrentWorkers > 1")
    List<MultimodalConfigEntity> findConcurrentProcessingConfigs();
    
    // 查找最新创建的配置
    @Query("SELECT c FROM MultimodalConfigEntity c ORDER BY c.createdAt DESC")
    List<MultimodalConfigEntity> findLatestCreatedConfigs();
    
    // 查找最近更新的配置
    @Query("SELECT c FROM MultimodalConfigEntity c ORDER BY c.updatedAt DESC")
    List<MultimodalConfigEntity> findRecentlyUpdatedConfigs();
    
    // 根据创建时间范围查找配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.createdAt BETWEEN :start AND :end")
    List<MultimodalConfigEntity> findByCreatedAtBetween(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);
    
    // 根据更新时间范围查找配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.updatedAt BETWEEN :start AND :end")
    List<MultimodalConfigEntity> findByUpdatedAtBetween(@Param("start") java.time.LocalDateTime start, @Param("end") java.time.LocalDateTime end);
    
    // 统计启用了后备模型的配置数量
    @Query("SELECT COUNT(c) FROM MultimodalConfigEntity c WHERE c.fallbackEnabled = true")
    long countFallbackEnabledConfigs();
    
    // 查找支持特定融合方法的配置
    @Query("SELECT c FROM MultimodalConfigEntity c WHERE c.multimodalFusionEnabled = true AND c.fusionMethod = :method")
    List<MultimodalConfigEntity> findByFusionMethod(@Param("method") String method);
}