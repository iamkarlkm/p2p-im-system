package com.im.backend.modules.local.search.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.local.search.dto.*;
import com.im.backend.modules.local.search.entity.LocalSearchQuery;
import com.im.backend.modules.local.search.service.LocalSmartSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 本地生活智能搜索控制器
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/local/search")
@RequiredArgsConstructor
@Tag(name = "本地生活智能搜索", description = "智能搜索、语义搜索、知识图谱相关接口")
public class LocalSmartSearchController {
    
    private final LocalSmartSearchService searchService;
    
    /**
     * 智能搜索
     */
    @PostMapping("/smart")
    @Operation(summary = "智能搜索", description = "支持自然语言查询理解的智能搜索")
    public Result<SmartSearchResponse> smartSearch(
            @RequestBody @Validated SmartSearchRequest request) {
        log.info("智能搜索请求: query={}, userId={}", request.getQuery(), request.getUserId());
        
        SmartSearchResponse response = searchService.smartSearch(request);
        return Result.success(response);
    }
    
    /**
     * 语义搜索
     */
    @PostMapping("/semantic")
    @Operation(summary = "语义搜索", description = "自然语言语义理解与搜索")
    public Result<SemanticSearchResponse> semanticSearch(
            @RequestBody @Validated SemanticSearchRequest request) {
        log.info("语义搜索请求: query={}", request.getNaturalQuery());
        
        SemanticSearchResponse response = searchService.semanticSearch(request);
        return Result.success(response);
    }
    
    /**
     * 多轮对话搜索
     */
    @PostMapping("/multi-turn/{conversationId}")
    @Operation(summary = "多轮对话搜索", description = "支持上下文的多轮对话搜索")
    public Result<SmartSearchResponse> multiTurnSearch(
            @Parameter(description = "对话会话ID") @PathVariable String conversationId,
            @RequestBody @Validated SmartSearchRequest request) {
        log.info("多轮对话搜索: conversationId={}, query={}", conversationId, request.getQuery());
        
        SmartSearchResponse response = searchService.multiTurnSearch(request, conversationId);
        return Result.success(response);
    }
    
    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    @Operation(summary = "搜索建议", description = "根据输入前缀获取搜索建议")
    public Result<List<String>> getSearchSuggestions(
            @Parameter(description = "输入前缀") @RequestParam String prefix,
            @Parameter(description = "经度") @RequestParam(required = false) Double longitude,
            @Parameter(description = "纬度") @RequestParam(required = false) Double latitude,
            @Parameter(description = "返回数量") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<String> suggestions = searchService.getSearchSuggestions(prefix, longitude, latitude, limit);
        return Result.success(suggestions);
    }
    
    /**
     * 获取热门搜索
     */
    @GetMapping("/hot")
    @Operation(summary = "热门搜索", description = "获取当前热门搜索关键词")
    public Result<List<String>> getHotSearches(
            @Parameter(description = "经度") @RequestParam(required = false) Double longitude,
            @Parameter(description = "纬度") @RequestParam(required = false) Double latitude,
            @Parameter(description = "返回数量") @RequestParam(required = false, defaultValue = "10") Integer limit) {
        
        List<String> hotSearches = searchService.getHotSearches(longitude, latitude, limit);
        return Result.success(hotSearches);
    }
    
    /**
     * 获取知识图谱推荐
     */
    @GetMapping("/kg-recommendations/{poiId}")
    @Operation(summary = "知识图谱推荐", description = "基于知识图谱的POI推荐")
    public Result<List<SmartSearchResponse.KnowledgeGraphRecommendationDTO>> getKgRecommendations(
            @Parameter(description = "POI ID") @PathVariable Long poiId,
            @Parameter(description = "推荐数量") @RequestParam(required = false, defaultValue = "5") Integer limit) {
        
        List<SmartSearchResponse.KnowledgeGraphRecommendationDTO> recommendations = 
                searchService.getKgRecommendations(poiId, limit);
        return Result.success(recommendations);
    }
    
