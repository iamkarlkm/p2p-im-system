package com.im.backend.repository;

import com.im.backend.entity.ContentClassificationResultEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 内容分类结果仓储接口
 * 存储分类结果、置信度评分和演进追踪信息
 */
@Repository
public interface ContentClassificationResultRepository extends JpaRepository<ContentClassificationResultEntity, Long> {
    
    // 按配置ID查询
    List<ContentClassificationResultEntity> findByClassificationConfigId(Long classificationConfigId);
    
    // 按配置ID和版本查询
    List<ContentClassificationResultEntity> findByClassificationConfigIdAndClassificationVersion(Long classificationConfigId, Integer classificationVersion);
    
    // 按内容ID查询
    List<ContentClassificationResultEntity> findByContentId(Long contentId);
    
    // 按内容类型查询
    List<ContentClassificationResultEntity> findByContentType(ContentClassificationResultEntity.ContentType contentType);
    
    // 按用户ID查询
    List<ContentClassificationResultEntity> findByUserId(Long userId);
    
    // 按会话ID查询
    List<ContentClassificationResultEntity> findBySessionId(Long sessionId);
    
    // 按主要类别查询
    List<ContentClassificationResultEntity> findByPrimaryCategory(String primaryCategory);
    
    // 按置信度分数查询（高于阈值）
    List<ContentClassificationResultEntity> findByConfidenceScoreGreaterThanEqual(Integer minConfidenceScore);
    
    // 按置信度分数查询（低于阈值）
    List<ContentClassificationResultEntity> findByConfidenceScoreLessThan(Integer maxConfidenceScore);
    
    // 查询上下文感知的分类结果
    List<ContentClassificationResultEntity> findByIsContextAwareTrue();
    
    // 查询多模态分类结果
    List<ContentClassificationResultEntity> findByIsMultiModalTrue();
    
    // 查询自动标签推荐的分类结果
    List<ContentClassificationResultEntity> findByIsAutoLabelRecommendedTrue();
    
    // 查询有用户反馈的分类结果
    List<ContentClassificationResultEntity> findByHasUserFeedbackTrue();
    
    // 查询隐私保护的分类结果
    List<ContentClassificationResultEntity> findByIsPrivacyProtectedTrue();
    
    // 查询演进追踪的分类结果
    List<ContentClassificationResultEntity> findByIsEvolutionTrackedTrue();
    
    // 查询训练示例
    List<ContentClassificationResultEntity> findByIsTrainingExampleTrue();
    
    // 查询异常检测的分类结果
    List<ContentClassificationResultEntity> findByIsAnomalyDetectedTrue();
    
    // 按语言代码查询
    List<ContentClassificationResultEntity> findByLanguageCode(String languageCode);
    
    // 按内容语言查询
    List<ContentClassificationResultEntity> findByContentLanguage(String contentLanguage);
    
    // 按内容创建时间范围查询
    List<ContentClassificationResultEntity> findByContentCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 按创建时间范围查询
    List<ContentClassificationResultEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 按更新时间范围查询
    List<ContentClassificationResultEntity> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 按准确率贡献查询（高于阈值）
    @Query("SELECT r FROM ContentClassificationResultEntity r WHERE r.accuracyContribution >= :minContribution")
    List<ContentClassificationResultEntity> findByAccuracyContributionGreaterThanEqual(@Param("minContribution") Double minContribution);
    
    // 按配置ID和置信度范围查询
    @Query("SELECT r FROM ContentClassificationResultEntity r WHERE r.classificationConfigId = :configId AND r.confidenceScore BETWEEN :minScore AND :maxScore")
    List<ContentClassificationResultEntity> findByClassificationConfigIdAndConfidenceScoreRange(
            @Param("configId") Long configId,
            @Param("minScore") Integer minScore,
            @Param("maxScore") Integer maxScore);
    
    // 统计按配置的分类结果数量
    @Query("SELECT r.classificationConfigId, COUNT(r) FROM ContentClassificationResultEntity r GROUP BY r.classificationConfigId")
    List<Object[]> countByClassificationConfigGroup();
    
    // 统计按内容类型的分类结果数量
    @Query("SELECT r.contentType, COUNT(r) FROM ContentClassificationResultEntity r GROUP BY r.contentType")
    List<Object[]> countByContentTypeGroup();
    
