package com.im.system.repository;

import com.im.system.entity.MessageQuoteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageQuoteRepository extends JpaRepository<MessageQuoteEntity, UUID> {

    List<MessageQuoteEntity> findByMessageId(UUID messageId);
    List<MessageQuoteEntity> findByQuotedMessageId(UUID quotedMessageId);
    List<MessageQuoteEntity> findByConversationId(UUID conversationId);
    List<MessageQuoteEntity> findByUserId(UUID userId);
    List<MessageQuoteEntity> findByConversationIdAndUserId(UUID conversationId, UUID userId);
    Optional<MessageQuoteEntity> findByIdAndConversationId(UUID id, UUID conversationId);
    Optional<MessageQuoteEntity> findByMessageIdAndConversationId(UUID messageId, UUID conversationId);
    List<MessageQuoteEntity> findByQuotedSenderId(UUID quotedSenderId);
    List<MessageQuoteEntity> findByQuoteType(String quoteType);
    List<MessageQuoteEntity> findByConversationIdAndQuoteType(UUID conversationId, String quoteType);
    List<MessageQuoteEntity> findByIsDeleted(Boolean isDeleted);
    List<MessageQuoteEntity> findByConversationIdAndIsDeleted(UUID conversationId, Boolean isDeleted);
    List<MessageQuoteEntity> findByConversationIdAndQuotedMessageIdIn(UUID conversationId, List<UUID> quotedMessageIds);
    List<MessageQuoteEntity> findByMessageIdIn(List<UUID> messageIds);
    List<MessageQuoteEntity> findByQuotedMessageIdIn(List<UUID> quotedMessageIds);
    List<MessageQuoteEntity> findByConversationIdAndCreatedAtBetween(UUID conversationId, java.time.LocalDateTime start, java.time.LocalDateTime end);
    List<MessageQuoteEntity> findByUserIdAndCreatedAtBetween(UUID userId, java.time.LocalDateTime start, java.time.LocalDateTime end);
    List<MessageQuoteEntity> findByConversationIdAndQuotePreviewContaining(UUID conversationId, String keyword);
    List<MessageQuoteEntity> findByQuotedContentContaining(String keyword);
    List<MessageQuoteEntity> findByQuotedSenderIdAndConversationId(UUID quotedSenderId, UUID conversationId);
    List<MessageQuoteEntity> findByUserIdAndConversationIdAndCreatedAtAfter(UUID userId, UUID conversationId, java.time.LocalDateTime createdAt);
    List<MessageQuoteEntity> findByUserIdAndConversationIdAndCreatedAtBefore(UUID userId, UUID conversationId, java.time.LocalDateTime createdAt);
    List<MessageQuoteEntity> findByConversationIdAndHasAttachment(UUID conversationId, Boolean hasAttachment);
    List<MessageQuoteEntity> findByConversationIdAndAttachmentCountGreaterThan(UUID conversationId, Integer attachmentCount);
    List<MessageQuoteEntity> findByConversationIdOrderByCreatedAtDesc(UUID conversationId);
    List<MessageQuoteEntity> findByUserIdOrderByCreatedAtDesc(UUID userId);
    List<MessageQuoteEntity> findByConversationIdAndQuotedSenderId(UUID conversationId, UUID quotedSenderId);
    List<MessageQuoteEntity> findByConversationIdAndQuotedSenderIdAndCreatedAtAfter(UUID conversationId, UUID quotedSenderId, java.time.LocalDateTime createdAt);
    List<MessageQuoteEntity> findByConversationIdAndQuotedSenderIdAndCreatedAtBefore(UUID conversationId, UUID quotedSenderId, java.time.LocalDateTime createdAt);
    List<MessageQuoteEntity> findByConversationIdAndUserIdAndCreatedAtAfter(UUID conversationId, UUID userId, java.time.LocalDateTime createdAt);
    List<MessageQuoteEntity> findByConversationIdAndUserIdAndCreatedAtBefore(UUID conversationId, UUID userId, java.time.LocalDateTime createdAt);
    List<MessageQuoteEntity> findByConversationIdAndQuoteTypeAndCreatedAtAfter(UUID conversationId, String quoteType, java.time.LocalDateTime createdAt);
    List<MessageQuoteEntity> findByConversationIdAndQuoteTypeAndCreatedAtBefore(UUID conversationId, String quoteType, java.time.LocalDateTime createdAt);

    @Query("SELECT mq FROM MessageQuoteEntity mq WHERE mq.conversationId = :conversationId AND mq.quotedMessageId = :quotedMessageId ORDER BY mq.createdAt DESC")
    List<MessageQuoteEntity> findQuotesForMessage(@Param("conversationId") UUID conversationId, @Param("quotedMessageId") UUID quotedMessageId);

    @Query("SELECT mq FROM MessageQuoteEntity mq WHERE mq.conversationId = :conversationId AND mq.messageId = :messageId")
    Optional<MessageQuoteEntity> findQuoteByMessageId(@Param("conversationId") UUID conversationId, @Param("messageId") UUID messageId);

    @Query("SELECT COUNT(mq) FROM MessageQuoteEntity mq WHERE mq.quotedMessageId = :quotedMessageId")
    Long countQuotesForMessage(@Param("quotedMessageId") UUID quotedMessageId);

    @Query("SELECT COUNT(mq) FROM MessageQuoteEntity mq WHERE mq.conversationId = :conversationId AND mq.quotedMessageId = :quotedMessageId")
    Long countQuotesForMessageInConversation(@Param("conversationId") UUID conversationId, @Param("quotedMessageId") UUID quotedMessageId);

    @Query("SELECT COUNT(mq) FROM MessageQuoteEntity mq WHERE mq.conversationId = :conversationId")
    Long countQuotesInConversation(@Param("conversationId") UUID conversationId);

    @Query("SELECT COUNT(mq) FROM MessageQuoteEntity mq WHERE mq.userId = :userId")
    Long countQuotesByUser(@Param("userId") UUID userId);

    @Query("SELECT COUNT(mq) FROM MessageQuoteEntity mq WHERE mq.conversationId = :conversationId AND mq.userId = :userId")
    Long countQuotesByUserInConversation(@Param("conversationId") UUID conversationId, @Param("userId") UUID userId);

    @Query("SELECT mq.quotedMessageId, COUNT(mq) FROM MessageQuoteEntity mq WHERE mq.conversationId = :conversationId GROUP BY mq.quotedMessageId ORDER BY COUNT(mq) DESC")
    List<Object[]> countQuotesByQuotedMessage(@Param("conversationId") UUID conversationId);

    @Query("SELECT mq.quotedSenderId, COUNT(mq) FROM MessageQuoteEntity mq WHERE mq.conversationId = :conversationId GROUP BY mq.quotedSenderId ORDER BY COUNT(mq) DESC")
    List<Object[]> countQuotesByQuotedSender(@Param("conversationId") UUID conversationId);

    @Modifying
    @Query("UPDATE MessageQuoteEntity mq SET mq.isDeleted = true, mq.updatedAt = :now WHERE mq.messageId = :messageId")
    int markQuoteAsDeleted(@Param("messageId") UUID messageId, @Param("now") java.time.LocalDateTime now);

    @Modifying
    @Query("UPDATE MessageQuoteEntity mq SET mq.isDeleted = true, mq.updatedAt = :now WHERE mq.quotedMessageId = :quotedMessageId")
    int markQuotesForDeletedMessage(@Param("quotedMessageId") UUID quotedMessageId, @Param("now") java.time.LocalDateTime now);

    @Modifying
    @Query("DELETE FROM MessageQuoteEntity mq WHERE mq.conversationId = :conversationId AND mq.isDeleted = true AND mq.createdAt <= :threshold")
    int deleteDeletedQuotes(@Param("conversationId") UUID conversationId, @Param("threshold") java.time.LocalDateTime threshold);

    @Modifying
    @Query("DELETE FROM MessageQuoteEntity mq WHERE mq.conversationId = :conversationId AND mq.createdAt <= :threshold")
    int deleteOldQuotes(@Param("conversationId") UUID conversationId, @Param("threshold") java.time.LocalDateTime threshold);
}