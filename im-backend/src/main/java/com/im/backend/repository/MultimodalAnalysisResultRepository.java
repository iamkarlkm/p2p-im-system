package com.im.backend.repository;

import com.im.backend.entity.MultimodalAnalysisResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 多模态分析结果仓储接口
 */
@Repository
public interface MultimodalAnalysisResultRepository extends JpaRepository<MultimodalAnalysisResultEntity, Long> {
    
    // 根据请求ID查找结果
    Optional<MultimodalAnalysisResultEntity> findByRequestId(String requestId);
    
    // 根据会话ID查找结果
    List<MultimodalAnalysisResultEntity> findBySessionId(String sessionId);
    
    // 根据用户ID查找结果
    List<MultimodalAnalysisResultEntity> findByUserId(Long userId);
    
    // 根据消息ID查找结果
    Optional<MultimodalAnalysisResultEntity> findByMessageId(Long messageId);
    
    // 根据内容哈希查找结果
    List<MultimodalAnalysisResultEntity> findByContentHash(String contentHash);
    
    // 根据分析状态查找结果
    List<MultimodalAnalysisResultEntity> findByAnalysisStatus(String analysisStatus);
    
    // 查找已完成的分析结果
    List<MultimodalAnalysisResultEntity> findByAnalysisStatusOrderByCompletedAtDesc(String analysisStatus);
    
    // 根据内容类型查找结果
    List<MultimodalAnalysisResultEntity> findByContentType(String contentType);
    
    // 根据置信度查找高质量结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.confidenceScore >= :threshold")
    List<MultimodalAnalysisResultEntity> findByConfidenceScoreGreaterThanEqual(@Param("threshold") Double threshold);
    
    // 根据情感分数查找积极/消极结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.multimodalSentimentScore >= :minScore AND r.multimodalSentimentScore <= :maxScore")
    List<MultimodalAnalysisResultEntity> findBySentimentScoreBetween(@Param("minScore") Double minScore, @Param("maxScore") Double maxScore);
    
    // 查找特定情感的文本分析结果
    List<MultimodalAnalysisResultEntity> findByTextSentiment(String sentiment);
    
    // 查找特定情感的音频分析结果
    List<MultimodalAnalysisResultEntity> findByAudioEmotion(String emotion);
    
    // 查找包含特定标签的图像分析结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.imageTags LIKE %:tag%")
    List<MultimodalAnalysisResultEntity> findByImageTag(@Param("tag") String tag);
    
    // 查找包含特定关键词的文本分析结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.textKeywords LIKE %:keyword%")
    List<MultimodalAnalysisResultEntity> findByTextKeyword(@Param("keyword") String keyword);
    
    // 查找特定意图的分析结果
    List<MultimodalAnalysisResultEntity> findByTextIntent(String intent);
    
    // 查找特定场景的图像分析结果
    List<MultimodalAnalysisResultEntity> findByImageScene(String scene);
    
    // 查找缓存的結果
    List<MultimodalAnalysisResultEntity> findByCachedTrue();
    
    // 查找已过期的缓存结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.cached = true AND r.cacheExpiryAt < :now")
    List<MultimodalAnalysisResultEntity> findExpiredCache(@Param("now") LocalDateTime now);
    
    // 根据处理时间查找结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.processingTimeMs <= :maxTime")
    List<MultimodalAnalysisResultEntity> findByProcessingTimeLessThanEqual(@Param("maxTime") Long maxTime);
    
    // 查找使用特定模型的分析结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.modelUsed LIKE %:model%")
    List<MultimodalAnalysisResultEntity> findByModelUsed(@Param("model") String model);
    
    // 根据优先级查找结果
    List<MultimodalAnalysisResultEntity> findByPriorityOrderByCreatedAtAsc(Integer priority);
    
    // 查找成本较低的分析结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.costUnits <= :maxCost")
    List<MultimodalAnalysisResultEntity> findByCostLessThanEqual(@Param("maxCost") Double maxCost);
    
    // 根据质量评级查找结果
    List<MultimodalAnalysisResultEntity> findByQualityRating(String qualityRating);
    
    // 统计特定用户的分析结果数量
    @Query("SELECT COUNT(r) FROM MultimodalAnalysisResultEntity r WHERE r.userId = :userId")
    long countByUserId(@Param("userId") Long userId);
    
    // 统计特定内容类型的分析结果数量
    @Query("SELECT COUNT(r) FROM MultimodalAnalysisResultEntity r WHERE r.contentType = :contentType")
    long countByContentType(@Param("contentType") String contentType);
    
    // 统计成功完成的分析结果数量
    @Query("SELECT COUNT(r) FROM MultimodalAnalysisResultEntity r WHERE r.analysisStatus = 'completed'")
    long countCompleted();
    
