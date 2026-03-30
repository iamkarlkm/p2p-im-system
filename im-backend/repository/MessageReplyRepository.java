package com.im.backend.repository;

import com.im.backend.entity.MessageReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 消息引用回复数据访问层
 */
@Repository
public interface MessageReplyRepository extends JpaRepository<MessageReply, Long> {

    Optional<MessageReply> findByReplyMessageId(Long replyMessageId);

    List<MessageReply> findByOriginalMessageIdOrderByCreatedAtDesc(Long originalMessageId);

    Page<MessageReply> findByConversationTypeAndConversationIdOrderByCreatedAtDesc(
        String conversationType, Long conversationId, Pageable pageable);

    List<MessageReply> findByParentReplyIdOrderByCreatedAtAsc(Long parentReplyId);

    @Query("SELECT mr FROM MessageReply mr WHERE mr.originalMessageId = :messageId " +
           "OR mr.replyMessageId = :messageId ORDER BY mr.createdAt DESC")
    List<MessageReply> findAllRelatedReplies(@Param("messageId") Long messageId);

    @Query("SELECT mr FROM MessageReply mr WHERE mr.conversationType = :type " +
           "AND mr.conversationId = :convId AND mr.senderId = :senderId " +
           "ORDER BY mr.createdAt DESC")
    List<MessageReply> findByConversationAndSender(@Param("type") String conversationType,
                                                    @Param("convId") Long conversationId,
                                                    @Param("senderId") Long senderId);

    long countByOriginalMessageId(Long originalMessageId);

    void deleteByReplyMessageId(Long replyMessageId);
}
