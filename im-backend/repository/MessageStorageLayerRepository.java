package com.im.backend.repository;

import com.im.backend.entity.MessageStorageLayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息存储分层配置仓储接口
 */
@Repository
public interface MessageStorageLayerRepository extends JpaRepository<MessageStorageLayerEntity, Long> {
    
    /**
     * 根据策略名称查找配置
     */
    Optional<MessageStorageLayerEntity> findByStrategyName(String strategyName);
    
    /**
     * 查找启用的策略配置
     */
    List<MessageStorageLayerEntity> findByStatus(String status);
    
    /**
     * 查找所有启用的策略配置
     */
    @Query("SELECT s FROM MessageStorageLayerEntity s WHERE s.status = 'ENABLED'")
    List<MessageStorageLayerEntity> findAllEnabled();
    
    /**
     * 根据存储类型查找配置
     */
    List<MessageStorageLayerEntity> findByColdStorageType(String coldStorageType);
    
    /**
     * 查找需要执行归档的策略
     * 条件：启用自动归档、状态为ENABLED、且归档间隔已过
     */
    @Query("""
        SELECT s FROM MessageStorageLayerEntity s 
        WHERE s.autoArchiveEnabled = true 
        AND s.status = 'ENABLED' 
        AND (s.lastArchiveTime IS NULL OR s.lastArchiveTime < :thresholdTime)
        ORDER BY s.lastArchiveTime ASC NULLS FIRST
    """)
    List<MessageStorageLayerEntity> findStrategiesNeedingArchive(@Param("thresholdTime") LocalDateTime thresholdTime);
    
    /**
     * 查找需要执行清理的策略
     * 条件：启用自动清理、状态为ENABLED、且清理间隔已过
     */
    @Query("""
        SELECT s FROM MessageStorageLayerEntity s 
        WHERE s.autoCleanupEnabled = true 
        AND s.status = 'ENABLED' 
        AND (s.lastCleanupTime IS NULL OR s.lastCleanupTime < :thresholdTime)
        ORDER BY s.lastCleanupTime ASC NULLS FIRST
    """)
    List<MessageStorageLayerEntity> findStrategiesNeedingCleanup(@Param("thresholdTime") LocalDateTime thresholdTime);
    
    /**
     * 根据ID列表批量查找配置
     */
    List<MessageStorageLayerEntity> findByIdIn(List<Long> ids);
    
    /**
     * 统计启用的策略数量
     */
    @Query("SELECT COUNT(s) FROM MessageStorageLayerEntity s WHERE s.status = 'ENABLED'")
    long countEnabled();
    
    /**
     * 统计归档消息总数
     */
    @Query("SELECT COALESCE(SUM(s.archivedMessagesCount), 0) FROM MessageStorageLayerEntity s")
    long sumArchivedMessagesCount();
    
    /**
     * 统计归档消息总大小
     */
    @Query("SELECT COALESCE(SUM(s.archivedMessagesSize), 0) FROM MessageStorageLayerEntity s")
    long sumArchivedMessagesSize();
    
    /**
     * 统计清理消息总数
     */
    @Query("SELECT COALESCE(SUM(s.cleanedMessagesCount), 0) FROM MessageStorageLayerEntity s")
    long sumCleanedMessagesCount();
    
    /**
     * 查找按归档消息数量排序的策略
     */
    @Query("SELECT s FROM MessageStorageLayerEntity s ORDER BY s.archivedMessagesCount DESC")
    List<MessageStorageLayerEntity> findAllOrderByArchivedMessagesCountDesc();
    
    /**
     * 查找按归档消息大小排序的策略
     */
    @Query("SELECT s FROM MessageStorageLayerEntity s ORDER BY s.archivedMessagesSize DESC")
    List<MessageStorageLayerEntity> findAllOrderByArchivedMessagesSizeDesc();
    
    /**
     * 查找错误的策略配置
     */
    List<MessageStorageLayerEntity> findByStatusAndErrorMessageIsNotNull(String status);
    
    /**
     * 更新策略状态
     */
    @Query("""
        UPDATE MessageStorageLayerEntity s 
        SET s.status = :status, 
            s.updatedAt = CURRENT_TIMESTAMP 
        WHERE s.id = :id
    """)
    int updateStatus(@Param("id") Long id, @Param("status") String status);
    
    /**
     * 更新归档统计信息
     */
    @Query("""
        UPDATE MessageStorageLayerEntity s 
        SET s.lastArchiveTime = :lastArchiveTime,
            s.lastArchiveMessageId = :lastArchiveMessageId,
            s.archivedMessagesCount = s.archivedMessagesCount + :incrementCount,
            s.archivedMessagesSize = s.archivedMessagesSize + :incrementSize,
            s.updatedAt = CURRENT_TIMESTAMP
        WHERE s.id = :id
    """)
    int updateArchiveStats(
        @Param("id") Long id,
        @Param("lastArchiveTime") LocalDateTime lastArchiveTime,
        @Param("lastArchiveMessageId") Long lastArchiveMessageId,
        @Param("incrementCount") Long incrementCount,
        @Param("incrementSize") Long incrementSize
    );
    
    /**
     * 更新清理统计信息
     */
    @Query("""
        UPDATE MessageStorageLayerEntity s 
        SET s.lastCleanupTime = :lastCleanupTime,
            s.cleanedMessagesCount = s.cleanedMessagesCount + :incrementCount,
            s.updatedAt = CURRENT_TIMESTAMP
        WHERE s.id = :id
    """)
    int updateCleanupStats(
        @Param("id") Long id,
        @Param("lastCleanupTime") LocalDateTime lastCleanupTime,
        @Param("incrementCount") Long incrementCount
    );
    
    /**
     * 清除错误信息
     */
    @Query("""
        UPDATE MessageStorageLayerEntity s 
        SET s.errorMessage = NULL,
            s.status = 'ENABLED',
            s.updatedAt = CURRENT_TIMESTAMP
        WHERE s.id = :id
    """)
    int clearError(@Param("id") Long id);
    
    /**
     * 设置错误信息
     */
    @Query("""
        UPDATE MessageStorageLayerEntity s 
        SET s.errorMessage = :errorMessage,
            s.status = 'ERROR',
            s.updatedAt = CURRENT_TIMESTAMP
        WHERE s.id = :id
    """)
    int setError(@Param("id") Long id, @Param("errorMessage") String errorMessage);
}