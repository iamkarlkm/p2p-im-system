package com.im.backend.repository;

import com.im.backend.model.MessageMultiSelectOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息多选操作 Repository
 */
@Repository
public interface MessageMultiSelectOperationRepository extends JpaRepository<MessageMultiSelectOperation, Long> {
    
    /**
     * 根据用户ID查询操作记录
     */
    List<MessageMultiSelectOperation> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 分页查询用户操作记录
     */
    Page<MessageMultiSelectOperation> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    
    /**
     * 根据操作类型查询
     */
    List<MessageMultiSelectOperation> findByUserIdAndOperationTypeOrderByCreatedAtDesc(
            Long userId, MessageMultiSelectOperation.OperationType operationType);
    
    /**
     * 查询未完成的操作
     */
    @Query("SELECT o FROM MessageMultiSelectOperation o WHERE o.userId = :userId AND o.completedAt IS NULL")
    List<MessageMultiSelectOperation> findPendingByUserId(@Param("userId") Long userId);
    
    /**
     * 查询已完成的操作
     */
    @Query("SELECT o FROM MessageMultiSelectOperation o WHERE o.userId = :userId AND o.completedAt IS NOT NULL")
    List<MessageMultiSelectOperation> findCompletedByUserId(@Param("userId") Long userId);
    
    /**
     * 根据时间范围查询
     */
    @Query("SELECT o FROM MessageMultiSelectOperation o WHERE o.userId = :userId " +
           "AND o.createdAt BETWEEN :startTime AND :endTime ORDER BY o.createdAt DESC")
    List<MessageMultiSelectOperation> findByUserIdAndTimeRange(
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计用户操作次数
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户某类型操作次数
     */
    long countByUserIdAndOperationType(Long userId, MessageMultiSelectOperation.OperationType operationType);
    
    /**
     * 统计成功次数
     */
    @Query("SELECT COUNT(o) FROM MessageMultiSelectOperation o WHERE o.userId = :userId AND o.operationResult = 'SUCCESS'")
    long countSuccessfulByUserId(@Param("userId") Long userId);
    
    /**
     * 获取用户最近的操作
     */
    List<MessageMultiSelectOperation> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);
    
    /**
     * 根据会话ID查询
     */
    List<MessageMultiSelectOperation> findByUserIdAndConversationIdOrderByCreatedAtDesc(
            Long userId, String conversationId);
    
    /**
     * 删除旧记录（清理历史）
     */
    @Query("DELETE FROM MessageMultiSelectOperation o WHERE o.createdAt < :beforeTime")
    void deleteByCreatedAtBefore(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 获取用户统计信息
     */
    @Query("SELECT o.operationType, COUNT(o), AVG(o.successCount * 1.0 / SIZE(o.selectedMessageIds)) " +
           "FROM MessageMultiSelectOperation o WHERE o.userId = :userId GROUP BY o.operationType")
    List<Object[]> getStatisticsByUserId(@Param("userId") Long userId);
}
