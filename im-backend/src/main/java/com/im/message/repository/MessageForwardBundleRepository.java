package com.im.message.repository;

import com.im.message.entity.MessageForwardBundleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息合并转发数据访问层
 */
@Repository
public interface MessageForwardBundleRepository extends JpaRepository<MessageForwardBundleEntity, Long> {

    // 按 Bundle ID 查询
    Optional<MessageForwardBundleEntity> findByBundleId(String bundleId);
    
    // 按创建者查询草稿
    @Query("SELECT b FROM MessageForwardBundleEntity b WHERE b.createdBy = :userId AND b.status = 'DRAFT' ORDER BY b.createdAt DESC")
    List<MessageForwardBundleEntity> findDraftsByUser(@Param("userId") Long userId);
    
    // 按创建者和状态查询
    List<MessageForwardBundleEntity> findByCreatedByAndStatus(Long userId, MessageForwardBundleEntity.ForwardStatus status);
    
    // 按源会话 ID 查询
    List<MessageForwardBundleEntity> findBySourceConversationId(Long sourceConversationId);
    
    // 按目标会话 ID 查询
    List<MessageForwardBundleEntity> findByTargetConversationId(Long targetConversationId);
    
    // 按创建者查询最近的转发记录
    @Query("SELECT b FROM MessageForwardBundleEntity b WHERE b.createdBy = :userId AND b.status = 'SENT' ORDER BY b.forwardedAt DESC")
    List<MessageForwardBundleEntity> findRecentSentByUser(@Param("userId") Long userId, int limit);
    
    // 统计用户的转发次数
    @Query("SELECT COUNT(b) FROM MessageForwardBundleEntity b WHERE b.createdBy = :userId")
    Long countByUser(@Param("userId") Long userId);
    
    // 统计成功的转发次数
    @Query("SELECT COUNT(b) FROM MessageForwardBundleEntity b WHERE b.createdBy = :userId AND b.status = 'SENT'")
    Long countSuccessfulByUser(@Param("userId") Long userId);
    
    // 批量更新状态为已发送
    @Modifying
    @Query("UPDATE MessageForwardBundleEntity b SET b.status = 'SENT', b.forwardedAt = :now, b.updatedAt = :now WHERE b.id IN :ids")
    int markAsSent(@Param("ids") List<Long> ids, @Param("now") LocalDateTime now);
    
    // 批量更新状态为失败
    @Modifying
    @Query("UPDATE MessageForwardBundleEntity b SET b.status = 'FAILED', b.errorMessage = :error, b.updatedAt = :now WHERE b.id IN :ids")
    int markAsFailed(@Param("ids") List<Long> ids, @Param("error") String error, @Param("now") LocalDateTime now);
    
    // 查找过期的草稿（超过 24 小时）
    @Query("SELECT b FROM MessageForwardBundleEntity b WHERE b.status = 'DRAFT' AND b.createdAt <= :cutoffTime")
    List<MessageForwardBundleEntity> findExpiredDrafts(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // 按转发类型统计
    @Query("SELECT b.forwardType, COUNT(b) FROM MessageForwardBundleEntity b WHERE b.createdBy = :userId GROUP BY b.forwardType")
    List<Object[]> countByForwardType(@Param("userId") Long userId);
    
    // 查找包含特定消息 ID 的转发 bundle
    @Query("SELECT b FROM MessageForwardBundleEntity b JOIN b.messageIds mid WHERE mid = :messageId")
    List<MessageForwardBundleEntity> findByMessageId(@Param("messageId") Long messageId);
    
    // 查找有待发送的 bundle
    @Query("SELECT b FROM MessageForwardBundleEntity b WHERE b.status = 'PENDING' ORDER BY b.createdAt ASC")
    List<MessageForwardBundleEntity> findPendingBundles();
    
    // 按创建时间范围查询
    @Query("SELECT b FROM MessageForwardBundleEntity b WHERE b.createdBy = :userId AND b.createdAt BETWEEN :startTime AND :endTime ORDER BY b.createdAt DESC")
    List<MessageForwardBundleEntity> findByTimeRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
    
    // 删除过期的草稿
    @Modifying
    @Query("DELETE FROM MessageForwardBundleEntity b WHERE b.status = 'DRAFT' AND b.createdAt <= :cutoffTime")
    int deleteExpiredDrafts(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    // 查找需要重试的失败 bundle
    @Query("SELECT b FROM MessageForwardBundleEntity b WHERE b.status = 'FAILED' AND b.retryCount < :maxRetries ORDER BY b.updatedAt ASC")
    List<MessageForwardBundleEntity> findFailedForRetry(@Param("maxRetries") int maxRetries);
    
    // 增加重试次数
    @Modifying
    @Query("UPDATE MessageForwardBundleEntity b SET b.retryCount = b.retryCount + 1, b.updatedAt = :now WHERE b.id = :id")
    int incrementRetryCount(@Param("id") Long id, @Param("now") LocalDateTime now);
}