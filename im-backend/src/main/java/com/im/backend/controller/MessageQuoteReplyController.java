package com.im.backend.controller;

import com.im.backend.dto.*;
import com.im.backend.model.MessageQuoteReply.QuoteStatus;
import com.im.backend.service.MessageQuoteReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/quote-reply")
public class MessageQuoteReplyController {

    @Autowired
    private MessageQuoteReplyService quoteReplyService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<MessageQuoteReplyDTO>> createQuoteReply(
            @RequestAttribute("userId") Long userId,
            @RequestBody CreateQuoteReplyRequest request) {
        MessageQuoteReplyDTO dto = quoteReplyService.createQuoteReply(userId, request);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageQuoteReplyDTO>> getQuoteReplyById(@PathVariable Long id) {
        MessageQuoteReplyDTO dto = quoteReplyService.getQuoteReplyById(id);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/by-message/{messageId}")
    public ResponseEntity<ApiResponse<MessageQuoteReplyDTO>> getQuoteReplyByMessageId(@PathVariable Long messageId) {
        MessageQuoteReplyDTO dto = quoteReplyService.getQuoteReplyByMessageId(messageId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<ApiResponse<List<MessageQuoteReplyDTO>>> getQuoteRepliesByConversation(
            @PathVariable Long conversationId) {
        List<MessageQuoteReplyDTO> list = quoteReplyService.getQuoteRepliesByConversation(conversationId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/my/{conversationId}")
    public ResponseEntity<ApiResponse<Page<MessageQuoteReplyDTO>>> getMyQuoteReplies(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long conversationId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MessageQuoteReplyDTO> result = quoteReplyService.getQuoteRepliesBySender(userId, conversationId, pageable);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageQuoteReplyDTO>> updateQuoteReply(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        String newContent = body.get("content");
        MessageQuoteReplyDTO dto = quoteReplyService.updateQuoteReply(id, userId, newContent);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteQuoteReply(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        quoteReplyService.deleteQuoteReply(id, userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @GetMapping("/tree/{rootQuoteId}")
    public ResponseEntity<ApiResponse<List<MessageQuoteReplyDTO>>> getQuoteTree(@PathVariable Long rootQuoteId) {
        List<MessageQuoteReplyDTO> tree = quoteReplyService.getQuoteTree(rootQuoteId);
        return ResponseEntity.ok(ApiResponse.success(tree));
    }

    @GetMapping("/nested/{parentQuoteId}")
    public ResponseEntity<ApiResponse<List<MessageQuoteReplyDTO>>> getNestedQuotes(@PathVariable Long parentQuoteId) {
        List<MessageQuoteReplyDTO> nested = quoteReplyService.getNestedQuotes(parentQuoteId);
        return ResponseEntity.ok(ApiResponse.success(nested));
    }

    @GetMapping("/count/{messageId}")
    public ResponseEntity<ApiResponse<Map<String, Long>>> countQuotesByMessage(@PathVariable Long messageId) {
        Long count = quoteReplyService.countQuotesByMessage(messageId);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/by-quoted-message/{messageId}")
    public ResponseEntity<ApiResponse<List<MessageQuoteReplyDTO>>> getQuotesByMessage(@PathVariable Long messageId) {
        List<MessageQuoteReplyDTO> list = quoteReplyService.getQuotesByMessage(messageId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/chain/{conversationId}/{messageId}")
    public ResponseEntity<ApiResponse<List<MessageQuoteReplyDTO>>> getQuotesContainingInChain(
            @PathVariable Long conversationId,
            @PathVariable Long messageId) {
        List<MessageQuoteReplyDTO> list = quoteReplyService.getQuotesContainingInChain(conversationId, messageId);
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/{id}/recall")
    public ResponseEntity<ApiResponse<MessageQuoteReplyDTO>> recallQuoteReply(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long id) {
        MessageQuoteReplyDTO dto = quoteReplyService.recallQuoteReply(id, userId);
        return ResponseEntity.ok(ApiResponse.success(dto));
    }

    @GetMapping("/can-quote/{messageId}")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> canQuoteMessage(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long messageId) {
        boolean canQuote = quoteReplyService.canQuoteMessage(userId, messageId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("canQuote", canQuote);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
