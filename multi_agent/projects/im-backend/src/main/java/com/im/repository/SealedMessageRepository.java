package com.im.repository;

import com.im.entity.SealedMessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * SealedMessageRepository - 密封消息仓储层
 * 
 * 提供密封消息的持久化操作，包括：
 * - 消息存储和查询
 * - 哈希去重检查
 * - 状态更新
 * - 过期消息清理
 */
@Repository
public interface SealedMessageRepository extends MongoRepository<SealedMessageEntity, String> {
    
    /**
     * 根据消息哈希查找（用于去重检查）
     */
    Optional<SealedMessageEntity> findByMessageHash(String messageHash);
    
    /**
     * 检查消息哈希是否存在（快速去重）
     */
    boolean existsByMessageHash(String messageHash);
    
    /**
     * 根据接收方ID查询待投递消息
     */
    List<SealedMessageEntity> findByRecipientIdAndDeliveryStatus(
        String recipientId, 
        String deliveryStatus
    );
    
    /**
     * 根据接收方ID查询所有消息（分页）
     */
    List<SealedMessageEntity> findByRecipientIdOrderByCreatedAtDesc(
        String recipientId
    );
    
    /**
     * 根据接收方ID和设备ID查询消息
     */
    List<SealedMessageEntity> findByRecipientIdAndRecipientDeviceIdOrderByCreatedAtDesc(
        String recipientId,
        String recipientDeviceId
    );
    
    /**
     * 根据会话ID查询消息历史
     */
    List<SealedMessageEntity> findByConversationIdOrderByCreatedAtDesc(
        String conversationId
    );
    
    /**
     * 查询特定时间范围内的消息
     */
    @Query("{ 'recipientId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
    List<SealedMessageEntity> findByRecipientIdAndTimeRange(
        String recipientId,
        Instant startTime,
        Instant endTime
    );
    
    /**
     * 查找过期的密封消息
     */
    List<SealedMessageEntity> findByExpiresAtBeforeAndDeliveryStatusNot(
        Instant now,
        String excludeStatus
    );
    
    /**
     * 统计接收方的未读密封消息数量
     */
    long countByRecipientIdAndDeliveryStatus(String recipientId, String deliveryStatus);
    
    /**
     * 删除所有过期消息
     */
    void deleteByExpiresAtBefore(Instant expirationTime);
    
    /**
     * 根据会话和状态查询消息
     */
    List<SealedMessageEntity> findByConversationIdAndDeliveryStatusOrderByCreatedAtDesc(
        String conversationId,
        String deliveryStatus
    );
    
    /**
     * 更新消息投递状态
     */
    @Query("{ 'id': ?0 }")
    void updateDeliveryStatus(String messageId, String newStatus);
    
    /**
     * 根据序列号范围查询消息（用于消息同步）
     */
    @Query("{ 'recipientId': ?0, 'sequenceNumber': { $gt: ?1, $lte: ?2 } }")
    List<SealedMessageEntity> findByRecipientIdAndSequenceRange(
        String recipientId,
        Long startSequence,
        Long endSequence
    );
    
    /**
     * 查找最近的密封消息
     */
    List<SealedMessageEntity> findTop50ByRecipientIdOrderByCreatedAtDesc(String recipientId);
    
    /**
     * 根据密封密钥指纹查找消息
     */
    List<SealedMessageEntity> findBySealedKeyFingerprintOrderByCreatedAtDesc(
        String sealedKeyFingerprint
    );
}
