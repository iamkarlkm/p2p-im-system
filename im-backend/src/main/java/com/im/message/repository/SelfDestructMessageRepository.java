package com.im.message.repository;

import com.im.message.model.SelfDestructMessage;
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
 * 阅后即焚消息数据访问层
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Repository
public interface SelfDestructMessageRepository extends JpaRepository<SelfDestructMessage, String> {

    /**
     * 根据会话ID查询消息
     */
    List<SelfDestructMessage> findByConversationIdOrderByCreatedAtDesc(String conversationId);

    /**
     * 分页查询会话消息
     */
    Page<SelfDestructMessage> findByConversationIdOrderByCreatedAtDesc(String conversationId, Pageable pageable);

    /**
     * 查询发送的消息
     */
    List<SelfDestructMessage> findBySenderIdOrderByCreatedAtDesc(String senderId);

    /**
     * 查询接收的消息
     */
    List<SelfDestructMessage> findByReceiverIdOrderByCreatedAtDesc(String receiverId);

    /**
     * 查询会话中未读的消息
     */
    @Query("SELECT m FROM SelfDestructMessage m WHERE m.conversationId = :conversationId AND m.receiverId = :receiverId AND m.isRead = false AND m.isDestroyed = false")
    List<SelfDestructMessage> findUnreadByConversationAndReceiver(@Param("conversationId") String conversationId, @Param("receiverId") String receiverId);

    /**
     * 根据ID和接收者查询
     */
    Optional<SelfDestructMessage> findByIdAndReceiverId(String id, String receiverId);

    /**
     * 根据ID和发送者查询
     */
    Optional<SelfDestructMessage> findByIdAndSenderId(String id, String senderId);

    /**
     * 查询需要销毁的消息（已过期且未销毁）
     */
    @Query("SELECT m FROM SelfDestructMessage m WHERE m.isRead = true AND m.isDestroyed = false AND m.scheduledDestroyAt <= :now")
    List<SelfDestructMessage> findExpiredMessages(@Param("now") LocalDateTime now);

    /**
     * 批量销毁消息
     */
    @Modifying
    @Query("UPDATE SelfDestructMessage m SET m.isDestroyed = true, m.messageContent = null, m.destroyedAt = :now WHERE m.id IN :ids")
    int batchDestroyMessages(@Param("ids") List<String> ids, @Param("now") LocalDateTime now);

    /**
     * 查询用户的所有相关消息（发送或接收）
     */
    @Query("SELECT m FROM SelfDestructMessage m WHERE (m.senderId = :userId OR m.receiverId = :userId) AND m.isDestroyed = false ORDER BY m.createdAt DESC")
    List<SelfDestructMessage> findAllByUserId(@Param("userId") String userId);

    /**
     * 分页查询用户的消息
     */
    @Query("SELECT m FROM SelfDestructMessage m WHERE (m.senderId = :userId OR m.receiverId = :userId) AND m.isDestroyed = false ORDER BY m.createdAt DESC")
    Page<SelfDestructMessage> findAllByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * 统计未读数量
     */
    @Query("SELECT COUNT(m) FROM SelfDestructMessage m WHERE m.receiverId = :receiverId AND m.isRead = false AND m.isDestroyed = false")
    Long countUnreadByReceiverId(@Param("receiverId") String receiverId);

    /**
     * 统计会话中的未读数量
     */
    @Query("SELECT COUNT(m) FROM SelfDestructMessage m WHERE m.conversationId = :conversationId AND m.receiverId = :receiverId AND m.isRead = false AND m.isDestroyed = false")
    Long countUnreadByConversationAndReceiver(@Param("conversationId") String conversationId, @Param("receiverId") String receiverId);

    /**
     * 标记消息为已读
     */
    @Modifying
    @Query("UPDATE SelfDestructMessage m SET m.isRead = true, m.readAt = :now, m.scheduledDestroyAt = :destroyAt WHERE m.id = :id")
    int markAsRead(@Param("id") String id, @Param("now") LocalDateTime now, @Param("destroyAt") LocalDateTime destroyAt);

    /**
     * 记录截图检测
     */
    @Modifying
    @Query("UPDATE SelfDestructMessage m SET m.screenshotDetected = true, m.screenshotCount = m.screenshotCount + 1, m.screenshotDetectedAt = :now WHERE m.id = :id")
    int recordScreenshot(@Param("id") String id, @Param("now") LocalDateTime now);

    /**
     * 查询被截图的消息
     */
    @Query("SELECT m FROM SelfDestructMessage m WHERE m.senderId = :senderId AND m.screenshotDetected = true ORDER BY m.screenshotDetectedAt DESC")
    List<SelfDestructMessage> findScreenshotDetectedBySender(@Param("senderId") String senderId);

    /**
     * 删除已销毁的旧消息（清理任务用）
     */
    @Modifying
    @Query("DELETE FROM SelfDestructMessage m WHERE m.isDestroyed = true AND m.destroyedAt < :beforeDate")
    int deleteOldDestroyedMessages(@Param("beforeDate") LocalDateTime beforeDate);

    /**
     * 查询需要定时清理的消息（已销毁超过7天）
     */
    @Query("SELECT m FROM SelfDestructMessage m WHERE m.isDestroyed = true AND m.destroyedAt < :beforeDate")
    List<SelfDestructMessage> findOldDestroyedMessages(@Param("beforeDate") LocalDateTime beforeDate);
}
