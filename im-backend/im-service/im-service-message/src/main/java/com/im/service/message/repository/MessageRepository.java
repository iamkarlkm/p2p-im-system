package com.im.service.message.repository;

import com.im.service.message.entity.Message;
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
 * 消息数据访问接口
 * 
 * @author IM Team
 * @version 1.0
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, String> {

    // ========== 基础查询方法 ==========

    /**
     * 根据ID查询未删除的消息
     */
    @Query("SELECT m FROM Message m WHERE m.id = :id AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false))")
    Optional<Message> findByIdAndUserId(@Param("id") String id, @Param("userId") String userId);

    /**
     * 查询单条消息(不考虑删除状态)
     */
    Optional<Message> findById(String id);

    /**
     * 根据客户端消息ID查询(用于去重)
     */
    Optional<Message> findByClientMessageId(String clientMessageId);

    // ========== 会话消息查询 ==========

    /**
     * 分页查询会话消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false)) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findByConversationIdAndUserId(
            @Param("conversationId") String conversationId, 
            @Param("userId") String userId,
            Pageable pageable);

    /**
     * 查询会话消息(不带分页)
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false)) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findByConversationIdAndUserId(
            @Param("conversationId") String conversationId, 
            @Param("userId") String userId);

    /**
     * 查询会话中指定时间之后的消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId AND " +
           "m.createdAt > :since AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false)) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findByConversationIdSince(
            @Param("conversationId") String conversationId,
            @Param("since") LocalDateTime since,
            @Param("userId") String userId);

    /**
     * 查询指定时间范围内的消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId AND " +
           "m.createdAt BETWEEN :startTime AND :endTime AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false)) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findByConversationIdAndTimeRange(
            @Param("conversationId") String conversationId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("userId") String userId);

    // ========== 发送者/接收者查询 ==========

    /**
     * 查询用户发送的消息
     */
    Page<Message> findBySenderIdAndSenderDeletedFalseOrderByCreatedAtDesc(
            String senderId, Pageable pageable);

    /**
     * 查询用户接收的消息
     */
    Page<Message> findByReceiverIdAndReceiverDeletedFalseOrderByCreatedAtDesc(
            String receiverId, Pageable pageable);

    /**
     * 查询两个用户之间的消息(私聊)
     */
    @Query("SELECT m FROM Message m WHERE " +
           "((m.senderId = :userId1 AND m.receiverId = :userId2) OR " +
           "(m.senderId = :userId2 AND m.receiverId = :userId1)) AND " +
           "m.senderDeleted = false AND m.receiverDeleted = false " +
           "ORDER BY m.createdAt DESC")
    Page<Message> findPrivateChatMessages(
            @Param("userId1") String userId1,
            @Param("userId2") String userId2,
            Pageable pageable);

    // ========== 状态更新方法 ==========

    /**
     * 更新消息状态
     */
    @Modifying
    @Query("UPDATE Message m SET m.status = :status, m.updatedAt = :now WHERE m.id = :id")
    int updateStatus(
            @Param("id") String id, 
            @Param("status") String status, 
            @Param("now") LocalDateTime now);

    /**
     * 标记消息为已发送
     */
    @Modifying
    @Query("UPDATE Message m SET m.status = 'SENT', m.sentAt = :now, m.updatedAt = :now WHERE m.id = :id")
    int markAsSent(@Param("id") String id, @Param("now") LocalDateTime now);

    /**
     * 标记消息为已送达
     */
    @Modifying
    @Query("UPDATE Message m SET m.status = 'DELIVERED', m.deliveredAt = :now, m.updatedAt = :now WHERE m.id = :id")
    int markAsDelivered(@Param("id") String id, @Param("now") LocalDateTime now);

    /**
     * 标记消息为已读
     */
    @Modifying
    @Query("UPDATE Message m SET m.status = 'READ', m.readAt = :now, m.lastReadAt = :now, m.updatedAt = :now WHERE m.id = :id")
    int markAsRead(@Param("id") String id, @Param("now") LocalDateTime now);

    /**
     * 标记会话中所有消息为已读
     */
    @Modifying
    @Query("UPDATE Message m SET m.status = 'READ', m.readAt = :now, m.lastReadAt = :now, m.updatedAt = :now " +
           "WHERE m.conversationId = :conversationId AND m.receiverId = :userId AND m.status != 'READ'")
    int markConversationAsRead(
            @Param("conversationId") String conversationId,
            @Param("userId") String userId,
            @Param("now") LocalDateTime now);

    // ========== 撤回相关方法 ==========

    /**
     * 撤回消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.recalled = true, m.recalledAt = :now, m.recalledBy = :userId, " +
           "m.content = '[消息已撤回]', m.status = 'RECALLED', m.updatedAt = :now WHERE m.id = :id")
    int recallMessage(
            @Param("id") String id, 
            @Param("userId") String userId,
            @Param("now") LocalDateTime now);

    // ========== 删除相关方法 ==========

    /**
     * 发送者删除消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.senderDeleted = true, m.senderDeletedAt = :now, m.updatedAt = :now WHERE m.id = :id")
    int deleteBySender(@Param("id") String id, @Param("now") LocalDateTime now);

    /**
     * 接收者删除消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.receiverDeleted = true, m.receiverDeletedAt = :now, m.updatedAt = :now WHERE m.id = :id")
    int deleteByReceiver(@Param("id") String id, @Param("now") LocalDateTime now);

    /**
     * 物理删除已被双方删除的消息
     */
    @Modifying
    @Query("DELETE FROM Message m WHERE m.senderDeleted = true AND m.receiverDeleted = true")
    int deleteFullyDeletedMessages();

    // ========== 置顶相关方法 ==========

    /**
     * 置顶消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.pinned = true, m.pinnedAt = :now, m.pinnedBy = :userId, m.updatedAt = :now WHERE m.id = :id")
    int pinMessage(
            @Param("id") String id, 
            @Param("userId") String userId,
            @Param("now") LocalDateTime now);

    /**
     * 取消置顶消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.pinned = false, m.pinnedAt = null, m.pinnedBy = null, m.updatedAt = :now WHERE m.id = :id")
    int unpinMessage(@Param("id") String id, @Param("now") LocalDateTime now);

    /**
     * 查询会话中置顶的消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId AND m.pinned = true AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false)) " +
           "ORDER BY m.pinnedAt DESC")
    List<Message> findPinnedMessages(
            @Param("conversationId") String conversationId,
            @Param("userId") String userId);

    // ========== 收藏相关方法 ==========

    /**
     * 收藏消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.favorited = true, m.favoritedAt = :now, m.updatedAt = :now WHERE m.id = :id AND m.senderId = :userId")
    int favoriteMessage(@Param("id") String id, @Param("userId") String userId, @Param("now") LocalDateTime now);

    /**
     * 取消收藏消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.favorited = false, m.favoritedAt = null, m.updatedAt = :now WHERE m.id = :id AND m.senderId = :userId")
    int unfavoriteMessage(@Param("id") String id, @Param("userId") String userId, @Param("now") LocalDateTime now);

    /**
     * 查询用户收藏的消息
     */
    Page<Message> findBySenderIdAndFavoritedTrueAndSenderDeletedFalseOrderByFavoritedAtDesc(
            String senderId, Pageable pageable);

    // ========== 搜索相关方法 ==========

    /**
     * 搜索消息内容
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId AND " +
           "(m.content LIKE %:keyword% OR m.contentSummary LIKE %:keyword%) AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false)) " +
           "ORDER BY m.createdAt DESC")
    List<Message> searchMessages(
            @Param("conversationId") String conversationId,
            @Param("keyword") String keyword,
            @Param("userId") String userId);

    /**
     * 全局搜索用户的消息
     */
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId OR m.receiverId = :userId) AND " +
           "(m.content LIKE %:keyword% OR m.contentSummary LIKE %:keyword%) AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false)) " +
           "ORDER BY m.createdAt DESC")
    Page<Message> searchUserMessages(
            @Param("userId") String userId,
            @Param("keyword") String keyword,
            Pageable pageable);

    // ========== 统计相关方法 ==========

    /**
     * 统计会话消息数量
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversationId = :conversationId AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false))")
    long countByConversationId(@Param("conversationId") String conversationId, @Param("userId") String userId);

    /**
     * 统计用户发送的消息数
     */
    long countBySenderIdAndSenderDeletedFalse(String senderId);

    /**
     * 统计用户接收的消息数
     */
    long countByReceiverIdAndReceiverDeletedFalse(String receiverId);

    /**
     * 统计会话中指定时间之后的消息数
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversationId = :conversationId AND " +
           "m.createdAt > :since AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false))")
    long countByConversationSince(
            @Param("conversationId") String conversationId,
            @Param("since") LocalDateTime since,
            @Param("userId") String userId);

    /**
     * 统计未读消息数
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiverId = :userId AND " +
           "m.status IN ('SENT', 'DELIVERED') AND m.receiverDeleted = false")
    long countUnreadByReceiver(@Param("userId") String userId);

    /**
     * 统计会话未读消息数
     */
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversationId = :conversationId AND " +
           "m.receiverId = :userId AND m.status IN ('SENT', 'DELIVERED') AND m.receiverDeleted = false")
    long countUnreadByConversation(@Param("conversationId") String conversationId, @Param("userId") String userId);

    // ========== 阅后即焚相关方法 ==========

    /**
     * 销毁阅后即焚消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.destroyed = true, m.destroyedAt = :now, m.content = '[消息已销毁]', " +
           "m.attachments = null, m.updatedAt = :now WHERE m.id = :id AND m.selfDestruct = true")
    int destroySelfDestructMessage(@Param("id") String id, @Param("now") LocalDateTime now);

    /**
     * 查询需要销毁的阅后即焚消息
     */
    @Query("SELECT m FROM Message m WHERE m.selfDestruct = true AND m.destroyed = false AND " +
           "m.createdAt < :expireTime")
    List<Message> findExpiredSelfDestructMessages(@Param("expireTime") LocalDateTime expireTime);

    // ========== 编辑相关方法 ==========

    /**
     * 编辑消息
     */
    @Modifying
    @Query("UPDATE Message m SET m.content = :content, m.edited = true, m.editedAt = :now, " +
           "m.originalContent = m.content, m.updatedAt = :now WHERE m.id = :id")
    int editMessage(
            @Param("id") String id,
            @Param("content") String content,
            @Param("now") LocalDateTime now);

    // ========== 批量操作方法 ==========

    /**
     * 批量标记消息为已读
     */
    @Modifying
    @Query("UPDATE Message m SET m.status = 'READ', m.readAt = :now, m.updatedAt = :now " +
           "WHERE m.id IN :ids AND m.status != 'READ'")
    int batchMarkAsRead(@Param("ids") List<String> ids, @Param("now") LocalDateTime now);

    /**
     * 批量删除消息(发送者视角)
     */
    @Modifying
    @Query("UPDATE Message m SET m.senderDeleted = true, m.senderDeletedAt = :now, m.updatedAt = :now " +
           "WHERE m.id IN :ids")
    int batchDeleteBySender(@Param("ids") List<String> ids, @Param("now") LocalDateTime now);

    // ========== 最新消息查询 ==========

    /**
     * 查询会话最新一条消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false)) " +
           "ORDER BY m.createdAt DESC")
    List<Message> findLatestMessage(
            @Param("conversationId") String conversationId,
            @Param("userId") String userId,
            Pageable pageable);

    /**
     * 查询会话第一条消息
     */
    @Query("SELECT m FROM Message m WHERE m.conversationId = :conversationId AND " +
           "((m.senderId = :userId AND m.senderDeleted = false) OR " +
           "(m.receiverId = :userId AND m.receiverDeleted = false)) " +
           "ORDER BY m.createdAt ASC")
    List<Message> findFirstMessage(
            @Param("conversationId") String conversationId,
            @Param("userId") String userId,
            Pageable pageable);
}
