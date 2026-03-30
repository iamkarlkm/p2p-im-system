package com.im.backend.controller;

import com.im.backend.dto.*;
import com.im.backend.service.MessageSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息搜索控制器
 */
@RestController
@RequestMapping("/api/search")
public class MessageSearchController {

    @Autowired
    private MessageSearchService searchService;

    /**
     * 搜索消息
     */
    @PostMapping("/messages")
    public ResponseEntity<ApiResponse<Page<MessageSearchResult>>> searchMessages(
            @RequestAttribute("userId") Long userId,
            @RequestBody MessageSearchRequest request) {
        
        Page<MessageSearchResult> results = searchService.searchMessages(userId, request);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * 在指定会话中搜索
     */
    @GetMapping("/conversation/{conversationType}/{conversationId}")
    public ResponseEntity<ApiResponse<Page<MessageSearchResult>>> searchInConversation(
            @RequestAttribute("userId") Long userId,
            @PathVariable String conversationType,
            @PathVariable Long conversationId,
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<MessageSearchResult> results = searchService.searchInConversation(
            userId, conversationId, conversationType, keyword, 
            org.springframework.data.domain.PageRequest.of(page, size));
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * 高级搜索
     */
    @PostMapping("/advanced")
    public ResponseEntity<ApiResponse<Page<MessageSearchResult>>> advancedSearch(
            @RequestAttribute("userId") Long userId,
            @RequestBody MessageSearchRequest request) {
        
        Page<MessageSearchResult> results = searchService.advancedSearch(userId, request);
        return ResponseEntity.ok(ApiResponse.success(results));
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public ResponseEntity<ApiResponse<List<String>>> getSearchSuggestions(
            @RequestAttribute("userId") Long userId,
            @RequestParam String prefix) {
        
        List<String> suggestions = searchService.getSearchSuggestions(userId, prefix);
        return ResponseEntity.ok(ApiResponse.success(suggestions));
    }

    /**
     * 获取搜索历史
     */
    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<SearchHistoryDTO>>> getSearchHistory(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        
        List<SearchHistoryDTO> history = searchService.getSearchHistory(userId, limit);
        return ResponseEntity.ok(ApiResponse.success(history));
    }

    /**
     * 删除搜索历史
     */
    @DeleteMapping("/history")
    public ResponseEntity<ApiResponse<Void>> deleteSearchHistory(
            @RequestAttribute("userId") Long userId,
            @RequestParam String keyword) {
        
        searchService.deleteSearchHistory(userId, keyword);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 清空搜索历史
     */
    @DeleteMapping("/history/all")
    public ResponseEntity<ApiResponse<Void>> clearSearchHistory(
            @RequestAttribute("userId") Long userId) {
        
        searchService.clearSearchHistory(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 获取热门搜索
     */
    @GetMapping("/hot")
    public ResponseEntity<ApiResponse<List<String>>> getHotKeywords(
            @RequestParam(defaultValue = "10") int limit) {
        
        List<String> hotKeywords = searchService.getHotKeywords(limit);
        return ResponseEntity.ok(ApiResponse.success(hotKeywords));
    }

    /**
     * 高亮预览
     */
    @PostMapping("/highlight")
    public ResponseEntity<ApiResponse<Map<String, String>>> highlightContent(
            @RequestBody Map<String, String> request) {
        
        String content = request.get("content");
        String keyword = request.get("keyword");
        String highlighted = searchService.highlightKeyword(content, keyword);
        
        Map<String, String> result = new HashMap<>();
        result.put("original", content);
        result.put("highlighted", highlighted);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