    /**
     * 记录搜索点击
     */
    @PostMapping("/click/{queryId}")
    @Operation(summary = "记录搜索点击", description = "记录用户在搜索结果中的点击行为")
    public Result<Void> recordSearchClick(
            @Parameter(description = "查询ID") @PathVariable Long queryId,
            @Parameter(description = "点击的POI ID") @RequestParam Long poiId,
            @Parameter(description = "点击的位置") @RequestParam Integer index) {
        
        searchService.recordSearchClick(queryId, poiId, index);
        return Result.success();
    }
    
    /**
     * 获取搜索历史
     */
    @GetMapping("/history/{userId}")
    @Operation(summary = "搜索历史", description = "获取用户的搜索历史")
    public Result<List<LocalSearchQuery>> getSearchHistory(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "返回数量") @RequestParam(required = false, defaultValue = "20") Integer limit) {
        
        List<LocalSearchQuery> history = searchService.getSearchHistory(userId, limit);
        return Result.success(history);
    }
    
    /**
     * 清空搜索历史
     */
    @DeleteMapping("/history/{userId}")
    @Operation(summary = "清空搜索历史", description = "清空用户的搜索历史")
    public Result<Void> clearSearchHistory(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        
        searchService.clearSearchHistory(userId);
        return Result.success();
    }
    
    /**
     * 删除单条搜索历史
     */
    @DeleteMapping("/history/{userId}/{queryId}")
    @Operation(summary = "删除搜索历史", description = "删除指定的搜索历史记录")
    public Result<Void> deleteSearchHistory(
            @Parameter(description = "用户ID") @PathVariable Long userId,
            @Parameter(description = "查询ID") @PathVariable Long queryId) {
        
        searchService.deleteSearchHistory(userId, queryId);
        return Result.success();
    }
    
    /**
     * 语音搜索
     */
    @PostMapping("/voice")
    @Operation(summary = "语音搜索", description = "语音输入智能搜索")
    public Result<SmartSearchResponse> voiceSearch(
            @RequestBody VoiceSearchRequest request) {
        log.info("语音搜索请求: dialect={}", request.getDialect());
        
        SmartSearchResponse response = searchService.voiceSearch(
                request.getAudioData(),
                request.getLongitude(),
                request.getLatitude(),
                request.getDialect()
        );
        return Result.success(response);
    }
    
    /**
     * 获取搜索统计
     */
    @GetMapping("/statistics")
    @Operation(summary = "搜索统计", description = "获取搜索统计数据")
    public Result<Map<String, Object>> getSearchStatistics(
            @Parameter(description = "开始时间") @RequestParam String startTime,
            @Parameter(description = "结束时间") @RequestParam String endTime) {
        
        Map<String, Object> statistics = searchService.getSearchStatistics(startTime, endTime);
        return Result.success(statistics);
    }
    
    /**
     * 快速搜索（简化版）
     */
    @GetMapping("/quick")
    @Operation(summary = "快速搜索", description = "GET方式的快速搜索")
    public Result<SmartSearchResponse> quickSearch(
            @Parameter(description = "搜索关键词") @RequestParam String query,
            @Parameter(description = "经度") @RequestParam Double longitude,
            @Parameter(description = "纬度") @RequestParam Double latitude,
            @Parameter(description = "搜索半径") @RequestParam(required = false, defaultValue = "5000") Integer radius,
            @Parameter(description = "排序方式") @RequestParam(required = false, defaultValue = "SMART") String sortBy,
            @Parameter(description = "页码") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页数量") @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        
        SmartSearchRequest request = SmartSearchRequest.builder()
                .query(query)
                .longitude(longitude)
                .latitude(latitude)
                .radius(radius)
                .sortBy(sortBy)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
        
        SmartSearchResponse response = searchService.smartSearch(request);
        return Result.success(response);
    }
    
    // ==================== 内部请求类 ====================
    
    @lombok.Data
    public static class VoiceSearchRequest {
        private byte[] audioData;
        private Double longitude;
        private Double latitude;
        private String dialect;
    }
}
