package com.im.system.controller;

import com.im.system.entity.MessageSearchEntity;
import com.im.system.service.MessageSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 消息全文搜索 REST API 控制器
 */
@RestController
@RequestMapping("/api/v1/search")
@CrossOrigin(origins = "*")
public class MessageSearchController {
    
    @Autowired
    private MessageSearchService messageSearchService;
    
    // ==================== 全文搜索接口 ====================
    
    /**
     * 基础全文搜索
     * GET /api/v1/search?q=keyword&page=0&size=20
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(value = "q", required = true) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.search(query, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("query", query);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 会话内搜索
     * GET /api/v1/search/session/{sessionId}?q=keyword&page=0&size=20
     */
    @GetMapping("/session/{sessionId}")
    public ResponseEntity<Map<String, Object>> searchInSession(
            @PathVariable String sessionId,
            @RequestParam(value = "q", required = true) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.searchInSession(sessionId, query, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("sessionId", sessionId);
            response.put("query", query);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("会话搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 发送者消息搜索
     * GET /api/v1/search/sender/{senderId}?q=keyword&page=0&size=20
     */
    @GetMapping("/sender/{senderId}")
    public ResponseEntity<Map<String, Object>> searchBySender(
            @PathVariable String senderId,
            @RequestParam(value = "q", required = true) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.searchBySender(senderId, query, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("senderId", senderId);
            response.put("query", query);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("发送者搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 高级搜索
     * POST /api/v1/search/advanced
     */
    @PostMapping("/advanced")
    public ResponseEntity<Map<String, Object>> advancedSearch(
            @RequestBody MessageSearchService.AdvancedSearchRequest request) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.advancedSearch(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("filters", buildFilterInfo(request));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("高级搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 短语搜索
     * GET /api/v1/search/phrase?q=exact phrase&page=0&size=20
     */
    @GetMapping("/phrase")
    public ResponseEntity<Map<String, Object>> phraseSearch(
            @RequestParam(value = "q", required = true) String phrase,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.phraseSearch(phrase, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("phrase", phrase);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("短语搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 模糊搜索
     * GET /api/v1/search/fuzzy?q=keyword&page=0&size=20
     */
    @GetMapping("/fuzzy")
    public ResponseEntity<Map<String, Object>> fuzzySearch(
            @RequestParam(value = "q", required = true) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.fuzzySearch(query, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("query", query);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("模糊搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 通配符搜索
     * GET /api/v1/search/wildcard?pattern=test*&page=0&size=20
     */
    @GetMapping("/wildcard")
    public ResponseEntity<Map<String, Object>> wildcardSearch(
            @RequestParam(value = "pattern", required = true) String pattern,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.wildcardSearch(pattern, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("pattern", pattern);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("通配符搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 正则表达式搜索
     * GET /api/v1/search/regex?pattern=^test.*end$&page=0&size=20
     */
    @GetMapping("/regex")
    public ResponseEntity<Map<String, Object>> regexSearch(
            @RequestParam(value = "pattern", required = true) String pattern,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.regexSearch(pattern, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("pattern", pattern);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("正则搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 多关键词搜索
     * GET /api/v1/search/multi?keywords=keyword1,keyword2&logic=AND&page=0&size=20
     */
    @GetMapping("/multi")
    public ResponseEntity<Map<String, Object>> multiKeywordSearch(
            @RequestParam(value = "keywords") String keywords,
            @RequestParam(value = "logic", defaultValue = "AND") String logic,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            List<String> keywordList = Arrays.asList(keywords.split(","));
            boolean andLogic = "AND".equalsIgnoreCase(logic);
            
            Page<MessageSearchEntity> results = messageSearchService.multiKeywordSearch(
                    keywordList, andLogic, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("keywords", keywordList);
            response.put("logic", logic);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("多关键词搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 排除关键词搜索
     * GET /api/v1/search/exclude?include=keyword&exclude=negative&page=0&size=20
     */
    @GetMapping("/exclude")
    public ResponseEntity<Map<String, Object>> excludeKeywordSearch(
            @RequestParam(value = "include", required = true) String include,
            @RequestParam(value = "exclude", required = true) String exclude,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.excludeKeywordSearch(
                    include, exclude, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("include", include);
            response.put("exclude", exclude);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("排除搜索失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ==================== 查询接口 ====================
    
    /**
     * 根据消息 ID 查询
     * GET /api/v1/search/message/{messageId}
     */
    @GetMapping("/message/{messageId}")
    public ResponseEntity<Map<String, Object>> getMessageById(
            @PathVariable String messageId) {
        
        try {
            Optional<MessageSearchEntity> result = messageSearchService.findByMessageId(messageId);
            
            if (result.isEmpty()) {
                return buildErrorResponse("消息不存在：" + messageId, HttpStatus.NOT_FOUND);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", result.get());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("查询失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取会话消息列表
     * GET /api/v1/search/session/{sessionId}/messages?page=0&size=20
     */
    @GetMapping("/session/{sessionId}/messages")
    public ResponseEntity<Map<String, Object>> getSessionMessages(
            @PathVariable String sessionId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.getSessionMessages(sessionId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("sessionId", sessionId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("获取会话消息失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取发送者消息列表
     * GET /api/v1/search/sender/{senderId}/messages?page=0&size=20
     */
    @GetMapping("/sender/{senderId}/messages")
    public ResponseEntity<Map<String, Object>> getSenderMessages(
            @PathVariable String senderId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            Page<MessageSearchEntity> results = messageSearchService.getSenderMessages(senderId, page, size);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results.getContent());
            response.put("pagination", buildPaginationInfo(results));
            response.put("senderId", senderId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("获取发送者消息失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取时间范围内消息
     * GET /api/v1/search/time-range?start=2026-01-01T00:00:00&end=2026-12-31T23:59:59
     */
    @GetMapping("/time-range")
    public ResponseEntity<Map<String, Object>> getMessagesByTimeRange(
            @RequestParam(value = "start", required = true) String start,
            @RequestParam(value = "end", required = true) String end) {
        
        try {
            LocalDateTime startTime = LocalDateTime.parse(start);
            LocalDateTime endTime = LocalDateTime.parse(end);
            
            List<MessageSearchEntity> results = messageSearchService.getMessagesByTimeRange(startTime, endTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("count", results.size());
            response.put("startTime", start);
            response.put("endTime", end);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("时间范围查询失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取有附件的消息
     * GET /api/v1/search/attachments?page=0&size=20
     */
    @GetMapping("/attachments")
    public ResponseEntity<Map<String, Object>> getMessagesWithAttachments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            List<MessageSearchEntity> results = messageSearchService.getMessagesWithAttachments();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("count", results.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("获取附件消息失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取加密消息
     * GET /api/v1/search/encrypted?page=0&size=20
     */
    @GetMapping("/encrypted")
    public ResponseEntity<Map<String, Object>> getEncryptedMessages(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            List<MessageSearchEntity> results = messageSearchService.getEncryptedMessages();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("count", results.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("获取加密消息失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取已编辑消息
     * GET /api/v1/search/edited?page=0&size=20
     */
    @GetMapping("/edited")
    public ResponseEntity<Map<String, Object>> getEditedMessages(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            List<MessageSearchEntity> results = messageSearchService.getEditedMessages();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("count", results.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("获取已编辑消息失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取高互动消息
     * GET /api/v1/search/high-interaction?minInteraction=5&page=0&size=20
     */
    @GetMapping("/high-interaction")
    public ResponseEntity<Map<String, Object>> getHighInteractionMessages(
            @RequestParam(value = "minInteraction", defaultValue = "5") int minInteraction,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            List<MessageSearchEntity> results = messageSearchService.getHighInteractionMessages(minInteraction);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("count", results.size());
            response.put("minInteraction", minInteraction);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("获取高互动消息失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取热门消息
     * GET /api/v1/search/popular?minReadCount=10&page=0&size=20
     */
    @GetMapping("/popular")
    public ResponseEntity<Map<String, Object>> getPopularMessages(
            @RequestParam(value = "minReadCount", defaultValue = "10") int minReadCount,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {
        
        try {
            List<MessageSearchEntity> results = messageSearchService.getPopularMessages(minReadCount);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("count", results.size());
            response.put("minReadCount", minReadCount);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("获取热门消息失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取最新消息
     * GET /api/v1/search/latest?limit=20
     */
    @GetMapping("/latest")
    public ResponseEntity<Map<String, Object>> getLatestMessages(
            @RequestParam(value = "limit", defaultValue = "20") int limit) {
        
        try {
            List<MessageSearchEntity> results = messageSearchService.getLatestMessages(limit);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", results);
            response.put("count", results.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("获取最新消息失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ==================== 统计接口 ====================
    
    /**
     * 获取消息总数统计
     * GET /api/v1/search/stats/count
     */
    @GetMapping("/stats/count")
    public ResponseEntity<Map<String, Object>> getMessageCount() {
        
        try {
            long count = messageSearchService.countMessages();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取会话消息统计
     * GET /api/v1/search/stats/session/{sessionId}/count
     */
    @GetMapping("/stats/session/{sessionId}/count")
    public ResponseEntity<Map<String, Object>> getSessionMessageCount(
            @PathVariable String sessionId) {
        
        try {
            long count = messageSearchService.countSessionMessages(sessionId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", count);
            response.put("sessionId", sessionId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("会话统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取发送者消息统计
     * GET /api/v1/search/stats/sender/{senderId}/count
     */
    @GetMapping("/stats/sender/{senderId}/count")
    public ResponseEntity<Map<String, Object>> getSenderMessageCount(
            @PathVariable String senderId) {
        
        try {
            long count = messageSearchService.countSenderMessages(senderId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", count);
            response.put("senderId", senderId);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("发送者统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取消息类型统计
     * GET /api/v1/search/stats/type
     */
    @GetMapping("/stats/type")
    public ResponseEntity<Map<String, Object>> getMessageTypeStats() {
        
        try {
            Map<String, Long> stats = messageSearchService.countByMessageType();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("类型统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取索引健康统计
     * GET /api/v1/search/stats/health
     */
    @GetMapping("/stats/health")
    public ResponseEntity<Map<String, Object>> getIndexHealthStats() {
        
        try {
            Map<String, Object> stats = messageSearchService.getIndexHealthStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("健康统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取索引大小统计
     * GET /api/v1/search/stats/size
     */
    @GetMapping("/stats/size")
    public ResponseEntity<Map<String, Object>> getIndexSizeStats() {
        
        try {
            Map<String, String> stats = messageSearchService.getIndexSizeStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("大小统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 获取索引碎片率统计
     * GET /api/v1/search/stats/fragmentation
     */
    @GetMapping("/stats/fragmentation")
    public ResponseEntity<Map<String, Object>> getIndexFragmentationStats() {
        
        try {
            Map<String, Object> stats = messageSearchService.getIndexFragmentationStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("碎片率统计失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ==================== 索引管理接口 ====================
    
    /**
     * 刷新索引
     * POST /api/v1/search/index/refresh
     */
    @PostMapping("/index/refresh")
    public ResponseEntity<Map<String, Object>> refreshIndex() {
        
        try {
            messageSearchService.refreshIndex();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "索引刷新成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("刷新索引失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 优化索引
     * POST /api/v1/search/index/optimize
     */
    @PostMapping("/index/optimize")
    public ResponseEntity<Map<String, Object>> optimizeIndex() {
        
        try {
            messageSearchService.optimizeIndex();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "索引优化成功");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("优化索引失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 删除过期消息
     * DELETE /api/v1/search/index/expired
     */
    @DeleteMapping("/index/expired")
    public ResponseEntity<Map<String, Object>> deleteExpiredMessages() {
        
        try {
            int count = messageSearchService.deleteExpiredMessages();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "删除过期消息成功");
            response.put("deletedCount", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("删除过期消息失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    /**
     * 清除所有索引数据（管理员操作）
     * DELETE /api/v1/search/index/clear
     */
    @DeleteMapping("/index/clear")
    public ResponseEntity<Map<String, Object>> clearAllIndex() {
        
        try {
            int count = messageSearchService.clearAllIndex();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "清除索引数据成功");
            response.put("deletedCount", count);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return buildErrorResponse("清除索引数据失败：" + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private Map<String, Object> buildPaginationInfo(Page<?> page) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("currentPage", page.getNumber());
        pagination.put("pageSize", page.getSize());
        pagination.put("numberOfElements", page.getNumberOfElements());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        pagination.put("first", page.isFirst());
        pagination.put("last", page.isLast());
        return pagination;
    }
    
    private Map<String, Object> buildFilterInfo(MessageSearchService.AdvancedSearchRequest request) {
        Map<String, Object> filters = new HashMap<>();
        if (request.getSessionId() != null) filters.put("sessionId", request.getSessionId());
        if (request.getSenderId() != null) filters.put("senderId", request.getSenderId());
        if (request.getMessageType() != null) filters.put("messageType", request.getMessageType());
        if (request.getStartTime() != null) filters.put("startTime", request.getStartTime());
        if (request.getEndTime() != null) filters.put("endTime", request.getEndTime());
        if (request.getHasAttachment() != null) filters.put("hasAttachment", request.getHasAttachment());
        if (request.getIsEncrypted() != null) filters.put("isEncrypted", request.getIsEncrypted());
        if (request.getLanguage() != null) filters.put("language", request.getLanguage());
        if (request.getMinPriority() != null) filters.put("minPriority", request.getMinPriority());
        return filters;
    }
    
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, HttpStatus status) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.status(status).body(response);
    }
}