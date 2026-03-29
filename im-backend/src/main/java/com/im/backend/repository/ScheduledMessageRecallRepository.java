package com.im.backend.repository;

import com.im.backend.model.ScheduledMessageRecall;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息定时撤回数据访问层
 */
@Repository
public interface ScheduledMessageRecallRepository extends JpaRepository<ScheduledMessageRecall, Long> {
    
    /**
     * 根据用户ID查询所有定时撤回记录
     */
    List<ScheduledMessageRecall> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 分页查询用户的定时撤回记录
     */
    Page<ScheduledMessageRecall> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据消息ID查询定时撤回记录
     */
    Optional<ScheduledMessageRecall> findByMessageId(Long messageId);
    
    /**
     * 查询指定消息的所有定时撤回记录
     */
    List<ScheduledMessageRecall> findByMessageIdOrderByCreatedAtDesc(Long messageId);
    
    /**
     * 根据会话ID查询定时撤回记录
     */
    List<ScheduledMessageRecall> findByConversationIdOrderByCreatedAtDesc(Long conversationId);
    
    /**
     * 查询指定状态的所有记录
     */
    List<ScheduledMessageRecall> findByStatusOrderByScheduledRecallTimeAsc(
        ScheduledMessageRecall.RecallStatus status);
    
    /**
     * 查询待执行且已到达执行时间的记录
     */
    @Query("SELECT sr FROM ScheduledMessageRecall sr WHERE sr.status = 'PENDING' " +
           "AND sr.scheduledRecallTime <= :now")
    List<ScheduledMessageRecall> findPendingAndDue(@Param("now") LocalDateTime now);
    
    /**
     * 查询用户待执行的定时撤回
     */
    @Query("SELECT sr FROM ScheduledMessageRecall sr WHERE sr.userId = :userId " +
           "AND sr.status = 'PENDING' ORDER BY sr.scheduledRecallTime ASC")
    List<ScheduledMessageRecall> findPendingByUserId(@Param("userId") Long userId);
    
    /**
     * 统计用户待执行的数量
     */
    long countByUserIdAndStatus(Long userId, ScheduledMessageRecall.RecallStatus status);
    
    /**
     * 查询用户的所有记录数量
     */
    long countByUserId(Long userId);
    
    /**
     * 更新状态为已执行
     */
    @Modifying
    @Query("UPDATE ScheduledMessageRecall sr SET sr.status = 'EXECUTED', " +
           "sr.executedAt = :executedAt, sr.updatedAt = :executedAt WHERE sr.id = :id")
    int markAsExecuted(@Param("id") Long id, @Param("executedAt") LocalDateTime executedAt);
    
    /**
     * 更新状态为已取消
     */
    @Modifying
    @Query("UPDATE ScheduledMessageRecall sr SET sr.status = 'CANCELLED', " +
           "sr.updatedAt = :updatedAt WHERE sr.id = :id AND sr.status = 'PENDING'")
    int markAsCancelled(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 更新状态为执行失败
     */
    @Modifying
    @Query("UPDATE ScheduledMessageRecall sr SET sr.status = 'FAILED', " +
           "sr.updatedAt = :updatedAt WHERE sr.id = :id")
    int markAsFailed(@Param("id") Long id, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 查询过期的待执行任务（超过执行时间但状态仍为PENDING）
     */
    @Query("SELECT sr FROM ScheduledMessageRecall sr WHERE sr.status = 'PENDING' " +
           "AND sr.scheduledRecallTime < :expiredTime")
    List<ScheduledMessageRecall> findExpiredPending(
        @Param("expiredTime") LocalDateTime expiredTime);
    
    /**
     * 根据状态分页查询
     */
    Page<ScheduledMessageRecall> findByStatusOrderByCreatedAtDesc(
        ScheduledMessageRecall.RecallStatus status, Pageable pageable);
    
    /**
     * 查询用户在指定会话中的定时撤回
     */
    @Query("SELECT sr FROM ScheduledMessageRecall sr WHERE sr.userId = :userId " +
           "AND sr.conversationId = :conversationId ORDER BY sr.createdAt DESC")
    List<ScheduledMessageRecall> findByUserIdAndConversationId(
        @Param("userId") Long userId, 
        @Param("conversationId") Long conversationId);
    
    /**
     * 检查消息是否已设置定时撤回
     */
    boolean existsByMessageIdAndStatus(Long messageId, ScheduledMessageRecall.RecallStatus status);
    
    /**
     * 删除已执行/已取消/已失败的过期记录
     */
    @Modifying
    @Query("DELETE FROM ScheduledMessageRecall sr WHERE sr.status IN ('EXECUTED', 'CANCELLED', 'FAILED') " +
           "AND sr.updatedAt < :beforeDate")
    int deleteOldCompletedRecords(@Param("beforeDate") LocalDateTime beforeDate);
}
