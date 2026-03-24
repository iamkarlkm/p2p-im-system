package com.im.backend.repository;

import com.im.backend.entity.AdaptiveContentClassificationConfigEntity;
import com.im.backend.entity.ContentClassificationResultEntity;
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
 * 自适应内容分类配置仓储接口
 * 支持自定义分类体系、增量学习和多模态内容分类
 */
@Repository
public interface AdaptiveContentClassificationConfigRepository extends JpaRepository<AdaptiveContentClassificationConfigEntity, Long> {
    
    // 按用户查询
    List<AdaptiveContentClassificationConfigEntity> findByUserId(Long userId);
    
    // 按用户和状态查询
    List<AdaptiveContentClassificationConfigEntity> findByUserIdAndStatus(Long userId, AdaptiveContentClassificationConfigEntity.ClassificationStatus status);
    
    // 按会话查询
    List<AdaptiveContentClassificationConfigEntity> findBySessionId(Long sessionId);
    
    // 按分类类型查询
    List<AdaptiveContentClassificationConfigEntity> findByClassificationType(AdaptiveContentClassificationConfigEntity.ClassificationType classificationType);
    
    // 按内容模态查询
    List<AdaptiveContentClassificationConfigEntity> findByContentModality(AdaptiveContentClassificationConfigEntity.ContentModality contentModality);
    
    // 按隐私级别查询
    List<AdaptiveContentClassificationConfigEntity> findByPrivacyLevel(AdaptiveContentClassificationConfigEntity.ClassificationPrivacyLevel privacyLevel);
    
    // 按状态查询
    List<AdaptiveContentClassificationConfigEntity> findByStatus(AdaptiveContentClassificationConfigEntity.ClassificationStatus status);
    
    // 按版本查询
    List<AdaptiveContentClassificationConfigEntity> findByVersion(Integer version);
    
    // 查询启用增量学习的配置
    List<AdaptiveContentClassificationConfigEntity> findByEnableIncrementalLearningTrue();
    
    // 查询启用上下文感知的配置
    List<AdaptiveContentClassificationConfigEntity> findByEnableContextAwarenessTrue();
    
    // 查询启用多语言支持的配置
    List<AdaptiveContentClassificationConfigEntity> findByEnableMultiLanguageTrue();
    
    // 查询启用自动标签推荐的配置
    List<AdaptiveContentClassificationConfigEntity> findByEnableAutoLabelRecommendationTrue();
    
    // 查询启用隐私保护的配置
    List<AdaptiveContentClassificationConfigEntity> findByEnablePrivacyProtectionTrue();
    
    // 查询启用演进追踪的配置
    List<AdaptiveContentClassificationConfigEntity> findByEnableEvolutionTrackingTrue();
    
    // 按准确率查询（高于阈值）
    @Query("SELECT c FROM AdaptiveContentClassificationConfigEntity c WHERE c.accuracyScore >= :minAccuracy")
    List<AdaptiveContentClassificationConfigEntity> findByAccuracyScoreGreaterThanEqual(@Param("minAccuracy") Double minAccuracy);
    
    // 按分类数量查询（大于阈值）
    @Query("SELECT c FROM AdaptiveContentClassificationConfigEntity c WHERE c.totalClassifications >= :minClassifications")
    List<AdaptiveContentClassificationConfigEntity> findByTotalClassificationsGreaterThanEqual(@Param("minClassifications") Integer minClassifications);
    
