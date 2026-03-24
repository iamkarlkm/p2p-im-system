package com.im.backend.repository;

import com.im.backend.entity.ContextAwareReplyEntity;
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
 * 上下文感知智能回复生成器仓储接口
 * 提供数据库访问方法
 */
@Repository
public interface ContextAwareReplyRepository extends JpaRepository<ContextAwareReplyEntity, Long> {
    
    // 基础查询方法
    
    /**
     * 根据用户ID查询回复记录
     */
    List<ContextAwareReplyEntity> findByUserId(String userId);
    
    /**
     * 根据用户ID和状态查询回复记录
     */
    List<ContextAwareReplyEntity> findByUserIdAndStatus(String userId, String status);
    
    /**
     * 根据会话ID查询回复记录
     */
    List<ContextAwareReplyEntity> findBySessionId(String sessionId);
    
    /**
     * 根据用户ID和会话ID查询回复记录
     */
    List<ContextAwareReplyEntity> findByUserIdAndSessionId(String userId, String sessionId);
    
    /**
     * 根据触发消息ID查询回复记录
     */
    Optional<ContextAwareReplyEntity> findByTriggerMessageId(String triggerMessageId);
    
    /**
     * 根据索引键查询回复记录
     */
    List<ContextAwareReplyEntity> findByIndexKey(String indexKey);
    
    /**
     * 根据状态查询回复记录
     */
    List<ContextAwareReplyEntity> findByStatus(String status);
    
    /**
     * 查询已使用的回复记录
     */
    List<ContextAwareReplyEntity> findByUsedTrue();
    
    /**
     * 查询未使用的回复记录
     */
    List<ContextAwareReplyEntity> findByUsedFalse();
    
    /**
     * 查询高质量回复（评分≥4）
     */
    @Query("SELECT r FROM ContextAwareReplyEntity r WHERE r.userFeedbackScore >= 4")
    List<ContextAwareReplyEntity> findHighQualityReplies();
    
    // 分页查询方法
    
    /**
     * 分页查询用户回复记录
     */
    Page<ContextAwareReplyEntity> findByUserId(String userId, Pageable pageable);
    
    /**
     * 分页查询会话回复记录
     */
    Page<ContextAwareReplyEntity> findBySessionId(String sessionId, Pageable pageable);
    
    /**
     * 分页查询用户和会话回复记录
     */
    Page<ContextAwareReplyEntity> findByUserIdAndSessionId(String userId, String sessionId, Pageable pageable);
    
    /**
     * 分页查询指定状态的回复记录
     */
    Page<ContextAwareReplyEntity> findByStatus(String status, Pageable pageable);
    
    /**
     * 分页查询已使用的回复记录
     */
    Page<ContextAwareReplyEntity> findByUsedTrue(Pageable pageable);
    
    // 时间范围查询方法
    
    /**
     * 查询创建时间在指定范围内的回复记录
     */
    List<ContextAwareReplyEntity> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * 查询用户创建时间在指定范围内的回复记录
     */
    List<ContextAwareReplyEntity> findByUserIdAndCreatedAtBetween(String userId, LocalDateTime start, LocalDateTime end);
    
    /**
     * 查询已过期的回复记录
     */
    @Query("SELECT r FROM ContextAwareReplyEntity r WHERE r.expiresAt < :now")
    List<ContextAwareReplyEntity> findExpiredReplies(@Param("now") LocalDateTime now);
    
    // 意图相关查询方法
    
    /**
     * 根据意图查询回复记录
     */
    List<ContextAwareReplyEntity> findByDetectedIntent(String detectedIntent);
    
    /**
     * 根据意图和用户查询回复记录
     */
    List<ContextAwareReplyEntity> findByUserIdAndDetectedIntent(String userId, String detectedIntent);
    
    /**
     * 根据意图置信度阈值查询回复记录
     */
    @Query("SELECT r FROM ContextAwareReplyEntity r WHERE r.intentConfidence >= :confidence")
    List<ContextAwareReplyEntity> findByIntentConfidenceGreaterThanEqual(@Param("confidence") Double confidence);
    
    /**
     * 查询高置信度意图的回复记录
     */
    @Query("SELECT r FROM ContextAwareReplyEntity r WHERE r.intentConfidence >= 0.8")
    List<ContextAwareReplyEntity> findHighConfidenceReplies();
    
    // 语言风格相关查询方法
    
    /**
     * 根据语言风格查询回复记录
     */
    List<ContextAwareReplyEntity> findByLanguageStyle(String languageStyle);
    
    /**
     * 根据语言风格和用户查询回复记录
     */
    List<ContextAwareReplyEntity> findByUserIdAndLanguageStyle(String userId, String languageStyle);
    
    // 统计查询方法
    
    /**
     * 统计用户回复记录数量
     */
    Long countByUserId(String userId);
    
