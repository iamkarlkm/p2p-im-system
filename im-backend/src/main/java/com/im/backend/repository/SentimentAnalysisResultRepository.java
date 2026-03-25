package com.im.backend.repository;

import com.im.backend.entity.SentimentAnalysisResultEntity;
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
 * 情感分析结果仓储接口
 * 提供基于深度学习的实时情感分析数据访问
 */
@Repository
public interface SentimentAnalysisResultRepository extends JpaRepository<SentimentAnalysisResultEntity, Long> {
    
    // 按消息ID查找
    Optional<SentimentAnalysisResultEntity> findByMessageId(Long messageId);
    
    // 按会话ID查找，支持分页
    Page<SentimentAnalysisResultEntity> findByConversationId(Long conversationId, Pageable pageable);
    
    // 按发送者ID查找
    List<SentimentAnalysisResultEntity> findBySenderId(Long senderId);
    
    // 按发送者ID和会话ID查找
    List<SentimentAnalysisResultEntity> findBySenderIdAndConversationId(Long senderId, Long conversationId);
    
    // 按主要情感类型查找
    List<SentimentAnalysisResultEntity> findByPrimaryEmotion(String primaryEmotion);
    
    // 按情感强度范围查找
    List<SentimentAnalysisResultEntity> findBySentimentIntensityBetween(Double minIntensity, Double maxIntensity);
    
    // 按紧急情绪标记查找
    List<SentimentAnalysisResultEntity> findByEmergencyFlagTrue();
    
    // 按模型版本查找
    List<SentimentAnalysisResultEntity> findByModelVersion(String modelVersion);
    
    // 按分析时间范围查找
    List<SentimentAnalysisResultEntity> findByAnalysisTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    // 按置信度阈值查找
    List<SentimentAnalysisResultEntity> findByConfidenceScoreGreaterThanEqual(Double minConfidence);
    
    // 按离线预测标记查找
    List<SentimentAnalysisResultEntity> findByOfflinePrediction(Boolean offlinePrediction);
    
    // 按多模态融合分数查找
    List<SentimentAnalysisResultEntity> findByMultimodalFusionScoreGreaterThan(Double threshold);
    
    // 复杂查询：查找特定时间段内特定情感类型的消息
    @Query("SELECT s FROM SentimentAnalysisResultEntity s WHERE s.primaryEmotion = :emotion AND s.analysisTime BETWEEN :start AND :end")
    List<SentimentAnalysisResultEntity> findByEmotionAndTimeRange(@Param("emotion") String emotion, 
                                                                  @Param("start") LocalDateTime start, 
                                                                  @Param("end") LocalDateTime end);
    
    // 复杂查询：查找情感强度异常的消息
    @Query("SELECT s FROM SentimentAnalysisResultEntity s WHERE s.sentimentIntensity > :intensityThreshold AND s.confidenceScore >= :confidenceThreshold")
    List<SentimentAnalysisResultEntity> findHighIntensityHighConfidence(@Param("intensityThreshold") Double intensityThreshold, 
                                                                        @Param("confidenceThreshold") Double confidenceThreshold);
    
    // 统计查询：按情感类型分组统计
    @Query("SELECT s.primaryEmotion, COUNT(s) FROM SentimentAnalysisResultEntity s GROUP BY s.primaryEmotion")
    List<Object[]> countByEmotionType();
    
    // 统计查询：按时间段统计情感趋势
    @Query("SELECT DATE(s.analysisTime), s.primaryEmotion, COUNT(s), AVG(s.sentimentIntensity) FROM SentimentAnalysisResultEntity s WHERE s.analysisTime BETWEEN :start AND :end GROUP BY DATE(s.analysisTime), s.primaryEmotion ORDER BY DATE(s.analysisTime)")
    List<Object[]> findEmotionTrendByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // 统计查询：计算用户情感基线
    @Query("SELECT s.senderId, AVG(s.sentimentIntensity), STDDEV(s.sentimentIntensity), COUNT(s) FROM SentimentAnalysisResultEntity s WHERE s.senderId = :senderId AND s.analysisTime BETWEEN :start AND :end GROUP BY s.senderId")
    List<Object[]> calculateUserBaseline(@Param("senderId") Long senderId, 
                                         @Param("start") LocalDateTime start, 
                                         @Param("end") LocalDateTime end);
    