    // 统计按主要类别的分类结果数量
    @Query("SELECT r.primaryCategory, COUNT(r) FROM ContentClassificationResultEntity r GROUP BY r.primaryCategory")
    List<Object[]> countByPrimaryCategoryGroup();
    
    // 统计按语言的分类结果数量
    @Query("SELECT r.languageCode, COUNT(r) FROM ContentClassificationResultEntity r GROUP BY r.languageCode")
    List<Object[]> countByLanguageCodeGroup();
    
    // 统计平均置信度分数
    @Query("SELECT AVG(r.confidenceScore) FROM ContentClassificationResultEntity r")
    Double averageConfidenceScore();
    
    // 统计按配置的平均置信度分数
    @Query("SELECT r.classificationConfigId, AVG(r.confidenceScore) FROM ContentClassificationResultEntity r GROUP BY r.classificationConfigId")
    List<Object[]> averageConfidenceScoreByConfig();
    
    // 统计按用户的平均置信度分数
    @Query("SELECT r.userId, AVG(r.confidenceScore) FROM ContentClassificationResultEntity r GROUP BY r.userId")
    List<Object[]> averageConfidenceScoreByUser();
    
    // 统计高置信度分类结果比例
    @Query("SELECT COUNT(r) * 100.0 / (SELECT COUNT(r2) FROM ContentClassificationResultEntity r2) FROM ContentClassificationResultEntity r WHERE r.confidenceScore >= 80")
    Double highConfidencePercentage();
    
    // 统计低置信度分类结果比例
    @Query("SELECT COUNT(r) * 100.0 / (SELECT COUNT(r2) FROM ContentClassificationResultEntity r2) FROM ContentClassificationResultEntity r WHERE r.confidenceScore < 60")
    Double lowConfidencePercentage();
    
    // 统计上下文感知分类结果比例
    @Query("SELECT COUNT(r) * 100.0 / (SELECT COUNT(r2) FROM ContentClassificationResultEntity r2) FROM ContentClassificationResultEntity r WHERE r.isContextAware = true")
    Double contextAwarePercentage();
    
    // 统计多模态分类结果比例
    @Query("SELECT COUNT(r) * 100.0 / (SELECT COUNT(r2) FROM ContentClassificationResultEntity r2) FROM ContentClassificationResultEntity r WHERE r.isMultiModal = true")
    Double multiModalPercentage();
    
