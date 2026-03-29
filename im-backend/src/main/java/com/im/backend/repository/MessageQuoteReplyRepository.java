package com.im.backend.repository;

import com.im.backend.model.MessageQuoteReply;
import com.im.backend.model.MessageQuoteReply.QuoteStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageQuoteReplyRepository extends JpaRepository<MessageQuoteReply, Long> {

    List<MessageQuoteReply> findByMessageId(Long messageId);

    List<MessageQuoteReply> findByQuotedMessageId(Long quotedMessageId);

    List<MessageQuoteReply> findByConversationId(Long conversationId);

    List<MessageQuoteReply> findBySenderId(Long senderId);

    @Query("SELECT qr FROM MessageQuoteReply qr WHERE qr.conversationId = :conversationId AND qr.status = :status ORDER BY qr.createdAt DESC")
    List<MessageQuoteReply> findByConversationIdAndStatus(@Param("conversationId") Long conversationId, @Param("status") QuoteStatus status);

    @Query("SELECT qr FROM MessageQuoteReply qr WHERE qr.senderId = :senderId AND qr.conversationId = :conversationId ORDER BY qr.createdAt DESC")
    Page<MessageQuoteReply> findBySenderIdAndConversationId(@Param("senderId") Long senderId, @Param("conversationId") Long conversationId, Pageable pageable);

    @Query("SELECT qr FROM MessageQuoteReply qr WHERE qr.rootQuoteId = :rootQuoteId ORDER BY qr.quoteLevel, qr.createdAt")
    List<MessageQuoteReply> findQuoteTreeByRootId(@Param("rootQuoteId") Long rootQuoteId);

    @Query("SELECT qr FROM MessageQuoteReply qr WHERE qr.parentQuoteId = :parentQuoteId ORDER BY qr.createdAt")
    List<MessageQuoteReply> findByParentQuoteId(@Param("parentQuoteId") Long parentQuoteId);

    @Query("SELECT qr FROM MessageQuoteReply qr WHERE qr.quotedMessageId = :messageId AND qr.status = 'ACTIVE'")
    List<MessageQuoteReply> findActiveQuotesByMessageId(@Param("messageId") Long messageId);

    @Query("SELECT COUNT(qr) FROM MessageQuoteReply qr WHERE qr.quotedMessageId = :messageId AND qr.status = 'ACTIVE'")
    Long countActiveQuotesByMessageId(@Param("messageId") Long messageId);

    @Query("SELECT qr FROM MessageQuoteReply qr WHERE qr.quoteLevel > :level AND qr.rootQuoteId = :rootId")
    List<MessageQuoteReply> findNestedQuotesByLevel(@Param("level") Integer level, @Param("rootId") Long rootId);

    @Query(value = "SELECT * FROM message_quote_reply qr WHERE qr.conversation_id = :conversationId " +
           "AND JSON_CONTAINS(qr.quote_chain, :messageId, '$') AND qr.status = 'ACTIVE'", nativeQuery = true)
    List<MessageQuoteReply> findByQuoteChainContaining(@Param("conversationId") Long conversationId, @Param("messageId") String messageId);

    @Query("SELECT qr FROM MessageQuoteReply qr WHERE qr.isBatchQuote = true AND qr.batchQuotedMessageIds IS NOT EMPTY")
    List<MessageQuoteReply> findBatchQuotes();

    Optional<MessageQuoteReply> findByMessageIdAndStatus(Long messageId, QuoteStatus status);
}