    // 统计失败的分析结果数量
    @Query("SELECT COUNT(r) FROM MultimodalAnalysisResultEntity r WHERE r.analysisStatus = 'failed'")
    long countFailed();
    
    // 统计正在处理的分析结果数量
    @Query("SELECT COUNT(r) FROM MultimodalAnalysisResultEntity r WHERE r.analysisStatus = 'processing'")
    long countProcessing();
    
    // 统计待处理的分析结果数量
    @Query("SELECT COUNT(r) FROM MultimodalAnalysisResultEntity r WHERE r.analysisStatus = 'pending'")
    long countPending();
    
    // 计算平均处理时间
    @Query("SELECT AVG(r.processingTimeMs) FROM MultimodalAnalysisResultEntity r WHERE r.processingTimeMs IS NOT NULL")
    Double averageProcessingTime();
    
    // 计算平均置信度
    @Query("SELECT AVG(r.confidenceScore) FROM MultimodalAnalysisResultEntity r WHERE r.confidenceScore IS NOT NULL")
    Double averageConfidenceScore();
    
    // 计算平均情感分数
    @Query("SELECT AVG(r.multimodalSentimentScore) FROM MultimodalAnalysisResultEntity r WHERE r.multimodalSentimentScore IS NOT NULL")
    Double averageSentimentScore();
    
    // 计算总成本
    @Query("SELECT SUM(r.costUnits) FROM MultimodalAnalysisResultEntity r WHERE r.costUnits IS NOT NULL")
    Double totalCost();
    
    // 查找最近完成的成功结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.analysisStatus = 'completed' ORDER BY r.completedAt DESC")
    List<MultimodalAnalysisResultEntity> findRecentSuccessfulResults();
    
    // 查找最近失败的结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.analysisStatus = 'failed' ORDER BY r.updatedAt DESC")
    List<MultimodalAnalysisResultEntity> findRecentFailedResults();
    
    // 根据创建时间范围查找结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.createdAt BETWEEN :start AND :end")
    List<MultimodalAnalysisResultEntity> findByCreatedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // 根据完成时间范围查找结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.completedAt BETWEEN :start AND :end")
    List<MultimodalAnalysisResultEntity> findByCompletedAtBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // 分页查询结果
    Page<MultimodalAnalysisResultEntity> findAll(Pageable pageable);
    
    // 根据用户ID分页查询
    Page<MultimodalAnalysisResultEntity> findByUserId(Long userId, Pageable pageable);
    
    // 根据内容类型分页查询
    Page<MultimodalAnalysisResultEntity> findByContentType(String contentType, Pageable pageable);
    
    // 根据分析状态分页查询
    Page<MultimodalAnalysisResultEntity> findByAnalysisStatus(String analysisStatus, Pageable pageable);
    
    // 查找未完成的分析结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.analysisStatus IN ('pending', 'processing') ORDER BY r.priority ASC, r.createdAt ASC")
    List<MultimodalAnalysisResultEntity> findIncompleteResults();
    
    // 查找需要重试的失败结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.analysisStatus = 'failed' AND r.retryCount < 3")
    List<MultimodalAnalysisResultEntity> findResultsNeedingRetry();
    
    // 根据业务上下文查找结果
    @Query("SELECT r FROM MultimodalAnalysisResultEntity r WHERE r.businessContext LIKE %:context%")
    List<MultimodalAnalysisResultEntity> findByBusinessContext(@Param("context") String context);
    
    // 查找重复内容分析结果
    @Query("SELECT r1 FROM MultimodalAnalysisResultEntity r1 WHERE EXISTS (SELECT 1 FROM MultimodalAnalysisResultEntity r2 WHERE r2.contentHash = r1.contentHash AND r2.id <> r1.id)")
    List<MultimodalAnalysisResultEntity> findDuplicateContentResults();
    
    // 统计情感分布
    @Query("SELECT r.textSentiment, COUNT(r) FROM MultimodalAnalysisResultEntity r WHERE r.textSentiment IS NOT NULL GROUP BY r.textSentiment")
    List<Object[]> countByTextSentiment();
    
    // 统计情感分布
    @Query("SELECT r.audioEmotion, COUNT(r) FROM MultimodalAnalysisResultEntity r WHERE r.audioEmotion IS NOT NULL GROUP BY r.audioEmotion")
    List<Object[]> countByAudioEmotion();
    
    // 统计场景分布
    @Query("SELECT r.imageScene, COUNT(r) FROM MultimodalAnalysisResultEntity r WHERE r.imageScene IS NOT NULL GROUP BY r.imageScene")
    List<Object[]> countByImageScene();
    
    // 统计意图分布
    @Query("SELECT r.textIntent, COUNT(r) FROM MultimodalAnalysisResultEntity r WHERE r.textIntent IS NOT NULL GROUP BY r.textIntent")
    List<Object[]> countByTextIntent();
}