    // 按主要类别搜索
    @Query("SELECT r FROM ContentClassificationResultEntity r WHERE LOWER(r.primaryCategory) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<ContentClassificationResultEntity> searchByPrimaryCategory(@Param("keyword") String keyword);
    
    // 复杂搜索：配置+内容类型+置信度范围
    @Query("SELECT r FROM ContentClassificationResultEntity r WHERE " +
           "(r.classificationConfigId = :configId OR :configId IS NULL) AND " +
           "(r.contentType = :contentType OR :contentType IS NULL) AND " +
           "(r.confidenceScore BETWEEN :minConfidence AND :maxConfidence) AND " +
           "(LOWER(r.primaryCategory) LIKE LOWER(CONCAT('%', :categoryKeyword, '%')) OR :categoryKeyword IS NULL)")
    List<ContentClassificationResultEntity> searchByMultipleCriteria(
            @Param("configId") Long configId,
            @Param("contentType") ContentClassificationResultEntity.ContentType contentType,
            @Param("minConfidence") Integer minConfidence,
            @Param("maxConfidence") Integer maxConfidence,
            @Param("categoryKeyword") String categoryKeyword);
    
    // 分页查询按配置
    Page<ContentClassificationResultEntity> findByClassificationConfigId(Long classificationConfigId, Pageable pageable);
    
    // 分页查询按用户
    Page<ContentClassificationResultEntity> findByUserId(Long userId, Pageable pageable);
    
    // 分页查询按内容类型
    Page<ContentClassificationResultEntity> findByContentType(ContentClassificationResultEntity.ContentType contentType, Pageable pageable);
    
    // 分页查询按主要类别
    Page<ContentClassificationResultEntity> findByPrimaryCategory(String primaryCategory, Pageable pageable);
    
    // 查找最新分类结果
    @Query("SELECT r FROM ContentClassificationResultEntity r WHERE r.classificationConfigId = :configId ORDER BY r.createdAt DESC")
    List<ContentClassificationResultEntity> findLatestByClassificationConfigId(@Param("configId") Long configId, Pageable pageable);
    
    // 查找最高置信度分类结果
    @Query("SELECT r FROM ContentClassificationResultEntity r WHERE r.classificationConfigId = :configId ORDER BY r.confidenceScore DESC")
    List<ContentClassificationResultEntity> findHighestConfidenceByClassificationConfigId(@Param("configId") Long configId, Pageable pageable);
    
    // 查找最低置信度分类结果
    @Query("SELECT r FROM ContentClassificationResultEntity r WHERE r.classificationConfigId = :configId ORDER BY r.confidenceScore ASC")
    List<ContentClassificationResultEntity> findLowestConfidenceByClassificationConfigId(@Param("configId") Long configId, Pageable pageable);
    
    // 查找最有价值的训练示例（高准确率贡献）
    @Query("SELECT r FROM ContentClassificationResultEntity r WHERE r.isTrainingExample = true ORDER BY r.accuracyContribution DESC")
    List<ContentClassificationResultEntity> findMostValuableTrainingExamples(Pageable pageable);
    
    // 查找异常分类结果
    @Query("SELECT r FROM ContentClassificationResultEntity r WHERE r.isAnomalyDetected = true ORDER BY r.createdAt DESC")
    List<ContentClassificationResultEntity> findAnomalyResults(Pageable pageable);
    
    // 统计每日分类数量
    @Query("SELECT DATE(r.createdAt) as date, COUNT(r) as count FROM ContentClassificationResultEntity r " +
           "WHERE r.createdAt >= :startDate GROUP BY DATE(r.createdAt) ORDER BY date DESC")
    List<Object[]> countDailyClassifications(@Param("startDate") LocalDateTime startDate);
    
    // 统计按小时的分类数量
    @Query("SELECT HOUR(r.createdAt) as hour, COUNT(r) as count FROM ContentClassificationResultEntity r " +
           "WHERE r.createdAt >= :startDate GROUP BY HOUR(r.createdAt) ORDER BY hour")
    List<Object[]> countHourlyClassifications(@Param("startDate") LocalDateTime startDate);
    
    // 统计用户分类活跃度
    @Query("SELECT r.userId, COUNT(r) as classificationCount, AVG(r.confidenceScore) as avgConfidence, MAX(r.createdAt) as lastClassification " +
           "FROM ContentClassificationResultEntity r GROUP BY r.userId ORDER BY classificationCount DESC")
    List<Object[]> findUserClassificationActivity();
    
    // 统计类别分布趋势
    @Query("SELECT DATE(r.createdAt) as date, r.primaryCategory, COUNT(r) as count FROM ContentClassificationResultEntity r " +
           "WHERE r.createdAt >= :startDate GROUP BY DATE(r.createdAt), r.primaryCategory ORDER BY date DESC, count DESC")
    List<Object[]> findCategoryTrend(@Param("startDate") LocalDateTime startDate);
    
    // 批量更新置信度分数
    @Query("UPDATE ContentClassificationResultEntity r SET r.confidenceScore = :newScore, r.updatedAt = CURRENT_TIMESTAMP WHERE r.id IN :ids")
    int updateConfidenceScoreByIds(@Param("ids") List<Long> ids, @Param("newScore") Integer newScore);
    
    // 批量标记为训练示例
    @Query("UPDATE ContentClassificationResultEntity r SET r.isTrainingExample = true, r.updatedAt = CURRENT_TIMESTAMP WHERE r.id IN :ids")
    int markAsTrainingExamplesByIds(@Param("ids") List<Long> ids);
    
    // 批量标记异常
    @Query("UPDATE ContentClassificationResultEntity r SET r.isAnomalyDetected = true, r.anomalyDetails = :anomalyDetails, r.updatedAt = CURRENT_TIMESTAMP WHERE r.id IN :ids")
    int markAsAnomaliesByIds(@Param("ids") List<Long> ids, @Param("anomalyDetails") String anomalyDetails);
    
    // 清理旧分类结果（保留最新N条）
    @Query("DELETE FROM ContentClassificationResultEntity r WHERE r.classificationConfigId = :configId AND r.id NOT IN " +
           "(SELECT r2.id FROM ContentClassificationResultEntity r2 WHERE r2.classificationConfigId = :configId ORDER BY r2.createdAt DESC LIMIT :keepCount)")
    int cleanupOldResults(@Param("configId") Long configId, @Param("keepCount") Long keepCount);
}