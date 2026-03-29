package com.im.backend.repository;

import com.im.backend.model.MessageReaction;
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
 * 消息表情回应数据访问层
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-27
 */
@Repository
public interface MessageReactionRepository extends JpaRepository<MessageReaction, Long> {

    /**
     * 根据消息ID查询所有有效回应
     */
    List<MessageReaction> findByMessageIdAndIsDeletedFalse(Long messageId);

    /**
     * 根据消息ID和表情代码查询
     */
    List<MessageReaction> findByMessageIdAndEmojiCodeAndIsDeletedFalse(Long messageId, String emojiCode);

    /**
     * 查询用户对消息的特定表情回应
     */
    Optional<MessageReaction> findByMessageIdAndUserIdAndEmojiCodeAndIsDeletedFalse(
            Long messageId, Long userId, String emojiCode);

    /**
     * 查询用户对消息的所有回应
     */
    List<MessageReaction> findByMessageIdAndUserIdAndIsDeletedFalse(Long messageId, Long userId);

    /**
     * 统计消息的表情数量
     */
    @Query("SELECT mr.emojiCode, COUNT(mr) FROM MessageReaction mr " +
           "WHERE mr.messageId = :messageId AND mr.isDeleted = false " +
           "GROUP BY mr.emojiCode ORDER BY COUNT(mr) DESC")
    List<Object[]> countReactionsByMessageId(@Param("messageId") Long messageId);

    /**
     * 统计消息的总回应数
     */
    Long countByMessageIdAndIsDeletedFalse(Long messageId);

    /**
     * 查询消息的所有回应用户
     */
    @Query("SELECT mr.userId FROM MessageReaction mr " +
           "WHERE mr.messageId = :messageId AND mr.isDeleted = false")
    List<Long> findUserIdsByMessageId(@Param("messageId") Long messageId);

    /**
     * 删除用户的回应（软删除）
     */
    @Query("UPDATE MessageReaction mr SET mr.isDeleted = true, mr.deletedAt = :now " +
           "WHERE mr.messageId = :messageId AND mr.userId = :userId AND mr.emojiCode = :emojiCode")
    void softDeleteByMessageIdAndUserIdAndEmojiCode(
            @Param("messageId") Long messageId,
            @Param("userId") Long userId,
            @Param("emojiCode") String emojiCode,
            @Param("now") LocalDateTime now);

    /**
     * 删除用户对消息的所有回应
     */
    @Query("UPDATE MessageReaction mr SET mr.isDeleted = true, mr.deletedAt = :now " +
           "WHERE mr.messageId = :messageId AND mr.userId = :userId AND mr.isDeleted = false")
    void softDeleteAllByMessageIdAndUserId(
            @Param("messageId") Long messageId,
            @Param("userId") Long userId,
            @Param("now") LocalDateTime now);

    /**
     * 查询会话中的热门表情
     */
    @Query("SELECT mr.emojiCode, COUNT(mr) as cnt FROM MessageReaction mr " +
           "WHERE mr.conversationId = :conversationId AND mr.createdAt > :since AND mr.isDeleted = false " +
           "GROUP BY mr.emojiCode ORDER BY cnt DESC")
    List<Object[]> findPopularEmojisInConversation(
            @Param("conversationId") Long conversationId,
            @Param("since") LocalDateTime since,
            Pageable pageable);

    /**
     * 检查用户是否对消息有反应
     */
    boolean existsByMessageIdAndUserIdAndIsDeletedFalse(Long messageId, Long userId);

    /**
     * 分页查询消息的回应
     */
    Page<MessageReaction> findByMessageIdAndIsDeletedFalse(Long messageId, Pageable pageable);
}