    // 按创建时间范围查询
    List<AdaptiveContentClassificationConfigEntity> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 按更新时间范围查询
    List<AdaptiveContentClassificationConfigEntity> findByUpdatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    // 统计用户配置数量
    @Query("SELECT COUNT(c) FROM AdaptiveContentClassificationConfigEntity c WHERE c.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    // 统计按状态的配置数量
    @Query("SELECT c.status, COUNT(c) FROM AdaptiveContentClassificationConfigEntity c GROUP BY c.status")
    List<Object[]> countByStatusGroup();
    
    // 统计按分类类型的配置数量
    @Query("SELECT c.classificationType, COUNT(c) FROM AdaptiveContentClassificationConfigEntity c GROUP BY c.classificationType")
    List<Object[]> countByClassificationTypeGroup();
    
    // 统计按内容模态的配置数量
    @Query("SELECT c.contentModality, COUNT(c) FROM AdaptiveContentClassificationConfigEntity c GROUP BY c.contentModality")
    List<Object[]> countByContentModalityGroup();
    
    // 统计平均准确率
    @Query("SELECT AVG(c.accuracyScore) FROM AdaptiveContentClassificationConfigEntity c")
    Double averageAccuracyScore();
    
    // 统计用户平均准确率
    @Query("SELECT AVG(c.accuracyScore) FROM AdaptiveContentClassificationConfigEntity c WHERE c.userId = :userId")
    Double averageAccuracyScoreByUserId(@Param("userId") Long userId);
    
    // 按名称模糊搜索
    @Query("SELECT c FROM AdaptiveContentClassificationConfigEntity c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<AdaptiveContentClassificationConfigEntity> searchByName(@Param("keyword") String keyword);
    
    // 按描述模糊搜索
    @Query("SELECT c FROM AdaptiveContentClassificationConfigEntity c WHERE LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<AdaptiveContentClassificationConfigEntity> searchByDescription(@Param("keyword") String keyword);
    
    // 复杂搜索：名称+描述+状态
    @Query("SELECT c FROM AdaptiveContentClassificationConfigEntity c WHERE " +
           "(LOWER(c.name) LIKE LOWER(CONCAT('%', :nameKeyword, '%')) OR :nameKeyword IS NULL) AND " +
           "(LOWER(c.description) LIKE LOWER(CONCAT('%', :descKeyword, '%')) OR :descKeyword IS NULL) AND " +
           "(c.status = :status OR :status IS NULL)")
    List<AdaptiveContentClassificationConfigEntity> searchByMultipleCriteria(
            @Param("nameKeyword") String nameKeyword,
            @Param("descKeyword") String descKeyword,
            @Param("status") AdaptiveContentClassificationConfigEntity.ClassificationStatus status);
    
    // 分页查询用户配置
    Page<AdaptiveContentClassificationConfigEntity> findByUserId(Long userId, Pageable pageable);
    
    // 分页查询按状态
    Page<AdaptiveContentClassificationConfigEntity> findByStatus(AdaptiveContentClassificationConfigEntity.ClassificationStatus status, Pageable pageable);
    
    // 分页查询按分类类型
    Page<AdaptiveContentClassificationConfigEntity> findByClassificationType(AdaptiveContentClassificationConfigEntity.ClassificationType classificationType, Pageable pageable);
    
    // 查找最新版本配置
    @Query("SELECT c FROM AdaptiveContentClassificationConfigEntity c WHERE c.userId = :userId AND c.name = :name ORDER BY c.version DESC")
    List<AdaptiveContentClassificationConfigEntity> findLatestVersionByUserIdAndName(
            @Param("userId") Long userId, 
            @Param("name") String name);
    
    // 检查配置名称是否已存在（同一用户）
    @Query("SELECT COUNT(c) > 0 FROM AdaptiveContentClassificationConfigEntity c WHERE c.userId = :userId AND c.name = :name")
    Boolean existsByUserIdAndName(@Param("userId") Long userId, @Param("name") String name);
    
    // 获取用户最活跃的配置（按分类数量）
    @Query("SELECT c FROM AdaptiveContentClassificationConfigEntity c WHERE c.userId = :userId ORDER BY c.totalClassifications DESC")
    List<AdaptiveContentClassificationConfigEntity> findMostActiveByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 获取最准确的配置（按准确率）
    @Query("SELECT c FROM AdaptiveContentClassificationConfigEntity c WHERE c.userId = :userId AND c.totalClassifications >= 10 ORDER BY c.accuracyScore DESC")
    List<AdaptiveContentClassificationConfigEntity> findMostAccurateByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 查找需要重新训练的配置（准确率低且分类数量多）
    @Query("SELECT c FROM AdaptiveContentClassificationConfigEntity c WHERE " +
           "c.accuracyScore < :maxAccuracy AND " +
           "c.totalClassifications >= :minClassifications AND " +
           "c.status = 'ACTIVE'")
    List<AdaptiveContentClassificationConfigEntity> findConfigsNeedingRetraining(
            @Param("maxAccuracy") Double maxAccuracy,
            @Param("minClassifications") Integer minClassifications);
    
    // 批量更新状态
    @Query("UPDATE AdaptiveContentClassificationConfigEntity c SET c.status = :newStatus WHERE c.id IN :ids")
    int updateStatusByIds(@Param("ids") List<Long> ids, @Param("newStatus") AdaptiveContentClassificationConfigEntity.ClassificationStatus newStatus);
    
    // 批量更新版本
    @Query("UPDATE AdaptiveContentClassificationConfigEntity c SET c.version = c.version + 1, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id IN :ids")
    int incrementVersionByIds(@Param("ids") List<Long> ids);
    
    // 统计每日新增配置
    @Query("SELECT DATE(c.createdAt) as date, COUNT(c) as count FROM AdaptiveContentClassificationConfigEntity c " +
           "WHERE c.createdAt >= :startDate GROUP BY DATE(c.createdAt) ORDER BY date DESC")
    List<Object[]> countDailyNewConfigs(@Param("startDate") LocalDateTime startDate);
    
    // 统计用户活跃度（按更新时间）
    @Query("SELECT c.userId, MAX(c.updatedAt) as lastUpdated, COUNT(c) as configCount FROM AdaptiveContentClassificationConfigEntity c " +
           "GROUP BY c.userId ORDER BY lastUpdated DESC")
    List<Object[]> findUserActivity();
}