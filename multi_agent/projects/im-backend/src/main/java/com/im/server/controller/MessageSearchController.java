package com.im.server.controller;

import com.im.server.service.MessageSearchService;
import com.im.server.service.MessageSearchService.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Message Search REST API Controller
 * 
 * 提供消息搜索相关的 RESTful API
 * 
 * Endpoints:
 * - POST /api/search - 全文搜索
 * - POST /api/search/conversation - 会话内搜索
 * - POST /api/search/advanced - 高级搜索（多条件）
 * - GET /api/search/suggestions - 获取搜索建议
 * - GET /api/search/hot - 获取热门搜索
 * - GET /api/search/history - 获取搜索历史
 * - DELETE /api/search/history - 清空搜索历史
 * - POST /api/search/index - 手动触发索引
 * - DELETE /api/search/index - 删除索引
 */
@RestController
@RequestMapping("/api/search")
public class MessageSearchController {

    @Autowired
    private MessageSearchService searchService;

    /**
     * 全文搜索消息
     * 
     * POST /api/search
     * 
     * Request Body:
     * {
     *   "keyword": "搜索关键词",
     *   "page": 0,
     *   "size": 20
     * }
     * 
     * Response:
     * {
     *   "code": 200,
     *   "message": "success",
     *   "data": {
     *     "hits": [...],
     *     "total": 100,
     *     "page": 0,
     *     "size": 20,
     *     "totalPages": 5,
     *     "hasNext": true,
     *     "hasPrevious": false,
     *     "keyword": "搜索关键词"
     *   }
     * }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> search(@RequestBody SearchRequest request) {
        try {
            // 参数校验
            if (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) {
                return badRequest("Keyword cannot be empty");
            }
            
            // 记录搜索历史
            if (request.getUserId() != null) {
                searchService.recordSearchHistory(request.getUserId(), request.getKeyword());
            }
            
            // 执行搜索
            int page = request.getPage() != null ? request.getPage() : 0;
            int size = request.getSize() != null ? request.getSize() : 20;
            
            SearchResult result = searchService.search(request.getKeyword(), page, size);
            
            return success(result);
        } catch (Exception e) {
            return serverError("Search failed: " + e.getMessage());
        }
    }

    /**
     * 在指定会话中搜索
     * 
     * POST /api/search/conversation
     */
    @PostMapping("/conversation")
    public ResponseEntity<Map<String, Object>> searchInConversation(
            @RequestBody ConversationSearchRequest request) {
        try {
            if (request.getKeyword() == null || request.getKeyword().trim().isEmpty()) {
                return badRequest("Keyword cannot be empty");
            }
            
            int page = request.getPage() != null ? request.getPage() : 0;
            int size = request.getSize() != null ? request.getSize() : 20;
            
            SearchResult result = searchService.searchInConversation(
                request.getConversationId(),
                request.getConversationType(),
                request.getKeyword(),
                page,
                size
            );
            
            return success(result);
        } catch (Exception e) {
            return serverError("Search failed: " + e.getMessage());
        }
    }

    /**
     * 高级搜索（多条件组合）
     * 
     * POST /api/search/advanced
     */
    @PostMapping("/advanced")
    public ResponseEntity<Map<String, Object>> advancedSearch(
            @RequestBody AdvancedSearchRequest request) {
        try {
            int page = request.getPage() != null ? request.getPage() : 0;
            int size = request.getSize() != null ? request.getSize() : 20;
            
            SearchResult result = searchService.searchWithFilters(
                request.getKeyword(),
                request.getConversationId(),
                request.getConversationType(),
                request.getSenderId(),
                request.getMessageType(),
                request.getStartTime(),
                request.getEndTime(),
                page,
                size
            );
            
            return success(result);
        } catch (Exception e) {
            return serverError("Advanced search failed: " + e.getMessage());
        }
    }

