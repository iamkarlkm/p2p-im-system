package com.im.backend.repository;

import com.im.backend.entity.MessageArchiveEntity;
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
 * 消息归档记录仓储接口
 */
@Repository
public interface MessageArchiveRepository extends JpaRepository<MessageArchiveEntity, Long> {
    
    /**
     * 根据原始消息ID查找归档记录
     */
    Optional<MessageArchiveEntity> findByMessageId(Long messageId);
    
    /**
     * 根据消息ID列表批量查找归档记录
     */
    List<MessageArchiveEntity> findByMessageIdIn(List<Long> messageIds);
    
    /**
     * 根据存储策略ID查找归档记录
     */
    List<MessageArchiveEntity> findByStorageStrategyId(Long strategyId);
    
    /**
     * 根据存储策略ID分页查找归档记录
     */
    Page<MessageArchiveEntity> findByStorageStrategyId(Long strategyId, Pageable pageable);
    
    /**
     * 根据发送者ID查找归档记录
     */
    List<MessageArchiveEntity> findBySenderId(Long senderId);
    
    /**
     * 根据会话ID查找归档记录
     */
    List<MessageArchiveEntity> findBySessionId(Long sessionId);
    
    /**
     * 根据状态查找归档记录
     */
    List<MessageArchiveEntity> findByStatus(String status);
    
    /**
     * 根据存储策略ID和状态查找归档记录
     */
    List<MessageArchiveEntity> findByStorageStrategyIdAndStatus(Long strategyId, String status);
    
