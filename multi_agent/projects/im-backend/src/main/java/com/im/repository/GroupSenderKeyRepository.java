package com.im.repository;

import com.im.entity.GroupSenderKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 群组Sender Key数据访问层
 * Signal Protocol群组加密的数据存储
 */
@Repository
public interface GroupSenderKeyRepository extends JpaRepository<GroupSenderKeyEntity, Long> {
    
    // ==================== 基础查询 ====================
    
    /**
     * 根据Sender Key ID查找
     */
    Optional<GroupSenderKeyEntity> findBySenderKeyId(String senderKeyId);
    
    /**
     * 根据群组ID查找所有Sender Key记录
     */
    List<GroupSenderKeyEntity> findByGroupId(String groupId);
    
    /**
     * 根据群组ID和状态查找
     */
    List<GroupSenderKeyEntity> findByGroupIdAndSenderKeyStatus(String groupId, String status);
    
    /**
     * 根据发送者ID查找其分发的所有Sender Key
     */
    List<GroupSenderKeyEntity> findBySenderId(String senderId);
    
    /**
     * 根据接收者ID查找其持有的所有Sender Key
     */
    List<GroupSenderKeyEntity> findByReceiverId(String receiverId);
    
    /**
     * 根据群组ID和接收者ID查找
     */
    List<GroupSenderKeyEntity> findByGroupIdAndReceiverId(String groupId, String receiverId);
    
    /**
     * 根据群组ID和发送者ID查找
     */
    List<GroupSenderKeyEntity> findByGroupIdAndSenderId(String groupId, String senderId);
    
    // ==================== 特定用户-群组查询 ====================
    
    /**
     * 查找特定发送者给特定接收者的Sender Key
     */
    Optional<GroupSenderKeyEntity> findBySenderKeyIdContaining(String groupId, String senderId, String receiverId);
    
    /**
     * 查找群组成员（发送者）发给某个接收者的所有Sender Key
     */
    List<GroupSenderKeyEntity> findByGroupIdAndSenderIdAndReceiverId(String groupId, String senderId, String receiverId);
    
    /**
     * 查找某个用户在群组中作为接收者的所有Sender Key
     */
    List<GroupSenderKeyEntity> findByGroupIdAndReceiverIdAndSenderKeyStatus(
        String groupId, String receiverId, String status);
    
    // ==================== 状态与有效性查询 ====================
    
    /**
     * 查找所有激活的Sender Key
     */
    List<GroupSenderKeyEntity> findBySenderKeyStatusAndAcknowledgedTrue(String status);
    
    /**
     * 查找某个群组中所有激活的Sender Key
     */
    @Query("SELECT g FROM GroupSenderKeyEntity g WHERE g.groupId = :groupId " +
           "AND g.senderKeyStatus = 'ACTIVE' AND g.acknowledged = true")
    List<GroupSenderKeyEntity> findActiveByGroupId(@Param("groupId") String groupId);
    
    /**
     * 查找某个发送者在群组中所有接收者的Sender Key
     */
    List<GroupSenderKeyEntity> findByGroupIdAndSenderIdAndSenderKeyStatus(
        String groupId, String senderId, String status);
    
    /**
     * 查找所有需要轮换的Sender Key
     */
    @Query("SELECT g FROM GroupSenderKeyEntity g WHERE g.futureSecrecyEnabled = true " +
           "AND g.nextRotationTime IS NOT NULL AND g.nextRotationTime < :now " +
           "AND g.senderKeyStatus = 'ACTIVE'")
    List<GroupSenderKeyEntity> findKeysNeedingRotation(@Param("now") LocalDateTime now);
    
    /**
     * 查找所有已过期的Sender Key
     */
    @Query("SELECT g FROM GroupSenderKeyEntity g WHERE g.expireAt IS NOT NULL " +
           "AND g.expireAt < :now AND g.senderKeyStatus != 'EXPIRED'")
    List<GroupSenderKeyEntity> findExpiredKeys(@Param("now") LocalDateTime now);
    
    /**
     * 查找所有未确认的Sender Key
     */
    List<GroupSenderKeyEntity> findByAcknowledgedFalse();
    
    /**
     * 查找群组中所有未确认的Sender Key
     */
    List<GroupSenderKeyEntity> findByGroupIdAndAcknowledgedFalse(String groupId);
    
    // ==================== 密钥版本查询 ====================
    
    /**
     * 查找发送者的最新版本Sender Key
     */
    @Query("SELECT g FROM GroupSenderKeyEntity g WHERE g.groupId = :groupId " +
           "AND g.senderId = :senderId AND g.senderKeyStatus IN ('ACTIVE', 'RATCHETING') " +
           "ORDER BY g.keyVersion DESC")
    List<GroupSenderKeyEntity> findLatestBySender(@Param("groupId") String groupId, 
                                                  @Param("senderId") String senderId);
    
    /**
     * 查找发送者发给接收者的最新版本Sender Key
     */
    @Query("SELECT g FROM GroupSenderKeyEntity g WHERE g.groupId = :groupId " +
           "AND g.senderId = :senderId AND g.receiverId = :receiverId " +
           "AND g.senderKeyStatus IN ('ACTIVE', 'RATCHETING') " +
           "ORDER BY g.keyVersion DESC")
    Optional<GroupSenderKeyEntity> findLatestBySenderAndReceiver(
        @Param("groupId") String groupId,
        @Param("senderId") String senderId,
        @Param("receiverId") String receiverId);
    
    // ==================== 统计查询 ====================
    
    /**
     * 统计群组中的Sender Key数量
     */
    long countByGroupId(String groupId);
    
    /**
     * 统计群组中激活的Sender Key数量
     */
    long countByGroupIdAndSenderKeyStatus(String groupId, String status);
    
