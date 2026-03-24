package com.im.system.service;

import com.im.system.entity.MessageQuoteEntity;
import com.im.system.repository.MessageQuoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MessageQuoteService {

    private final MessageQuoteRepository messageQuoteRepository;

    @Autowired
    public MessageQuoteService(MessageQuoteRepository messageQuoteRepository) {
        this.messageQuoteRepository = messageQuoteRepository;
    }

    // Basic CRUD operations
    public MessageQuoteEntity createQuote(MessageQuoteEntity quote) {
        return messageQuoteRepository.save(quote);
    }

    public Optional<MessageQuoteEntity> getQuoteById(UUID id) {
        return messageQuoteRepository.findById(id);
    }

    public Optional<MessageQuoteEntity> getQuoteByIdAndConversationId(UUID id, UUID conversationId) {
        return messageQuoteRepository.findByIdAndConversationId(id, conversationId);
    }

    public Optional<MessageQuoteEntity> getQuoteByMessageIdAndConversationId(UUID messageId, UUID conversationId) {
        return messageQuoteRepository.findByMessageIdAndConversationId(messageId, conversationId);
    }

    public List<MessageQuoteEntity> getAllQuotes() {
        return messageQuoteRepository.findAll();
    }

    public MessageQuoteEntity updateQuote(MessageQuoteEntity quote) {
        return messageQuoteRepository.save(quote);
    }

    public void deleteQuote(UUID id) {
        messageQuoteRepository.deleteById(id);
    }

    // Query operations
    public List<MessageQuoteEntity> getQuotesByMessageId(UUID messageId) {
        return messageQuoteRepository.findByMessageId(messageId);
    }

    public List<MessageQuoteEntity> getQuotesByQuotedMessageId(UUID quotedMessageId) {
        return messageQuoteRepository.findByQuotedMessageId(quotedMessageId);
    }

    public List<MessageQuoteEntity> getQuotesByConversationId(UUID conversationId) {
        return messageQuoteRepository.findByConversationIdOrderByCreatedAtDesc(conversationId);
    }

    public List<MessageQuoteEntity> getQuotesByUserId(UUID userId) {
        return messageQuoteRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<MessageQuoteEntity> getQuotesByConversationIdAndUserId(UUID conversationId, UUID userId) {
        return messageQuoteRepository.findByConversationIdAndUserId(conversationId, userId);
    }

    public List<MessageQuoteEntity> getQuotesForMessage(UUID conversationId, UUID quotedMessageId) {
        return messageQuoteRepository.findQuotesForMessage(conversationId, quotedMessageId);
    }

    public List<MessageQuoteEntity> getQuotesByQuotedSenderId(UUID quotedSenderId) {
        return messageQuoteRepository.findByQuotedSenderId(quotedSenderId);
    }

    public List<MessageQuoteEntity> getQuotesByConversationIdAndQuotedSenderId(UUID conversationId, UUID quotedSenderId) {
        return messageQuoteRepository.findByConversationIdAndQuotedSenderId(conversationId, quotedSenderId);
    }

    public List<MessageQuoteEntity> getQuotesByQuoteType(String quoteType) {
        return messageQuoteRepository.findByQuoteType(quoteType);
    }

    public List<MessageQuoteEntity> getQuotesByConversationIdAndQuoteType(UUID conversationId, String quoteType) {
        return messageQuoteRepository.findByConversationIdAndQuoteType(conversationId, quoteType);
    }

    // Statistics operations
    public Long countQuotesForMessage(UUID quotedMessageId) {
        return messageQuoteRepository.countQuotesForMessage(quotedMessageId);
    }

    public Long countQuotesForMessageInConversation(UUID conversationId, UUID quotedMessageId) {
        return messageQuoteRepository.countQuotesForMessageInConversation(conversationId, quotedMessageId);
    }

    public Long countQuotesInConversation(UUID conversationId) {
        return messageQuoteRepository.countQuotesInConversation(conversationId);
    }

    public Long countQuotesByUser(UUID userId) {
        return messageQuoteRepository.countQuotesByUser(userId);
    }

    public Long countQuotesByUserInConversation(UUID conversationId, UUID userId) {
        return messageQuoteRepository.countQuotesByUserInConversation(conversationId, userId);
    }

    public List<Object[]> countQuotesByQuotedMessage(UUID conversationId) {
        return messageQuoteRepository.countQuotesByQuotedMessage(conversationId);
    }

    public List<Object[]> countQuotesByQuotedSender(UUID conversationId) {
        return messageQuoteRepository.countQuotesByQuotedSender(conversationId);
    }

    // Helper methods for creating quotes
    public MessageQuoteEntity createTextQuote(UUID messageId, UUID quotedMessageId, UUID conversationId,
                                              UUID userId, String quotedContent, UUID quotedSenderId,
                                              String quotedSenderName, String quotePreview) {
        MessageQuoteEntity quote = new MessageQuoteEntity(messageId, quotedMessageId, conversationId, userId);
        quote.setQuotedContent(quotedContent);
        quote.setQuotedSenderId(quotedSenderId);
        quote.setQuotedSenderName(quotedSenderName);
        quote.setQuoteType("TEXT");
        quote.setQuotePreview(quotePreview);
        quote.setHasAttachment(false);
        quote.setAttachmentCount(0);
        return messageQuoteRepository.save(quote);
    }

    public MessageQuoteEntity createAttachmentQuote(UUID messageId, UUID quotedMessageId, UUID conversationId,
                                                    UUID userId, String quotedContent, UUID quotedSenderId,
                                                    String quotedSenderName, String quotePreview,
                                                    int attachmentCount) {
        MessageQuoteEntity quote = new MessageQuoteEntity(messageId, quotedMessageId, conversationId, userId);
        quote.setQuotedContent(quotedContent);
        quote.setQuotedSenderId(quotedSenderId);
        quote.setQuotedSenderName(quotedSenderName);
        quote.setQuoteType("ATTACHMENT");
        quote.setQuotePreview(quotePreview);
        quote.setHasAttachment(true);
        quote.setAttachmentCount(attachmentCount);
        return messageQuoteRepository.save(quote);
    }

    public MessageQuoteEntity createMediaQuote(UUID messageId, UUID quotedMessageId, UUID conversationId,
                                               UUID userId, String quotedContent, UUID quotedSenderId,
                                               String quotedSenderName, String quotePreview, String mediaType) {
        MessageQuoteEntity quote = new MessageQuoteEntity(messageId, quotedMessageId, conversationId, userId);
        quote.setQuotedContent(quotedContent);
        quote.setQuotedSenderId(quotedSenderId);
        quote.setQuotedSenderName(quotedSenderName);
        quote.setQuoteType(mediaType != null ? mediaType.toUpperCase() : "MEDIA");
        quote.setQuotePreview(quotePreview);
        quote.setHasAttachment(true);
        quote.setAttachmentCount(1);
        return messageQuoteRepository.save(quote);
    }

    // Delete operations
    @Transactional
    public void markQuoteAsDeleted(UUID messageId) {
        messageQuoteRepository.markQuoteAsDeleted(messageId, LocalDateTime.now());
    }

    @Transactional
    public void markQuotesForDeletedMessage(UUID quotedMessageId) {
        messageQuoteRepository.markQuotesForDeletedMessage(quotedMessageId, LocalDateTime.now());
    }

    @Transactional
    public int cleanupDeletedQuotes(UUID conversationId, LocalDateTime threshold) {
        return messageQuoteRepository.deleteDeletedQuotes(conversationId, threshold);
    }

    @Transactional
    public int cleanupOldQuotes(UUID conversationId, LocalDateTime threshold) {
        return messageQuoteRepository.deleteOldQuotes(conversationId, threshold);
    }

    // Search operations
    public List<MessageQuoteEntity> searchQuotesByPreview(UUID conversationId, String keyword) {
        return messageQuoteRepository.findByConversationIdAndQuotePreviewContaining(conversationId, keyword);
    }

    public List<MessageQuoteEntity> searchQuotesByContent(String keyword) {
        return messageQuoteRepository.findByQuotedContentContaining(keyword);
    }

    // Batch operations
    public List<MessageQuoteEntity> getQuotesByMessageIds(List<UUID> messageIds) {
        return messageQuoteRepository.findByMessageIdIn(messageIds);
    }

    public List<MessageQuoteEntity> getQuotesByQuotedMessageIds(List<UUID> quotedMessageIds) {
        return messageQuoteRepository.findByQuotedMessageIdIn(quotedMessageIds);
    }

    public List<MessageQuoteEntity> getQuotesInConversationByDateRange(UUID conversationId, LocalDateTime start, LocalDateTime end) {
        return messageQuoteRepository.findByConversationIdAndCreatedAtBetween(conversationId, start, end);
    }

    public List<MessageQuoteEntity> getQuotesByUserInDateRange(UUID userId, LocalDateTime start, LocalDateTime end) {
        return messageQuoteRepository.findByUserIdAndCreatedAtBetween(userId, start, end);
    }

    // Advanced queries
    public List<MessageQuoteEntity> getQuotesByConversationIdAndHasAttachment(UUID conversationId, Boolean hasAttachment) {
        return messageQuoteRepository.findByConversationIdAndHasAttachment(conversationId, hasAttachment);
    }

    public List<MessageQuoteEntity> getQuotesByConversationIdAndAttachmentCountGreaterThan(UUID conversationId, Integer attachmentCount) {
        return messageQuoteRepository.findByConversationIdAndAttachmentCountGreaterThan(conversationId, attachmentCount);
    }

    public List<MessageQuoteEntity> getQuotesByUserAfter(UUID userId, UUID conversationId, LocalDateTime createdAt) {
        return messageQuoteRepository.findByUserIdAndConversationIdAndCreatedAtAfter(userId, conversationId, createdAt);
    }

    public List<MessageQuoteEntity> getQuotesByUserBefore(UUID userId, UUID conversationId, LocalDateTime createdAt) {
        return messageQuoteRepository.findByUserIdAndConversationIdAndCreatedAtBefore(userId, conversationId, createdAt);
    }

    public List<MessageQuoteEntity> getQuotesByQuotedSenderAfter(UUID conversationId, UUID quotedSenderId, LocalDateTime createdAt) {
        return messageQuoteRepository.findByConversationIdAndQuotedSenderIdAndCreatedAtAfter(conversationId, quotedSenderId, createdAt);
    }

    public List<MessageQuoteEntity> getQuotesByQuotedSenderBefore(UUID conversationId, UUID quotedSenderId, LocalDateTime createdAt) {
        return messageQuoteRepository.findByConversationIdAndQuotedSenderIdAndCreatedAtBefore(conversationId, quotedSenderId, createdAt);
    }
}