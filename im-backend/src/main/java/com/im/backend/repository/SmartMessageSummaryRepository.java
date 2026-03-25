package com.im.backend.repository;

import com.im.backend.entity.SmartMessageSummaryEntity;
import com.im.backend.enums.SummaryQuality;
import com.im.backend.enums.SummaryStatus;
import com.im.backend.enums.SummaryType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 智能消息摘要仓储接口
 * 提供丰富的查询方法，支持分页、统计、批量操作
 */
@Repository
public interface SmartMessageSummaryRepository extends 
        JpaRepository<SmartMessageSummaryEntity, Long>, 
        JpaSpecificationExecutor<SmartMessageSummaryEntity> {

    // ==================== 基本查询方法 ====================
    
    /**
     * 根据会话ID查询摘要列表
     */
    List<SmartMessageSummaryEntity> findBySessionId(String sessionId);
    
    /**
     * 根据会话ID和用户ID查询摘要列表
     */
    List<SmartMessageSummaryEntity> findBySessionIdAndUserId(String sessionId, String userId);
    
    /**
     * 根据会话ID和摘要类型查询摘要列表
     */
    List<SmartMessageSummaryEntity> findBySessionIdAndSummaryType(String sessionId, SummaryType summaryType);
    
    /**
     * 根据消息ID查询摘要
     */
    Optional<SmartMessageSummaryEntity> findByMessageId(String messageId);
    
    /**
     * 根据消息ID和用户ID查询摘要
     */
    Optional<SmartMessageSummaryEntity> findByMessageIdAndUserId(String messageId, String userId);
    
    /**
     * 根据用户ID查询摘要列表
     */
    List<SmartMessageSummaryEntity> findByUserId(String userId);
    
    /**
     * 根据用户ID和摘要类型查询摘要列表
     */
    List<SmartMessageSummaryEntity> findByUserIdAndSummaryType(String userId, SummaryType summaryType);
    
    /**
     * 根据状态查询摘要列表
     */
    List<SmartMessageSummaryEntity> findByStatus(SummaryStatus status);
    
    /**
     * 根据状态和摘要类型查询摘要列表
     */
    List<SmartMessageSummaryEntity> findByStatusAndSummaryType(SummaryStatus status, SummaryType summaryType);
    
    /**
     * 根据质量查询摘要列表
     */
    List<SmartMessageSummaryEntity> findByQuality(SummaryQuality quality);
    
    /**
     * 根据质量和状态查询摘要列表
     */
    List<SmartMessageSummaryEntity> findByQualityAndStatus(SummaryQuality quality, SummaryStatus status);
    
    // ==================== 分页查询方法 ====================
    
    /**
     * 根据会话ID分页查询摘要
     */
    Page<SmartMessageSummaryEntity> findBySessionId(String sessionId, Pageable pageable);
    
    /**
     * 根据用户ID分页查询摘要
     */
    Page<SmartMessageSummaryEntity> findByUserId(String userId, Pageable pageable);
    
    /**
     * 根据状态分页查询摘要
     */
    Page<SmartMessageSummaryEntity> findByStatus(SummaryStatus status, Pageable pageable);
    
    /**
     * 根据质量和状态分页查询摘要
     */
    Page<SmartMessageSummaryEntity> findByQualityAndStatus(SummaryQuality quality, SummaryStatus status, Pageable pageable);
    
    /**
     * 根据用户ID和状态分页查询摘要
     */
    Page<SmartMessageSummaryEntity> findByUserIdAndStatus(String userId, SummaryStatus status, Pageable pageable);
    
    /**
     * 根据用户ID和摘要类型分页查询摘要
     */
    Page<SmartMessageSummaryEntity> findByUserIdAndSummaryType(String userId, SummaryType summaryType, Pageable pageable);
    
    // ==================== 时间范围查询方法 ====================
    
    /**
     * 查询创建时间在指定范围内的摘要
     */
    List<SmartMessageSummaryEntity> findByCreatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询更新时间在指定范围内的摘要
     */
    List<SmartMessageSummaryEntity> findByUpdatedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询生成时间在指定范围内的摘要
     */
    List<SmartMessageSummaryEntity> findByGeneratedAtBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询缓存过期时间在指定范围内的摘要
     */
    List<SmartMessageSummaryEntity> findByCacheExpiryTimeBetween(LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 查询已过期的缓存摘要
     */
    List<SmartMessageSummaryEntity> findByCacheExpiryTimeBefore(LocalDateTime time);
    
    // ==================== 统计查询方法 ====================
    
    /**
     * 统计指定会话的摘要数量
     */
    Long countBySessionId(String sessionId);
    
    /**
     * 统计指定用户的摘要数量
     */
    Long countByUserId(String userId);
    
    /**
     * 统计指定状态和用户的摘要数量
     */
    Long countByUserIdAndStatus(String userId, SummaryStatus status);
    
    /**
     * 统计指定状态和会话的摘要数量
     */
    Long countBySessionIdAndStatus(String sessionId, SummaryStatus status);
    
    /**
     * 统计指定质量和用户的摘要数量
     */
    Long countByUserIdAndQuality(String userId, SummaryQuality quality);
    
    /**
     * 统计指定类型和用户的摘要数量
     */
    Long countByUserIdAndSummaryType(String userId, SummaryType summaryType);
    
    /**
     * 统计需要重新生成的摘要数量（质量过低）
     */
    @Query("SELECT COUNT(s) FROM SmartMessageSummaryEntity s WHERE s.qualityScore < 60 AND s.status = 'COMPLETED'")
    Long countByNeedsRegeneration();
    
    /**
     * 统计已过期的缓存摘要数量
     */
    @Query("SELECT COUNT(s) FROM SmartMessageSummaryEntity s WHERE s.offlineCached = true AND s.cacheExpiryTime < CURRENT_TIMESTAMP")
    Long countByExpiredCache();
    
    // ==================== 批量更新方法 ====================
    
    /**
     * 批量更新摘要状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE SmartMessageSummaryEntity s SET s.status = :status, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    int updateStatusByIds(@Param("ids") List<Long> ids, @Param("status") SummaryStatus status);
    
    /**
     * 批量更新摘要质量
     */
    @Modifying
    @Transactional
    @Query("UPDATE SmartMessageSummaryEntity s SET s.quality = :quality, s.qualityScore = :score, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    int updateQualityByIds(@Param("ids") List<Long> ids, @Param("quality") SummaryQuality quality, @Param("score") Integer score);
    
    /**
     * 批量标记为已读
     */
    @Modifying
    @Transactional
    @Query("UPDATE SmartMessageSummaryEntity s SET s.isRead = true, s.readAt = CURRENT_TIMESTAMP, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    int markAsReadByIds(@Param("ids") List<Long> ids);
    
    /**
     * 批量标记为喜欢
     */
    @Modifying
    @Transactional
    @Query("UPDATE SmartMessageSummaryEntity s SET s.isFavorite = :favorite, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    int markAsFavoriteByIds(@Param("ids") List<Long> ids, @Param("favorite") boolean favorite);
    
    /**
     * 批量逻辑删除
     */
    @Modifying
    @Transactional
    @Query("UPDATE SmartMessageSummaryEntity s SET s.deleted = true, s.deletedAt = CURRENT_TIMESTAMP, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    int softDeleteByIds(@Param("ids") List<Long> ids);
    
    /**
     * 批量物理删除已逻辑删除的记录
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SmartMessageSummaryEntity s WHERE s.deleted = true AND s.deletedAt < :beforeTime")
    int hardDeleteExpired(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 批量更新缓存过期时间
     */
    @Modifying
    @Transactional
    @Query("UPDATE SmartMessageSummaryEntity s SET s.cacheExpiryTime = :expiryTime, s.updatedAt = CURRENT_TIMESTAMP WHERE s.id IN :ids")
    int updateCacheExpiryByIds(@Param("ids") List<Long> ids, @Param("expiryTime") LocalDateTime expiryTime);
    
    // ==================== 高级查询方法 ====================
    
    /**
     * 根据关键词搜索摘要内容
     */
    @Query("SELECT s FROM SmartMessageSummaryEntity s WHERE " +
           "LOWER(s.summaryContent) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.originalContent) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<SmartMessageSummaryEntity> searchByKeyword(@Param("keyword") String keyword);
    
    /**
     * 根据关键词和用户搜索摘要内容
     */
    @Query("SELECT s FROM SmartMessageSummaryEntity s WHERE s.userId = :userId AND " +
           "(LOWER(s.summaryContent) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.originalContent) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<SmartMessageSummaryEntity> searchByKeywordAndUser(@Param("keyword") String keyword, @Param("userId") String userId);
    
    /**
     * 查询高质量摘要（评分 >= 80）
     */
    @Query("SELECT s FROM SmartMessageSummaryEntity s WHERE s.qualityScore >= 80 AND s.status = 'COMPLETED'")
    List<SmartMessageSummaryEntity> findHighQualitySummaries();
    
    /**
     * 查询低质量需要重新生成的摘要
     */
    @Query("SELECT s FROM SmartMessageSummaryEntity s WHERE s.qualityScore < 60 AND s.status = 'COMPLETED'")
    List<SmartMessageSummaryEntity> findLowQualitySummaries();
    
    /**
     * 查询用户的最近摘要
     */
    @Query("SELECT s FROM SmartMessageSummaryEntity s WHERE s.userId = :userId AND s.status = 'COMPLETED' ORDER BY s.generatedAt DESC")
    List<SmartMessageSummaryEntity> findRecentByUser(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 查询会话的最近摘要
     */
    @Query("SELECT s FROM SmartMessageSummaryEntity s WHERE s.sessionId = :sessionId AND s.status = 'COMPLETED' ORDER BY s.generatedAt DESC")
    List<SmartMessageSummaryEntity> findRecentBySession(@Param("sessionId") String sessionId, Pageable pageable);
    
    /**
     * 查询用户最喜欢的摘要（评分最高）
     */
    @Query("SELECT s FROM SmartMessageSummaryEntity s WHERE s.userId = :userId AND s.status = 'COMPLETED' ORDER BY s.qualityScore DESC, s.userRating DESC")
    List<SmartMessageSummaryEntity> findTopByUser(@Param("userId") String userId, Pageable pageable);
    
    /**
     * 查询用户反馈评分高的摘要
     */
    @Query("SELECT s FROM SmartMessageSummaryEntity s WHERE s.userId = :userId AND s.userRating >= :minRating ORDER BY s.userRating DESC")
    List<SmartMessageSummaryEntity> findHighlyRatedByUser(@Param("userId") String userId, @Param("minRating") int minRating);
    
    /**
     * 查询用户的个性化摘要统计
     */
    @Query("SELECT s.summaryStyle, COUNT(s) as count, AVG(s.qualityScore) as avgScore " +
           "FROM SmartMessageSummaryEntity s " +
           "WHERE s.userId = :userId AND s.summaryStyle IS NOT NULL " +
           "GROUP BY s.summaryStyle")
    List<Object[]> findUserStyleStats(@Param("userId") String userId);
    
    /**
     * 查询用户的摘要质量分布
     */
    @Query("SELECT s.quality, COUNT(s) as count FROM SmartMessageSummaryEntity s " +
           "WHERE s.userId = :userId GROUP BY s.quality")
    List<Object[]> findUserQualityDistribution(@Param("userId") String userId);
    
    // ==================== 清理和归档方法 ====================
    
    /**
     * 清理过期的缓存摘要（物理删除）
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM SmartMessageSummaryEntity s WHERE s.offlineCached = true AND s.cacheExpiryTime < CURRENT_TIMESTAMP")
    int cleanupExpiredCache();
    
    /**
     * 归档旧摘要（更新状态为过期）
     */
    @Modifying
    @Transactional
    @Query("UPDATE SmartMessageSummaryEntity s SET s.status = 'EXPIRED', s.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE s.status = 'COMPLETED' AND s.createdAt < :beforeTime")
    int archiveOldSummaries(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 批量更新需要重新生成的摘要状态
     */
    @Modifying
    @Transactional
    @Query("UPDATE SmartMessageSummaryEntity s SET s.status = 'NEEDS_REGEN', s.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE s.qualityScore < 60 AND s.status = 'COMPLETED'")
    int markLowQualityForRegeneration();
    
    // ==================== 检查和验证方法 ====================
    
    /**
     * 检查摘要是否存在
     */
    boolean existsBySessionIdAndUserIdAndSummaryType(String sessionId, String userId, SummaryType summaryType);
    
    /**
     * 检查消息是否已有摘要
     */
    boolean existsByMessageIdAndUserId(String messageId, String userId);
    
    /**
     * 检查摘要是否已过期
     */
    @Query("SELECT COUNT(s) > 0 FROM SmartMessageSummaryEntity s WHERE s.id = :id AND s.offlineCached = true AND s.cacheExpiryTime < CURRENT_TIMESTAMP")
    boolean isSummaryExpired(@Param("id") Long id);
    
    /**
     * 验证摘要是否属于用户
     */
    boolean existsByIdAndUserId(Long id, String userId);
}