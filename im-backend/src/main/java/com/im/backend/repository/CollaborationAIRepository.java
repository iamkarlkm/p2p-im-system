package com.im.backend.repository;

import com.im.backend.entity.CollaborationAIEntity;
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
 * 协作增强 AI 助手仓储接口
 * 提供协作 AI 配置的 CRUD 操作和复杂查询
 */
@Repository
public interface CollaborationAIRepository extends JpaRepository<CollaborationAIEntity, Long> {

    /**
     * 根据会话ID查找协作 AI 配置
     */
    Optional<CollaborationAIEntity> findBySessionId(String sessionId);

    /**
     * 根据用户ID查找用户的协作 AI 配置列表
     */
    List<CollaborationAIEntity> findByUserId(String userId);

    /**
     * 根据群组ID查找群组的协作 AI 配置列表
     */
    List<CollaborationAIEntity> findByGroupId(String groupId);

    /**
     * 根据用户ID和协作类型查找配置
     */
    List<CollaborationAIEntity> findByUserIdAndCollaborationType(String userId, CollaborationAIEntity.CollaborationType collaborationType);

    /**
     * 查找所有启用的协作 AI 配置
     */
    List<CollaborationAIEntity> findByEnabledTrue();

    /**
     * 查找需要进行分析的协作 AI 配置（下次分析时间已到）
     */
    @Query("SELECT c FROM CollaborationAIEntity c WHERE c.enabled = true AND c.nextAnalysisAt <= :currentTime")
    List<CollaborationAIEntity> findForAnalysis(@Param("currentTime") LocalDateTime currentTime);

    /**
     * 根据 AI 置信度范围查找配置
     */
    @Query("SELECT c FROM CollaborationAIEntity c WHERE c.enabled = true AND c.aiConfidence >= :minConfidence AND c.aiConfidence <= :maxConfidence")
    List<CollaborationAIEntity> findByConfidenceRange(@Param("minConfidence") Integer minConfidence, @Param("maxConfidence") Integer maxConfidence);

    /**
     * 统计用户的协作 AI 配置数量
     */
    @Query("SELECT COUNT(c) FROM CollaborationAIEntity c WHERE c.userId = :userId")
    long countByUserId(@Param("userId") String userId);

    /**
     * 统计群组的协作 AI 配置数量
     */
    @Query("SELECT COUNT(c) FROM CollaborationAIEntity c WHERE c.groupId = :groupId")
    long countByGroupId(@Param("groupId") String groupId);

    /**
     * 统计各种协作类型的配置数量
     */
    @Query("SELECT c.collaborationType, COUNT(c) FROM CollaborationAIEntity c WHERE c.enabled = true GROUP BY c.collaborationType")
    List<Object[]> countByCollaborationType();

    /**
     * 按创建时间降序排列的协作 AI 配置分页列表
     */
    Page<CollaborationAIEntity> findByEnabledTrueOrderByCreatedAtDesc(Pageable pageable);

    /**
     * 按更新时间降序排列的协作 AI 配置分页列表
     */
    Page<CollaborationAIEntity> findByEnabledTrueOrderByUpdatedAtDesc(Pageable pageable);

    /**
     * 按 AI 置信度降序排列的协作 AI 配置分页列表
     */
    Page<CollaborationAIEntity> findByEnabledTrueOrderByAiConfidenceDesc(Pageable pageable);

    /**
     * 搜索包含特定关键词的会议纪要
     */
    @Query("SELECT c FROM CollaborationAIEntity c WHERE c.enabled = true AND c.meetingMinutes LIKE %:keyword%")
    List<CollaborationAIEntity> searchMeetingMinutes(@Param("keyword") String keyword);

    /**
     * 搜索包含特定关键词的项目进度
     */
    @Query("SELECT c FROM CollaborationAIEntity c WHERE c.enabled = true AND c.projectProgress LIKE %:keyword%")
    List<CollaborationAIEntity> searchProjectProgress(@Param("keyword") String keyword);

    /**
     * 搜索包含特定关键词的任务分配
     */
    @Query("SELECT c FROM CollaborationAIEntity c WHERE c.enabled = true AND c.taskAssignments LIKE %:keyword%")
    List<CollaborationAIEntity> searchTaskAssignments(@Param("keyword") String keyword);

    /**
     * 批量启用或禁用协作 AI 配置
     */
    @Query("UPDATE CollaborationAIEntity c SET c.enabled = :enabled WHERE c.id IN :ids")
    int batchUpdateEnabled(@Param("ids") List<Long> ids, @Param("enabled") Boolean enabled);

    /**
     * 批量更新 AI 置信度
     */
    @Query("UPDATE CollaborationAIEntity c SET c.aiConfidence = :confidence, c.updatedAt = CURRENT_TIMESTAMP WHERE c.id IN :ids")
    int batchUpdateConfidence(@Param("ids") List<Long> ids, @Param("confidence") Integer confidence);