    /**
     * 根据归档时间范围查找归档记录
     */
    @Query("SELECT a FROM MessageArchiveEntity a WHERE a.archiveTime BETWEEN :startTime AND :endTime")
    List<MessageArchiveEntity> findByArchiveTimeBetween(@Param("startTime") LocalDateTime startTime,
                                                        @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据原始消息创建时间范围查找归档记录
     */
    @Query("SELECT a FROM MessageArchiveEntity a WHERE a.originalCreatedAt BETWEEN :startTime AND :endTime")
    List<MessageArchiveEntity> findByOriginalCreatedAtBetween(@Param("startTime") LocalDateTime startTime,
                                                              @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据存储路径查找归档记录
     */
    List<MessageArchiveEntity> findByStoragePath(String storagePath);
    
    /**
     * 根据存储桶和路径前缀查找归档记录
     */
    @Query("SELECT a FROM MessageArchiveEntity a WHERE a.storageBucket = :bucket AND a.storagePath LIKE :prefix%")
    List<MessageArchiveEntity> findByStorageBucketAndStoragePathStartingWith(@Param("bucket") String bucket,
                                                                             @Param("prefix") String prefix);
    
    /**
     * 统计归档记录数量（按策略）
     */
    @Query("SELECT COUNT(a) FROM MessageArchiveEntity a WHERE a.storageStrategyId = :strategyId")
    long countByStorageStrategyId(@Param("strategyId") Long strategyId);
    
    /**
     * 统计归档记录数量（按状态）
     */
    @Query("SELECT COUNT(a) FROM MessageArchiveEntity a WHERE a.status = :status")
    long countByStatus(@Param("status") String status);
    
    /**
     * 统计归档记录数量（按策略和状态）
     */
    @Query("SELECT COUNT(a) FROM MessageArchiveEntity a WHERE a.storageStrategyId = :strategyId AND a.status = :status")
    long countByStorageStrategyIdAndStatus(@Param("strategyId") Long strategyId, @Param("status") String status);
    
    /**
     * 统计归档总大小（按策略）
     */
    @Query("SELECT COALESCE(SUM(a.archivedSize), 0) FROM MessageArchiveEntity a WHERE a.storageStrategyId = :strategyId")
    long sumArchivedSizeByStorageStrategyId(@Param("strategyId") Long strategyId);
    
    /**
     * 统计归档总大小
     */
    @Query("SELECT COALESCE(SUM(a.archivedSize), 0) FROM MessageArchiveEntity a")
    long sumTotalArchivedSize();
    
    /**
     * 查找需要清理的归档记录（已上传且超过保留期）
     */
    @Query("""
        SELECT a FROM MessageArchiveEntity a 
        WHERE a.status IN ('UPLOADED', 'RESTORED') 
        AND a.archiveTime < :thresholdTime
        ORDER BY a.archiveTime ASC
    """)
    List<MessageArchiveEntity> findArchivesForCleanup(@Param("thresholdTime") LocalDateTime thresholdTime);
    
    /**
     * 查找需要清理的归档记录（按策略）
     */
    @Query("""
        SELECT a FROM MessageArchiveEntity a 
        WHERE a.storageStrategyId = :strategyId 
        AND a.status IN ('UPLOADED', 'RESTORED') 
        AND a.archiveTime < :thresholdTime
        ORDER BY a.archiveTime ASC
    """)
    List<MessageArchiveEntity> findArchivesForCleanupByStrategy(@Param("strategyId") Long strategyId,
                                                                @Param("thresholdTime") LocalDateTime thresholdTime);
    
    /**
     * 批量更新状态
     */
    @Query("""
        UPDATE MessageArchiveEntity a 
        SET a.status = :status, 
            a.updatedAt = CURRENT_TIMESTAMP 
        WHERE a.id IN :ids
    """)
    int updateStatusByIdIn(@Param("ids") List<Long> ids, @Param("status") String status);
    
    /**
     * 批量标记为已删除
     */
    @Query("""
        UPDATE MessageArchiveEntity a 
        SET a.status = 'DELETED',
            a.deletedTime = CURRENT_TIMESTAMP,
            a.deletedBy = :deletedBy,
            a.updatedAt = CURRENT_TIMESTAMP 
        WHERE a.id IN :ids
    """)
    int markAsDeletedByIdIn(@Param("ids") List<Long> ids, @Param("deletedBy") Long deletedBy);
    
    /**
     * 根据消息类型统计归档记录
     */
    @Query("SELECT a.messageType, COUNT(a) FROM MessageArchiveEntity a WHERE a.storageStrategyId = :strategyId GROUP BY a.messageType")
    List<Object[]> countByMessageType(@Param("strategyId") Long strategyId);
    
    /**
     * 按归档时间分组统计（按天）
     */
    @Query("""
        SELECT FUNCTION('DATE', a.archiveTime), COUNT(a) 
        FROM MessageArchiveEntity a 
        WHERE a.archiveTime BETWEEN :startTime AND :endTime 
        GROUP BY FUNCTION('DATE', a.archiveTime)
        ORDER BY FUNCTION('DATE', a.archiveTime)
    """)
    List<Object[]> countByArchiveDate(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找最早归档记录
     */
    @Query("SELECT a FROM MessageArchiveEntity a ORDER BY a.archiveTime ASC LIMIT 1")
    Optional<MessageArchiveEntity> findFirstByOrderByArchiveTimeAsc();
    
    /**
     * 查找最晚归档记录
     */
    @Query("SELECT a FROM MessageArchiveEntity a ORDER BY a.archiveTime DESC LIMIT 1")
    Optional<MessageArchiveEntity> findFirstByOrderByArchiveTimeDesc();
    
    /**
     * 检查消息是否已归档
     */
    @Query("SELECT COUNT(a) > 0 FROM MessageArchiveEntity a WHERE a.messageId = :messageId")
    boolean existsByMessageId(@Param("messageId") Long messageId);
    
    /**
     * 删除指定策略的所有归档记录
     */
    @Query("DELETE FROM MessageArchiveEntity a WHERE a.storageStrategyId = :strategyId")
    void deleteByStorageStrategyId(@Param("strategyId") Long strategyId);
    
    /**
     * 删除指定状态的归档记录
     */
    @Query("DELETE FROM MessageArchiveEntity a WHERE a.status = :status")
    void deleteByStatus(@Param("status") String status);
    
    /**
     * 清理已过期的归档记录
     */
    @Query("""
        DELETE FROM MessageArchiveEntity a 
        WHERE a.status = 'DELETED' 
        AND a.deletedTime < :thresholdTime
    """)
    int deleteDeletedArchives(@Param("thresholdTime") LocalDateTime thresholdTime);
}