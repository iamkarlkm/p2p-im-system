package com.im.system.controller;

import com.im.system.entity.MessageQuoteEntity;
import com.im.system.service.MessageQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/message-quotes")
@CrossOrigin(origins = "*")
public class MessageQuoteController {

    private final MessageQuoteService messageQuoteService;

    @Autowired
    public MessageQuoteController(MessageQuoteService messageQuoteService) {
        this.messageQuoteService = messageQuoteService;
    }

    // Basic CRUD operations
    @PostMapping
    public ResponseEntity<MessageQuoteEntity> createQuote(@RequestBody MessageQuoteEntity quote) {
        MessageQuoteEntity createdQuote = messageQuoteService.createQuote(quote);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdQuote);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MessageQuoteEntity> getQuoteById(@PathVariable UUID id) {
        return messageQuoteService.getQuoteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/conversation/{conversationId}")
    public ResponseEntity<MessageQuoteEntity> getQuoteByIdAndConversationId(
            @PathVariable UUID id,
            @PathVariable UUID conversationId) {
        return messageQuoteService.getQuoteByIdAndConversationId(id, conversationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/message/{messageId}/conversation/{conversationId}")
    public ResponseEntity<MessageQuoteEntity> getQuoteByMessageIdAndConversationId(
            @PathVariable UUID messageId,
            @PathVariable UUID conversationId) {
        return messageQuoteService.getQuoteByMessageIdAndConversationId(messageId, conversationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MessageQuoteEntity> updateQuote(
            @PathVariable UUID id,
            @RequestBody MessageQuoteEntity quote) {
        quote.setId(id);
        MessageQuoteEntity updatedQuote = messageQuoteService.updateQuote(quote);
        return ResponseEntity.ok(updatedQuote);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuote(@PathVariable UUID id) {
        messageQuoteService.deleteQuote(id);
        return ResponseEntity.noContent().build();
    }

    // Query operations
    @GetMapping("/message/{messageId}")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByMessageId(@PathVariable UUID messageId) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByMessageId(messageId));
    }

    @GetMapping("/quoted/{quotedMessageId}")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByQuotedMessageId(@PathVariable UUID quotedMessageId) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByQuotedMessageId(quotedMessageId));
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByConversationId(@PathVariable UUID conversationId) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByConversationId(conversationId));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByUserId(userId));
    }

