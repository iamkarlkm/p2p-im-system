package com.im.server.repository;

import com.im.server.entity.ReadReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消息已读回执数据访问层
 */
@Repository
public interface ReadReceiptRepository extends JpaRepository<ReadReceipt, Long> {

    Optional<ReadReceipt> findByMessageIdAndUserId(Long messageId, Long userId);

    List<ReadReceipt> findByMessageId(Long messageId);

    List<ReadReceipt> findByConversationIdAndUserId(Long conversationId, Long userId);

    @Query("SELECT COUNT(r) FROM ReadReceipt r WHERE r.conversationId = :conversationId AND r.userId = :userId")
    long countByConversationIdAndUserId(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Query("SELECT r.messageId FROM ReadReceipt r WHERE r.conversationId = :conversationId AND r.userId = :userId ORDER BY r.readAt DESC LIMIT 1")
    Long findLastReadMessageId(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    @Query("SELECT DISTINCT r.conversationId FROM ReadReceipt r WHERE r.userId = :userId")
    List<Long> findUnreadConversations(@Param("userId") Long userId);

    @Query("SELECT r FROM ReadReceipt r WHERE r.conversationId = :conversationId AND r.userId = :userId ORDER BY r.readAt DESC")
    List<ReadReceipt> findByConversationAndUserOrderByReadAtDesc(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

    boolean existsByMessageIdAndUserId(Long messageId, Long userId);

    @Query("SELECT COUNT(r) FROM ReadReceipt r WHERE r.messageId = :messageId")
    long countByMessageId(@Param("messageId") Long messageId);

    @Query("SELECT r.userId FROM ReadReceipt r WHERE r.messageId = :messageId")
    List<Long> findUserIdsByMessageId(@Param("messageId") Long messageId);

    void deleteByConversationIdAndUserId(Long conversationId, Long userId);
}