    // 统计查询：查找情感异常的用户
    @Query("SELECT s.senderId, AVG(s.sentimentIntensity), COUNT(s) FROM SentimentAnalysisResultEntity s WHERE s.analysisTime >= :recentTime GROUP BY s.senderId HAVING AVG(s.sentimentIntensity) < :lowThreshold OR AVG(s.sentimentIntensity) > :highThreshold")
    List<Object[]> findEmotionalAnomalyUsers(@Param("recentTime") LocalDateTime recentTime, 
                                             @Param("lowThreshold") Double lowThreshold, 
                                             @Param("highThreshold") Double highThreshold);
    
    // 批量操作：按消息ID列表查找
    @Query("SELECT s FROM SentimentAnalysisResultEntity s WHERE s.messageId IN :messageIds")
    List<SentimentAnalysisResultEntity> findByMessageIds(@Param("messageIds") List<Long> messageIds);
    
    // 批量操作：按会话ID列表查找
    @Query("SELECT s FROM SentimentAnalysisResultEntity s WHERE s.conversationId IN :conversationIds")
    List<SentimentAnalysisResultEntity> findByConversationIds(@Param("conversationIds") List<Long> conversationIds);
    
    // 清理操作：删除指定时间前的记录
    int deleteByAnalysisTimeBefore(LocalDateTime cutoffTime);
    
    // 清理操作：删除特定会话的记录
    int deleteByConversationId(Long conversationId);
    
    // 清理操作：删除特定用户的记录
    int deleteBySenderId(Long senderId);
    
    // 高级搜索：复合条件查询
    @Query("SELECT s FROM SentimentAnalysisResultEntity s WHERE " +
           "(:conversationId IS NULL OR s.conversationId = :conversationId) AND " +
           "(:senderId IS NULL OR s.senderId = :senderId) AND " +
           "(:primaryEmotion IS NULL OR s.primaryEmotion = :primaryEmotion) AND " +
           "(:emergencyFlag IS NULL OR s.emergencyFlag = :emergencyFlag) AND " +
           "s.analysisTime BETWEEN :startTime AND :endTime")
    Page<SentimentAnalysisResultEntity> advancedSearch(@Param("conversationId") Long conversationId,
                                                       @Param("senderId") Long senderId,
                                                       @Param("primaryEmotion") String primaryEmotion,
                                                       @Param("emergencyFlag") Boolean emergencyFlag,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime,
                                                       Pageable pageable);
    
    // 统计：计算整体情感分布
    @Query("SELECT COUNT(s), AVG(s.sentimentIntensity), MIN(s.sentimentIntensity), MAX(s.sentimentIntensity) FROM SentimentAnalysisResultEntity s WHERE s.analysisTime BETWEEN :start AND :end")
    Object[] getOverallSentimentStatistics(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // 统计：按模型版本统计性能
    @Query("SELECT s.modelVersion, COUNT(s), AVG(s.processingLatencyMs), AVG(s.confidenceScore) FROM SentimentAnalysisResultEntity s GROUP BY s.modelVersion")
    List<Object[]> getModelPerformanceStatistics();
    
    // 统计：紧急情绪统计
    @Query("SELECT s.emergencyReason, COUNT(s) FROM SentimentAnalysisResultEntity s WHERE s.emergencyFlag = true GROUP BY s.emergencyReason")
    List<Object[]> getEmergencyStatistics();
    
    // 导出：获取批量数据用于导出
    @Query("SELECT s FROM SentimentAnalysisResultEntity s WHERE s.analysisTime BETWEEN :start AND :end ORDER BY s.analysisTime DESC")
    List<SentimentAnalysisResultEntity> findForExport(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}