    @GetMapping("/conversation/{conversationId}/user/{userId}")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByConversationIdAndUserId(
            @PathVariable UUID conversationId,
            @PathVariable UUID userId) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByConversationIdAndUserId(conversationId, userId));
    }

    @GetMapping("/conversation/{conversationId}/message/{quotedMessageId}")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesForMessage(
            @PathVariable UUID conversationId,
            @PathVariable UUID quotedMessageId) {
        return ResponseEntity.ok(messageQuoteService.getQuotesForMessage(conversationId, quotedMessageId));
    }

    @GetMapping("/sender/{quotedSenderId}")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByQuotedSenderId(@PathVariable UUID quotedSenderId) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByQuotedSenderId(quotedSenderId));
    }

    @GetMapping("/conversation/{conversationId}/sender/{quotedSenderId}")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByConversationIdAndQuotedSenderId(
            @PathVariable UUID conversationId,
            @PathVariable UUID quotedSenderId) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByConversationIdAndQuotedSenderId(conversationId, quotedSenderId));
    }

    @GetMapping("/type/{quoteType}")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByQuoteType(@PathVariable String quoteType) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByQuoteType(quoteType));
    }

    @GetMapping("/conversation/{conversationId}/type/{quoteType}")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByConversationIdAndQuoteType(
            @PathVariable UUID conversationId,
            @PathVariable String quoteType) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByConversationIdAndQuoteType(conversationId, quoteType));
    }

    // Statistics endpoints
    @GetMapping("/quoted/{quotedMessageId}/count")
    public ResponseEntity<Map<String, Long>> countQuotesForMessage(@PathVariable UUID quotedMessageId) {
        Long count = messageQuoteService.countQuotesForMessage(quotedMessageId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{conversationId}/quoted/{quotedMessageId}/count")
    public ResponseEntity<Map<String, Long>> countQuotesForMessageInConversation(
            @PathVariable UUID conversationId,
            @PathVariable UUID quotedMessageId) {
        Long count = messageQuoteService.countQuotesForMessageInConversation(conversationId, quotedMessageId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{conversationId}/count")
    public ResponseEntity<Map<String, Long>> countQuotesInConversation(@PathVariable UUID conversationId) {
        Long count = messageQuoteService.countQuotesInConversation(conversationId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countQuotesByUser(@PathVariable UUID userId) {
        Long count = messageQuoteService.countQuotesByUser(userId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{conversationId}/user/{userId}/count")
    public ResponseEntity<Map<String, Long>> countQuotesByUserInConversation(
            @PathVariable UUID conversationId,
            @PathVariable UUID userId) {
        Long count = messageQuoteService.countQuotesByUserInConversation(conversationId, userId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{conversationId}/stats/by-quoted-message")
    public ResponseEntity<List<Map<String, Object>>> countQuotesByQuotedMessage(@PathVariable UUID conversationId) {
        List<Object[]> results = messageQuoteService.countQuotesByQuotedMessage(conversationId);
        List<Map<String, Object>> response = results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("quotedMessageId", row[0]);
            map.put("count", row[1]);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/conversation/{conversationId}/stats/by-quoted-sender")
    public ResponseEntity<List<Map<String, Object>>> countQuotesByQuotedSender(@PathVariable UUID conversationId) {
        List<Object[]> results = messageQuoteService.countQuotesByQuotedSender(conversationId);
        List<Map<String, Object>> response = results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("quotedSenderId", row[0]);
            map.put("count", row[1]);
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Delete operations
    @DeleteMapping("/message/{messageId}/mark-deleted")
    public ResponseEntity<Void> markQuoteAsDeleted(@PathVariable UUID messageId) {
        messageQuoteService.markQuoteAsDeleted(messageId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/quoted/{quotedMessageId}/mark-deleted")
    public ResponseEntity<Void> markQuotesForDeletedMessage(@PathVariable UUID quotedMessageId) {
        messageQuoteService.markQuotesForDeletedMessage(quotedMessageId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/conversation/{conversationId}/cleanup-deleted")
    public ResponseEntity<Map<String, Integer>> cleanupDeletedQuotes(
            @PathVariable UUID conversationId,
            @RequestParam String threshold) {
        int deletedCount = messageQuoteService.cleanupDeletedQuotes(conversationId, LocalDateTime.parse(threshold));
        Map<String, Integer> response = new HashMap<>();
        response.put("deletedCount", deletedCount);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/conversation/{conversationId}/cleanup-old")
    public ResponseEntity<Map<String, Integer>> cleanupOldQuotes(
            @PathVariable UUID conversationId,
            @RequestParam String threshold) {
        int deletedCount = messageQuoteService.cleanupOldQuotes(conversationId, LocalDateTime.parse(threshold));
        Map<String, Integer> response = new HashMap<>();
        response.put("deletedCount", deletedCount);
        return ResponseEntity.ok(response);
    }

    // Search operations
    @GetMapping("/conversation/{conversationId}/search")
    public ResponseEntity<List<MessageQuoteEntity>> searchQuotesByPreview(
            @PathVariable UUID conversationId,
            @RequestParam String keyword) {
        return ResponseEntity.ok(messageQuoteService.searchQuotesByPreview(conversationId, keyword));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MessageQuoteEntity>> searchQuotesByContent(@RequestParam String keyword) {
        return ResponseEntity.ok(messageQuoteService.searchQuotesByContent(keyword));
    }

    // Batch operations
    @PostMapping("/batch/by-message-ids")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByMessageIds(@RequestBody List<UUID> messageIds) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByMessageIds(messageIds));
    }

    @PostMapping("/batch/by-quoted-message-ids")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByQuotedMessageIds(@RequestBody List<UUID> quotedMessageIds) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByQuotedMessageIds(quotedMessageIds));
    }

    @GetMapping("/conversation/{conversationId}/date-range")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesInConversationByDateRange(
            @PathVariable UUID conversationId,
            @RequestParam String start,
            @RequestParam String end) {
        return ResponseEntity.ok(messageQuoteService.getQuotesInConversationByDateRange(
                conversationId, LocalDateTime.parse(start), LocalDateTime.parse(end)));
    }

    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByUserInDateRange(
            @PathVariable UUID userId,
            @RequestParam String start,
            @RequestParam String end) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByUserInDateRange(
                userId, LocalDateTime.parse(start), LocalDateTime.parse(end)));
    }

    // Advanced queries
    @GetMapping("/conversation/{conversationId}/has-attachment")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByConversationIdAndHasAttachment(
            @PathVariable UUID conversationId,
            @RequestParam Boolean hasAttachment) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByConversationIdAndHasAttachment(conversationId, hasAttachment));
    }

    @GetMapping("/conversation/{conversationId}/attachment-count-greater-than")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByConversationIdAndAttachmentCountGreaterThan(
            @PathVariable UUID conversationId,
            @RequestParam Integer attachmentCount) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByConversationIdAndAttachmentCountGreaterThan(conversationId, attachmentCount));
    }

    @GetMapping("/conversation/{conversationId}/user/{userId}/after")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByUserAfter(
            @PathVariable UUID conversationId,
            @PathVariable UUID userId,
            @RequestParam String createdAt) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByUserAfter(userId, conversationId, LocalDateTime.parse(createdAt)));
    }

    @GetMapping("/conversation/{conversationId}/user/{userId}/before")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByUserBefore(
            @PathVariable UUID conversationId,
            @PathVariable UUID userId,
            @RequestParam String createdAt) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByUserBefore(userId, conversationId, LocalDateTime.parse(createdAt)));
    }

    @GetMapping("/conversation/{conversationId}/sender/{quotedSenderId}/after")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByQuotedSenderAfter(
            @PathVariable UUID conversationId,
            @PathVariable UUID quotedSenderId,
            @RequestParam String createdAt) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByQuotedSenderAfter(conversationId, quotedSenderId, LocalDateTime.parse(createdAt)));
    }

    @GetMapping("/conversation/{conversationId}/sender/{quotedSenderId}/before")
    public ResponseEntity<List<MessageQuoteEntity>> getQuotesByQuotedSenderBefore(
            @PathVariable UUID conversationId,
            @PathVariable UUID quotedSenderId,
            @RequestParam String createdAt) {
        return ResponseEntity.ok(messageQuoteService.getQuotesByQuotedSenderBefore(conversationId, quotedSenderId, LocalDateTime.parse(createdAt)));
    }
}