    /**
     * 统计会话回复记录数量
     */
    Long countBySessionId(String sessionId);
    
    /**
     * 统计指定状态的回复记录数量
     */
    Long countByStatus(String status);
    
    /**
     * 统计已使用的回复记录数量
     */
    Long countByUsedTrue();
    
    /**
     * 统计高质量回复记录数量
     */
    @Query("SELECT COUNT(r) FROM ContextAwareReplyEntity r WHERE r.userFeedbackScore >= 4")
    Long countHighQualityReplies();
    
    /**
     * 统计各意图分布数量
     */
    @Query("SELECT r.detectedIntent, COUNT(r) FROM ContextAwareReplyEntity r GROUP BY r.detectedIntent")
    List<Object[]> countByIntentGroup();
    
    /**
     * 统计各语言风格分布数量
     */
    @Query("SELECT r.languageStyle, COUNT(r) FROM ContextAwareReplyEntity r GROUP BY r.languageStyle")
    List<Object[]> countByLanguageStyleGroup();
    
    /**
     * 统计用户平均反馈评分
     */
    @Query("SELECT AVG(r.userFeedbackScore) FROM ContextAwareReplyEntity r WHERE r.userFeedbackScore IS NOT NULL")
    Double averageFeedbackScore();
    
    /**
     * 统计用户平均生成时间
     */
    @Query("SELECT AVG(r.generationTimeMs) FROM ContextAwareReplyEntity r WHERE r.generationTimeMs IS NOT NULL")
    Double averageGenerationTime();
    
    // 高级查询方法
    
    /**
     * 搜索包含特定关键词的上下文摘要
     */
    @Query("SELECT r FROM ContextAwareReplyEntity r WHERE r.contextSummary LIKE %:keyword%")
    List<ContextAwareReplyEntity> searchByContextSummary(@Param("keyword") String keyword);
    
    /**
     * 搜索包含特定关键词的触发消息内容
     */
    @Query("SELECT r FROM ContextAwareReplyEntity r WHERE r.triggerMessageContent LIKE %:keyword%")
    List<ContextAwareReplyEntity> searchByTriggerMessageContent(@Param("keyword") String keyword);
    
    /**
     * 搜索包含特定关键词的回复内容
     */
    @Query("SELECT r FROM ContextAwareReplyEntity r WHERE r.selectedReply LIKE %:keyword%")
    List<ContextAwareReplyEntity> searchBySelectedReply(@Param("keyword") String keyword);
    
    /**
     * 复杂组合查询：用户、意图、时间范围
     */
    @Query("SELECT r FROM ContextAwareReplyEntity r WHERE r.userId = :userId AND r.detectedIntent = :intent AND r.createdAt BETWEEN :start AND :end")
    List<ContextAwareReplyEntity> findByUserIdAndIntentAndDateRange(
        @Param("userId") String userId,
        @Param("intent") String intent,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    /**
     * 批量更新状态
     */
    @Query("UPDATE ContextAwareReplyEntity r SET r.status = :status WHERE r.id IN :ids")
    int updateStatusByIds(@Param("status") String status, @Param("ids") List<Long> ids);
    
    /**
     * 批量标记为已使用
     */
    @Query("UPDATE ContextAwareReplyEntity r SET r.used = true WHERE r.id IN :ids")
    int markAsUsedByIds(@Param("ids") List<Long> ids);
    
    /**
     * 批量删除过期回复
     */
    @Query("DELETE FROM ContextAwareReplyEntity r WHERE r.expiresAt < :now")
    int deleteExpiredReplies(@Param("now") LocalDateTime now);
    
    /**
     * 清理低质量回复（评分≤2）
     */
    @Query("DELETE FROM ContextAwareReplyEntity r WHERE r.userFeedbackScore <= 2")
    int cleanupLowQualityReplies();
    
    /**
     * 获取最近N条用户回复
     */
    @Query(value = "SELECT * FROM context_aware_reply WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit", nativeQuery = true)
    List<ContextAwareReplyEntity> findRecentByUserId(@Param("userId") String userId, @Param("limit") int limit);
    
    /**
     * 获取用户最常用的意图
     */
    @Query("SELECT r.detectedIntent, COUNT(r) as count FROM ContextAwareReplyEntity r WHERE r.userId = :userId GROUP BY r.detectedIntent ORDER BY count DESC")
    List<Object[]> findTopIntentsByUser(@Param("userId") String userId);
    
    /**
     * 获取用户最常用的语言风格
     */
    @Query("SELECT r.languageStyle, COUNT(r) as count FROM ContextAwareReplyEntity r WHERE r.userId = :userId GROUP BY r.languageStyle ORDER BY count DESC")
    List<Object[]> findTopLanguageStylesByUser(@Param("userId") String userId);
}