package com.im.repository;

import com.im.entity.ViewOnceMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 一次性媒体消息数据访问层
 */
@Repository
public interface ViewOnceMessageRepository extends JpaRepository<ViewOnceMessageEntity, Long> {
    
    /**
     * 根据消息ID查找
     */
    Optional<ViewOnceMessageEntity> findByMessageId(String messageId);
    
    /**
     * 根据消息ID和接收者查找
     */
    Optional<ViewOnceMessageEntity> findByMessageIdAndReceiverId(String messageId, String receiverId);
    
    /**
     * 查找会话的所有一次性媒体消息
     */
    List<ViewOnceMessageEntity> findByConversationIdAndActiveTrueOrderByCreatedAtDesc(String conversationId);
    
    /**
     * 查找用户收到的所有一次性媒体消息
     */
    List<ViewOnceMessageEntity> findByReceiverIdAndActiveTrueOrderByCreatedAtDesc(String receiverId);
    
    /**
     * 查找发送者发送的所有一次性媒体消息
     */
    List<ViewOnceMessageEntity> findBySenderIdAndActiveTrueOrderByCreatedAtDesc(String senderId);
    
    /**
     * 查找未查看的一次性媒体消息
     */
    List<ViewOnceMessageEntity> findByReceiverIdAndViewedFalseAndActiveTrueOrderByCreatedAtDesc(String receiverId);
    
    /**
     * 查找已查看但未销毁的消息
     */
    List<ViewOnceMessageEntity> findByViewedTrueAndDestroyedFalseAndActiveTrue();
    
    /**
     * 查找已过期的消息
     */
    List<ViewOnceMessageEntity> findByExpireAtBeforeAndActiveTrue(LocalDateTime dateTime);
    
    /**
     * 批量标记消息为已查看
     */
    @Modifying
    @Query("UPDATE ViewOnceMessageEntity v SET v.viewed = true, v.viewedAt = :viewedAt, " +
           "v.viewedByIp = :ip, v.viewedByDeviceId = :deviceId WHERE v.messageId IN :messageIds")
    int markAsViewed(@Param("messageIds") List<String> messageIds, 
                     @Param("viewedAt") LocalDateTime viewedAt,
                     @Param("ip") String ip, 
                     @Param("deviceId") String deviceId);
    
    /**
     * 批量标记消息为已销毁
     */
    @Modifying
    @Query("UPDATE ViewOnceMessageEntity v SET v.destroyed = true, v.destroyedAt = :destroyedAt, " +
           "v.destroyReason = :reason, v.active = false WHERE v.messageId IN :messageIds")
    int markAsDestroyed(@Param("messageIds") List<String> messageIds,
                       @Param("destroyedAt") LocalDateTime destroyedAt,
                       @Param("reason") String reason);
    
    /**
     * 统计用户收到的一次性媒体消息数量
     */
    long countByReceiverIdAndActiveTrue(String receiverId);
    
    /**
     * 统计用户已查看的一次性媒体消息数量
     */
    long countByReceiverIdAndViewedTrueAndActiveTrue(String receiverId);
    
    /**
     * 统计会话中的一次性媒体消息数量
     */
    long countByConversationIdAndActiveTrue(String conversationId);
    
    /**
     * 统计会话中未查看的一次性媒体消息数量
     */
    long countByConversationIdAndViewedFalseAndActiveTrue(String conversationId);
    
    /**
     * 删除会话的所有一次性媒体消息记录
     */
    @Modifying
    @Query("DELETE FROM ViewOnceMessageEntity v WHERE v.conversationId = :conversationId")
    int deleteByConversationId(@Param("conversationId") String conversationId);
    
    /**
     * 删除超过一定时间的已销毁消息记录
     */
    @Modifying
    @Query("DELETE FROM ViewOnceMessageEntity v WHERE v.destroyed = true AND v.destroyedAt < :before")
    int deleteDestroyedMessagesBefore(@Param("before") LocalDateTime before);
    
    /**
     * 检查消息是否已被查看
     */
    boolean existsByMessageIdAndViewedTrue(String messageId);
    
    /**
     * 检查消息是否已激活
     */
    boolean existsByMessageIdAndActiveTrue(String messageId);
    
    /**
     * 查找特定时间范围内创建的消息
     */
    List<ViewOnceMessageEntity> findByCreatedAtBetweenAndActiveTrue(LocalDateTime start, LocalDateTime end);
    
    /**
     * 查找特定媒体类型的消息
     */
    List<ViewOnceMessageEntity> findByMediaTypeAndActiveTrueOrderByCreatedAtDesc(String mediaType);
    
    /**
     * 查找启用截图检测的消息
     */
    @Query("SELECT v FROM ViewOnceMessageEntity v WHERE v.screenshotDetection = true " +
           "AND v.viewed = true AND v.destroyed = false AND v.active = true")
    List<ViewOnceMessageEntity> findScreenshotMonitorEnabled();
}
