package com.im.system.repository;

import com.im.system.entity.ClientEncryptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 客户端加密本地存储仓储接口
 */
@Repository
public interface ClientEncryptionRepository extends JpaRepository<ClientEncryptionEntity, Long> {
    
    /**
     * 根据用户 ID 查找加密配置
     */
    Optional<ClientEncryptionEntity> findByUserId(Long userId);
    
    /**
     * 检查用户是否启用加密
     */
    @Query("SELECT e.encryptionEnabled FROM ClientEncryptionEntity e WHERE e.userId = :userId")
    Optional<Boolean> findEncryptionEnabledByUserId(@Param("userId") Long userId);
    
    /**
     * 查找所有启用加密的用户
     */
    List<ClientEncryptionEntity> findByEncryptionEnabledTrue();
    
    /**
     * 查找需要同步的加密配置
     */
    List<ClientEncryptionEntity> findBySyncStatus(String syncStatus);
    
    /**
     * 查找密钥即将过期的用户
     */
    @Query("SELECT e FROM ClientEncryptionEntity e WHERE e.keyExpiresAt IS NOT NULL AND e.keyExpiresAt > :now AND e.keyExpiresAt < :threshold")
    List<ClientEncryptionEntity> findExpiringKeys(@Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);
    
    /**
     * 查找未备份的用户
     */
    List<ClientEncryptionEntity> findByBackedUpFalse();
    
    /**
     * 更新同步状态
     */
    @Modifying
    @Query("UPDATE ClientEncryptionEntity e SET e.syncStatus = :status, e.lastSyncAt = :syncedAt WHERE e.userId = :userId")
    int updateSyncStatus(@Param("userId") Long userId, @Param("status") String status, @Param("syncedAt") LocalDateTime syncedAt);
    
    /**
     * 更新加密统计
     */
    @Modifying
    @Query("UPDATE ClientEncryptionEntity e SET e.encryptedMessageCount = e.encryptedMessageCount + :count, e.updatedAt = :updatedAt WHERE e.userId = :userId")
    int incrementEncryptedCount(@Param("userId") Long userId, @Param("count") Long count, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 更新解密统计
     */
    @Modifying
    @Query("UPDATE ClientEncryptionEntity e SET e.decryptedMessageCount = e.decryptedMessageCount + :count, e.updatedAt = :updatedAt WHERE e.userId = :userId")
    int incrementDecryptedCount(@Param("userId") Long userId, @Param("count") Long count, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 标记为已备份
     */
    @Modifying
    @Query("UPDATE ClientEncryptionEntity e SET e.backedUp = true, e.backupKeyCreatedAt = :backupAt WHERE e.userId = :userId")
    int markAsBackedUp(@Param("userId") Long userId, @Param("backupAt") LocalDateTime backupAt);
    
    /**
     * 删除用户的加密配置
     */
    @Modifying
    @Query("DELETE FROM ClientEncryptionEntity e WHERE e.userId = :userId")
    int deleteByUserId(@Param("userId") Long userId);
    
    /**
     * 统计启用加密的用户数
     */
    @Query("SELECT COUNT(e) FROM ClientEncryptionEntity e WHERE e.encryptionEnabled = true")
    Long countEnabledUsers();
    
    /**
     * 统计总加密消息数
     */
    @Query("SELECT SUM(e.encryptedMessageCount) FROM ClientEncryptionEntity e")
    Long sumEncryptedMessages();
    
    /**
     * 查找特定加密算法的用户
     */
    List<ClientEncryptionEntity> findByEncryptionAlgorithm(String algorithm);
    
    /**
     * 查找密钥版本过期的用户
     */
    @Query("SELECT e FROM ClientEncryptionEntity e WHERE e.keyVersion < :minVersion")
    List<ClientEncryptionEntity> findOutdatedKeyVersion(@Param("minVersion") Integer minVersion);
    
    /**
     * 查找最后同步时间早于指定时间的用户
     */
    @Query("SELECT e FROM ClientEncryptionEntity e WHERE e.lastSyncAt < :threshold")
    List<ClientEncryptionEntity> findStaleSync(@Param("threshold") LocalDateTime threshold);
}