    /**
     * 查找最近更新的协作 AI 配置
     */
    @Query("SELECT c FROM CollaborationAIEntity c WHERE c.enabled = true AND c.updatedAt >= :since ORDER BY c.updatedAt DESC")
    List<CollaborationAIEntity> findRecentlyUpdated(@Param("since") LocalDateTime since);

    /**
     * 查找需要生成效率报告的配置
     */
    @Query("SELECT c FROM CollaborationAIEntity c WHERE c.enabled = true AND c.generateReport = true AND (c.lastAnalysisAt IS NULL OR c.lastAnalysisAt <= :threshold)")
    List<CollaborationAIEntity> findForReportGeneration(@Param("threshold") LocalDateTime threshold);

    /**
     * 统计各分析频率的配置数量
     */
    @Query("SELECT c.analysisFrequency, COUNT(c) FROM CollaborationAIEntity c WHERE c.enabled = true GROUP BY c.analysisFrequency ORDER BY c.analysisFrequency")
    List<Object[]> countByAnalysisFrequency();

    /**
     * 查找性能指标超过阈值的配置
     */
    @Query("SELECT c FROM CollaborationAIEntity c WHERE c.enabled = true AND JSON_EXTRACT(c.performanceMetrics, :metricPath) > :threshold")
    List<CollaborationAIEntity> findByPerformanceMetricThreshold(@Param("metricPath") String metricPath, @Param("threshold") Double threshold);

    /**
     * 根据协作类型和启用状态统计
     */
    @Query("SELECT c.collaborationType, COUNT(c) FROM CollaborationAIEntity c WHERE c.enabled = :enabled GROUP BY c.collaborationType")
    List<Object[]> countByCollaborationTypeAndEnabled(@Param("enabled") Boolean enabled);

    /**
     * 批量删除过期的协作 AI 配置
     */
    @Query("DELETE FROM CollaborationAIEntity c WHERE c.enabled = false AND c.updatedAt < :expiryDate")
    int deleteExpiredConfigurations(@Param("expiryDate") LocalDateTime expiryDate);

    /**
     * 查找有洞察但无推荐的配置
     */
    @Query("SELECT c FROM CollaborationAIEntity c WHERE c.enabled = true AND c.insights IS NOT EMPTY AND (c.recommendations IS EMPTY OR SIZE(c.recommendations) = 0)")
    List<CollaborationAIEntity> findWithInsightsButNoRecommendations();

    /**
     * 查找有瓶颈但无建议的配置
     */
    @Query("SELECT c FROM CollaborationAIEntity c WHERE c.enabled = true AND c.bottleneckAnalysis IS NOT NULL AND c.bottleneckAnalysis != '' AND (c.realtimeSuggestions IS NULL OR c.realtimeSuggestions = '')")
    List<CollaborationAIEntity> findWithBottlenecksButNoSuggestions();

    /**
     * 按协作类型和用户ID查找
     */
    List<CollaborationAIEntity> findByCollaborationTypeAndUserId(CollaborationAIEntity.CollaborationType collaborationType, String userId);

    /**
     * 按协作类型和群组ID查找
     */
    List<CollaborationAIEntity> findByCollaborationTypeAndGroupId(CollaborationAIEntity.CollaborationType collaborationType, String groupId);

    /**
     * 查找所有自动生成会议纪要的配置
     */
    List<CollaborationAIEntity> findByAutoGenerateMinutesTrueAndEnabledTrue();

    /**
     * 查找所有跟踪项目进度的配置
     */
    List<CollaborationAIEntity> findByTrackProgressTrueAndEnabledTrue();

    /**
     * 查找所有识别任务的配置
     */
    List<CollaborationAIEntity> findByIdentifyTasksTrueAndEnabledTrue();

    /**
     * 查找所有分析协作模式的配置
     */
    List<CollaborationAIEntity> findByAnalyzePatternsTrueAndEnabledTrue();

    /**
     * 查找所有提供实时建议的配置
     */
    List<CollaborationAIEntity> findByProvideSuggestionsTrueAndEnabledTrue();

    /**
     * 查找所有生成效率报告的配置
     */
    List<CollaborationAIEntity> findByGenerateReportTrueAndEnabledTrue();

    /**
     * 查找所有构建团队知识的配置
     */
    List<CollaborationAIEntity> findByBuildKnowledgeTrueAndEnabledTrue();

    /**
     * 查找所有识别瓶颈的配置
     */
    List<CollaborationAIEntity> findByIdentifyBottlenecksTrueAndEnabledTrue();

    /**
     * 查找所有优化角色的配置
     */
    List<CollaborationAIEntity> findByOptimizeRolesTrueAndEnabledTrue();

    /**
     * 查找所有评估会议质量的配置
     */
    List<CollaborationAIEntity> findByAssessMeetingsTrueAndEnabledTrue();
}