    /**
     * 获取搜索建议
     * 
     * GET /api/search/suggestions?prefix=xxx&limit=10
     */
    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getSuggestions(
            @RequestParam String prefix,
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<String> suggestions = searchService.getSuggestions(prefix, limit);
            return success(suggestions);
        } catch (Exception e) {
            return serverError("Failed to get suggestions: " + e.getMessage());
        }
    }

    /**
     * 获取热门搜索
     * 
     * GET /api/search/hot?limit=10
     */
    @GetMapping("/hot")
    public ResponseEntity<Map<String, Object>> getHotSearch(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            List<String> hotKeywords = searchService.getHotKeywords(limit);
            return success(hotKeywords);
        } catch (Exception e) {
            return serverError("Failed to get hot search: " + e.getMessage());
        }
    }

    /**
     * 获取用户搜索历史
     * 
     * GET /api/search/history?userId=123&limit=20
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getSearchHistory(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<String> history = searchService.getSearchHistory(userId, limit);
            return success(history);
        } catch (Exception e) {
            return serverError("Failed to get search history: " + e.getMessage());
        }
    }

    /**
     * 清空用户搜索历史
     * 
     * DELETE /api/search/history?userId=123
     */
    @DeleteMapping("/history")
    public ResponseEntity<Map<String, Object>> clearSearchHistory(@RequestParam Long userId) {
        try {
            searchService.clearSearchHistory(userId);
            return success("History cleared successfully");
        } catch (Exception e) {
            return serverError("Failed to clear history: " + e.getMessage());
        }
    }

    /**
     * 手动索引消息（管理员接口）
     * 
     * POST /api/search/index
     */
    @PostMapping("/index")
    public ResponseEntity<Map<String, Object>> indexMessage(@RequestBody IndexRequest request) {
        try {
            searchService.indexMessage(
                request.getMessageId(),
                request.getConversationId(),
                request.getConversationType(),
                request.getSenderId(),
                request.getSenderNickname(),
                request.getMessageType(),
                request.getContent(),
                request.getPlainText(),
                request.getFileName(),
                request.getMentionedUsers(),
                request.getMessageTime() != null ? request.getMessageTime() : LocalDateTime.now()
            );
            return success("Message indexed successfully");
        } catch (Exception e) {
            return serverError("Failed to index message: " + e.getMessage());
        }
    }

    /**
     * 删除消息索引（管理员接口）
     * 
     * DELETE /api/search/index/{messageId}
     */
    @DeleteMapping("/index/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteIndex(@PathVariable Long messageId) {
        try {
            searchService.deleteIndex(messageId);
            return success("Index deleted successfully");
        } catch (Exception e) {
            return serverError("Failed to delete index: " + e.getMessage());
        }
    }

    // ==================== Request DTOs ====================

    /**
     * 搜索请求
     */
    public static class SearchRequest {
        private String keyword;
        private Long userId;
        private Integer page;
        private Integer size;

        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }
    }

    /**
     * 会话搜索请求
     */
    public static class ConversationSearchRequest {
        private String keyword;
        private Long conversationId;
        private Integer conversationType;
        private Integer page;
        private Integer size;

        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        public Integer getConversationType() { return conversationType; }
        public void setConversationType(Integer conversationType) { this.conversationType = conversationType; }
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }
    }

    /**
     * 高级搜索请求
     */
    public static class AdvancedSearchRequest {
        private String keyword;
        private Long conversationId;
        private Integer conversationType;
        private Long senderId;
        private Integer messageType;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime startTime;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime endTime;
        private Integer page;
        private Integer size;

        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        public Integer getConversationType() { return conversationType; }
        public void setConversationType(Integer conversationType) { this.conversationType = conversationType; }
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public Integer getMessageType() { return messageType; }
        public void setMessageType(Integer messageType) { this.messageType = messageType; }
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public LocalDateTime getEndTime() { return endTime; }
        public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
        public Integer getPage() { return page; }
        public void setPage(Integer page) { this.page = page; }
        public Integer getSize() { return size; }
        public void setSize(Integer size) { this.size = size; }
    }

    /**
     * 索引请求
     */
    public static class IndexRequest {
        private Long messageId;
        private Long conversationId;
        private Integer conversationType;
        private Long senderId;
        private String senderNickname;
        private Integer messageType;
        private String content;
        private String plainText;
        private String fileName;
        private List<Long> mentionedUsers;
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        private LocalDateTime messageTime;

        public Long getMessageId() { return messageId; }
        public void setMessageId(Long messageId) { this.messageId = messageId; }
        public Long getConversationId() { return conversationId; }
        public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
        public Integer getConversationType() { return conversationType; }
        public void setConversationType(Integer conversationType) { this.conversationType = conversationType; }
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        public String getSenderNickname() { return senderNickname; }
        public void setSenderNickname(String senderNickname) { this.senderNickname = senderNickname; }
        public Integer getMessageType() { return messageType; }
        public void setMessageType(Integer messageType) { this.messageType = messageType; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getPlainText() { return plainText; }
        public void setPlainText(String plainText) { this.plainText = plainText; }
        public String getFileName() { return fileName; }
        public void setFileName(String fileName) { this.fileName = fileName; }
        public List<Long> getMentionedUsers() { return mentionedUsers; }
        public void setMentionedUsers(List<Long> mentionedUsers) { this.mentionedUsers = mentionedUsers; }
        public LocalDateTime getMessageTime() { return messageTime; }
        public void setMessageTime(LocalDateTime messageTime) { this.messageTime = messageTime; }
    }

    // ==================== Response Helpers ====================

    private ResponseEntity<Map<String, Object>> success(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> badRequest(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        return ResponseEntity.badRequest().body(response);
    }

    private ResponseEntity<Map<String, Object>> serverError(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", message);
        return ResponseEntity.internalServerError().body(response);
    }
}