    /**
     * 统计用户作为发送者分发的Sender Key数量
     */
    long countBySenderId(String senderId);
    
    /**
     * 统计用户作为接收者持有的Sender Key数量
     */
    long countByReceiverId(String receiverId);
    
    /**
     * 统计群组成员数量（基于唯一的发送者）
     */
    @Query("SELECT COUNT(DISTINCT g.senderId) FROM GroupSenderKeyEntity g WHERE g.groupId = :groupId")
    long countDistinctSendersInGroup(@Param("groupId") String groupId);
    
    // ==================== 更新操作 ====================
    
    /**
     * 批量激活Sender Key
     */
    @Modifying
    @Query("UPDATE GroupSenderKeyEntity g SET g.senderKeyStatus = 'ACTIVE', g.acknowledged = true, " +
           "g.acknowledgedAt = :now WHERE g.senderKeyId IN :senderKeyIds")
    int activateSenderKeys(@Param("senderKeyIds") List<String> senderKeyIds, @Param("now") LocalDateTime now);
    
    /**
     * 批量确认Sender Key
     */
    @Modifying
    @Query("UPDATE GroupSenderKeyEntity g SET g.acknowledged = true, g.distributionStatus = 'ACKNOWLEDGED', " +
           "g.acknowledgedAt = :now WHERE g.senderKeyId IN :senderKeyIds")
    int acknowledgeSenderKeys(@Param("senderKeyIds") List<String> senderKeyIds, @Param("now") LocalDateTime now);
    
    /**
     * 更新链密钥
     */
    @Modifying
    @Query("UPDATE GroupSenderKeyEntity g SET g.chainKey = :chainKey, g.chainKeyIndex = :chainKeyIndex, " +
           "g.updatedAt = :now WHERE g.senderKeyId = :senderKeyId")
    int updateChainKey(@Param("senderKeyId") String senderKeyId,
                      @Param("chainKey") String chainKey,
                      @Param("chainKeyIndex") Long chainKeyIndex,
                      @Param("now") LocalDateTime now);
    
    /**
     * 标记Sender Key为过期
     */
    @Modifying
    @Query("UPDATE GroupSenderKeyEntity g SET g.senderKeyStatus = 'EXPIRED', g.updatedAt = :now " +
           "WHERE g.senderKeyId IN :senderKeyIds")
    int expireSenderKeys(@Param("senderKeyIds") List<String> senderKeyIds, @Param("now") LocalDateTime now);
    
    /**
     * 标记Sender Key为已撤销
     */
    @Modifying
    @Query("UPDATE GroupSenderKeyEntity g SET g.senderKeyStatus = 'REVOKED', g.updatedAt = :now " +
           "WHERE g.senderKeyId IN :senderKeyIds")
    int revokeSenderKeys(@Param("senderKeyIds") List<String> senderKeyIds, @Param("now") LocalDateTime now);
    
    /**
     * 批量更新Sender Key版本
     */
    @Modifying
    @Query("UPDATE GroupSenderKeyEntity g SET g.senderKeyStatus = 'REPLACED', g.updatedAt = :now " +
           "WHERE g.groupId = :groupId AND g.senderId = :senderId AND g.keyVersion < :newVersion")
    int replaceOldVersions(@Param("groupId") String groupId,
                          @Param("senderId") String senderId,
                          @Param("newVersion") Integer newVersion,
                          @Param("now") LocalDateTime now);
    
    // ==================== 删除操作 ====================
    
    /**
     * 删除群组的所有Sender Key
     */
    @Modifying
    @Query("DELETE FROM GroupSenderKeyEntity g WHERE g.groupId = :groupId")
    int deleteByGroupId(@Param("groupId") String groupId);
    
    /**
     * 删除发送者的所有Sender Key
     */
    @Modifying
    @Query("DELETE FROM GroupSenderKeyEntity g WHERE g.senderId = :senderId")
    int deleteBySenderId(@Param("senderId") String senderId);
    
    /**
     * 删除接收者的所有Sender Key
     */
    @Modifying
    @Query("DELETE FROM GroupSenderKeyEntity g WHERE g.receiverId = :receiverId")
    int deleteByReceiverId(@Param("receiverId") String receiverId);
    
    /**
     * 删除群组中发送者的所有Sender Key
     */
    @Modifying
    @Query("DELETE FROM GroupSenderKeyEntity g WHERE g.groupId = :groupId AND g.senderId = :senderId")
    int deleteByGroupIdAndSenderId(@Param("groupId") String groupId, @Param("senderId") String senderId);
    
    /**
     * 删除超过一定时间的已过期Sender Key
     */
    @Modifying
    @Query("DELETE FROM GroupSenderKeyEntity g WHERE g.updatedAt < :before " +
           "AND g.senderKeyStatus IN ('EXPIRED', 'REVOKED', 'REPLACED')")
    int deleteStaleKeys(@Param("before") LocalDateTime before);
    
    // ==================== 存在性检查 ====================
    
    /**
     * 检查Sender Key是否存在
     */
    boolean existsBySenderKeyId(String senderKeyId);
    
    /**
     * 检查Sender Key是否已激活
     */
    boolean existsBySenderKeyIdAndSenderKeyStatusAndAcknowledgedTrue(String senderKeyId, String status);
    
    /**
     * 检查群组中是否存在有效的Sender Key
     */
    @Query("SELECT COUNT(g) > 0 FROM GroupSenderKeyEntity g WHERE g.groupId = :groupId " +
           "AND g.senderKeyStatus = 'ACTIVE' AND g.acknowledged = true")
    boolean existsActiveKeyInGroup(@Param("groupId") String groupId);
}
