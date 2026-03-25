package com.im.system.repository;

import com.im.system.entity.MessageDraftEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息草稿跨设备同步仓储接口
 * 提供草稿的CRUD操作和同步相关查询
 */
@Repository
public interface MessageDraftRepository extends JpaRepository<MessageDraftEntity, Long> {
    
    /**
     * 根据用户ID和会话ID查找草稿
     */
    Optional<MessageDraftEntity> findByUserIdAndConversationId(Long userId, String conversationId);
    
    /**
     * 根据用户ID、设备ID和会话ID查找草稿
     */
    Optional<MessageDraftEntity> findByUserIdAndDeviceIdAndConversationId(Long userId, String deviceId, String conversationId);
    
    /**
     * 查找用户的所有草稿
     */
    List<MessageDraftEntity> findByUserId(Long userId);
    
    /**
     * 查找用户的活跃草稿
     */
    List<MessageDraftEntity> findByUserIdAndActiveTrue(Long userId);
    
    /**
     * 查找用户指定设备上的草稿
     */
    List<MessageDraftEntity> findByUserIdAndDeviceId(Long userId, String deviceId);
    
    /**
     * 查找需要同步的草稿（同步状态为PENDING）
     */
    List<MessageDraftEntity> findByUserIdAndSyncStatus(String userId, String syncStatus);
    
    /**
     * 查找有冲突的草稿
     */
    List<MessageDraftEntity> findByUserIdAndSyncStatusOrderByLastUpdatedAtDesc(Long userId, String syncStatus);
    
    /**
     * 根据同步状态查询草稿数量
     */
    Long countByUserIdAndSyncStatus(Long userId, String syncStatus);
    
    /**
     * 删除用户的指定草稿
     */
    @Modifying
    @Query("DELETE FROM MessageDraftEntity d WHERE d.userId = :userId AND d.conversationId = :conversationId")
    int deleteByUserAndConversation(@Param("userId") Long userId, @Param("conversationId") String conversationId);
    
    /**
     * 删除用户设备上的所有草稿
     */
    @Modifying
    @Query("DELETE FROM MessageDraftEntity d WHERE d.userId = :userId AND d.deviceId = :deviceId")
    int deleteByUserAndDevice(@Param("userId") Long userId, @Param("deviceId") String deviceId);
    
    /**
     * 清除用户的所有草稿
     */
    @Modifying
    @Query("DELETE FROM MessageDraftEntity d WHERE d.userId = :userId")
    int deleteAllByUser(@Param("userId") Long userId);
    
    /**
     * 更新草稿的同步状态
     */
    @Modifying
    @Query("UPDATE MessageDraftEntity d SET d.syncStatus = :syncStatus, d.lastUpdatedAt = :updatedAt WHERE d.id = :id")
    int updateSyncStatus(@Param("id") Long id, @Param("syncStatus") String syncStatus, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 批量更新草稿的同步状态
     */
    @Modifying
    @Query("UPDATE MessageDraftEntity d SET d.syncStatus = :syncStatus, d.lastUpdatedAt = :updatedAt WHERE d.userId = :userId AND d.conversationId = :conversationId")
    int batchUpdateSyncStatus(@Param("userId") Long userId, @Param("conversationId") String conversationId, 
                              @Param("syncStatus") String syncStatus, @Param("updatedAt") LocalDateTime updatedAt);
    
    /**
     * 解决草稿冲突
     */
    @Modifying
    @Query("UPDATE MessageDraftEntity d SET d.syncStatus = 'SYNCED', d.conflictInfo = null, d.localVersion = d.serverVersion WHERE d.id = :id AND d.syncStatus = 'CONFLICT'")
    int resolveConflict(@Param("id") Long id);
    
    /**
     * 标记草稿为清空状态
     */
    @Modifying
    @Query("UPDATE MessageDraftEntity d SET d.cleared = true, d.draftContent = '', d.syncStatus = 'PENDING' WHERE d.id = :id")
    int markAsCleared(@Param("id") Long id);
    
    /**
     * 查找最后更新的草稿
     */
    Optional<MessageDraftEntity> findFirstByUserIdAndConversationIdOrderByLastUpdatedAtDesc(Long userId, String conversationId);
    
    /**
     * 查找用户在某段时间后更新的草稿
     */
    List<MessageDraftEntity> findByUserIdAndLastUpdatedAtAfter(Long userId, LocalDateTime afterTime);
    
    /**
     * 查找不同步的草稿（服务端版本不等于本地版本）
     */
    @Query("SELECT d FROM MessageDraftEntity d WHERE d.userId = :userId AND d.localVersion != d.serverVersion")
    List<MessageDraftEntity> findOutOfSyncDrafts(@Param("userId") Long userId);
    
    /**
     * 根据草稿类型查询草稿
     */
    List<MessageDraftEntity> findByUserIdAndDraftType(Long userId, String draftType);
    
    /**
     * 查询自动保存的草稿
     */
    List<MessageDraftEntity> findByUserIdAndAutoSaveTrue(Long userId);
    
    /**
     * 查询指定设备上特定会话类型的草稿
     */
    List<MessageDraftEntity> findByUserIdAndDeviceIdAndConversationType(Long userId, String deviceId, String conversationType);
    
    /**
     * 统计用户的草稿数量
     */
    @Query("SELECT COUNT(d) FROM MessageDraftEntity d WHERE d.userId = :userId")
    Long countByUser(@Param("userId") Long userId);
    
    /**
     * 统计用户设备的草稿数量
     */
    @Query("SELECT COUNT(d) FROM MessageDraftEntity d WHERE d.userId = :userId AND d.deviceId = :deviceId")
    Long countByUserAndDevice(@Param("userId") Long userId, @Param("deviceId") String deviceId);
    
    /**
     * 查找需要清理的旧草稿（创建时间早于指定时间）
     */
    List<MessageDraftEntity> findByCreatedAtBefore(LocalDateTime beforeTime);
    
    /**
     * 批量删除旧草稿
     */
    @Modifying
    @Query("DELETE FROM MessageDraftEntity d WHERE d.createdAt < :beforeTime")
    int deleteOldDrafts(@Param("beforeTime") LocalDateTime beforeTime);
    
    /**
     * 根据用户ID和会话ID列表查询草稿
     */
    @Query("SELECT d FROM MessageDraftEntity d WHERE d.userId = :userId AND d.conversationId IN :conversationIds")
    List<MessageDraftEntity> findByUserAndConversations(@Param("userId") Long userId, @Param("conversationIds") List<String> conversationIds);
    
    /**
     * 更新草稿的活跃状态
     */
    @Modifying
    @Query("UPDATE MessageDraftEntity d SET d.active = :active WHERE d.userId = :userId AND d.deviceId = :deviceId")
    int updateActiveStatus(@Param("userId") Long userId, @Param("deviceId") String deviceId, @Param("active") Boolean active);
    
    /**
     * 查找所有有冲突的草稿（用于管理员查看）
     */
    @Query("SELECT d FROM MessageDraftEntity d WHERE d.syncStatus = 'CONFLICT' ORDER BY d.lastUpdatedAt DESC")
    List<MessageDraftEntity> findAllConflicts();
    
    /**
     * 统计系统总草稿数
     */
    @Query("SELECT COUNT(d) FROM MessageDraftEntity d")
    Long countAllDrafts();
    
    /**
     * 统计各同步状态的草稿数量
     */
    @Query("SELECT d.syncStatus, COUNT(d) FROM MessageDraftEntity d GROUP BY d.syncStatus")
    List<Object[]> countBySyncStatus();
}