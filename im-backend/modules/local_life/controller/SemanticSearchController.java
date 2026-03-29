package com.im.backend.modules.local_life.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.local_life.dto.*;
import com.im.backend.modules.local_life.service.IntentRecognitionService;
import com.im.backend.modules.local_life.service.SemanticSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 语义搜索控制器
 * 处理自然语言POI搜索
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@RestController
@RequestMapping("/api/v1/semantic-search")
@RequiredArgsConstructor
@Tag(name = "语义搜索", description = "本地生活智能语义搜索API")
public class SemanticSearchController {

    private final SemanticSearchService semanticSearchService;
    private final IntentRecognitionService intentRecognitionService;

    @PostMapping("/search")
    @Operation(summary = "自然语言搜索", description = "使用自然语言搜索附近的POI")
    public Result<List<SemanticSearchResultDTO>> search(
            @RequestBody @Validated NaturalLanguageSearchRequestDTO request,
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        List<SemanticSearchResultDTO> results = semanticSearchService.search(request, userId);
        return Result.success(results);
    }

    @PostMapping("/intent")
    @Operation(summary = "识别搜索意图", description = "分析自然语言查询的搜索意图")
    public Result<SearchIntentDTO> recognizeIntent(
            @Parameter(description = "查询文本", required = true) @RequestParam String query,
            @Parameter(description = "会话ID") @RequestParam(required = false) String sessionId) {
        java.util.Map<String, Object> context = sessionId != null ? Map.of("sessionId", sessionId) : null;
        SearchIntentDTO intent = intentRecognitionService.recognizeIntent(query, context);
        return Result.success(intent);
    }

    @GetMapping("/suggestions")
    @Operation(summary = "搜索建议", description = "获取输入时的搜索建议")
    public Result<List<String>> getSuggestions(
            @Parameter(description = "输入文本", required = true) @RequestParam String query,
            @Parameter(description = "纬度") @RequestParam(required = false) Double latitude,
            @Parameter(description = "经度") @RequestParam(required = false) Double longitude) {
        List<String> suggestions = semanticSearchService.getSearchSuggestions(query, latitude, longitude);
        return Result.success(suggestions);
    }

    @GetMapping("/hot-searches")
    @Operation(summary = "热门搜索", description = "获取当前位置的热门搜索词")
    public Result<List<String>> getHotSearches(
            @Parameter(description = "纬度") @RequestParam(required = false) Double latitude,
            @Parameter(description = "经度") @RequestParam(required = false) Double longitude,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "10") int limit) {
        List<String> hotSearches = semanticSearchService.getHotSearches(latitude, longitude, limit);
        return Result.success(hotSearches);
    }

    @GetMapping("/history")
    @Operation(summary = "搜索历史", description = "获取用户的搜索历史")
    public Result<List<String>> getSearchHistory(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId,
            @Parameter(description = "返回数量") @RequestParam(defaultValue = "20") int limit) {
        List<String> history = semanticSearchService.getSearchHistory(userId, limit);
        return Result.success(history);
    }

    @DeleteMapping("/history")
    @Operation(summary = "清除搜索历史", description = "清除用户的搜索历史")
    public Result<Void> clearSearchHistory(
            @Parameter(description = "用户ID", required = true) @RequestParam Long userId) {
        semanticSearchService.clearSearchHistory(userId);
        return Result.success();
    }

    @PostMapping("/context-search")
    @Operation(summary = "上下文搜索", description = "基于会话上下文的搜索")
    public Result<List<SemanticSearchResultDTO>> searchWithContext(
            @Parameter(description = "会话ID", required = true) @RequestParam String sessionId,
            @Parameter(description = "查询文本", required = true) @RequestParam String query) {
        List<SemanticSearchResultDTO> results = semanticSearchService.searchWithContext(sessionId, query);
        return Result.success(results);
    }

    @PostMapping("/clarify")
    @Operation(summary = "获取澄清问题", description = "当意图不明确时获取澄清问题")
    public Result<List<String>> getClarificationQuestions(
            @Parameter(description = "查询文本", required = true) @RequestParam String query) {
        SearchIntentDTO intent = intentRecognitionService.recognizeIntent(query, null);
        List<String> missingEntities = intent.getNeedsClarification() ? 
                List.of("location", "price") : List.of();
        List<String> questions = intentRecognitionService.generateClarificationQuestions(query, missingEntities);
        return Result.success(questions);
    }

    @GetMapping("/intent-types")
    @Operation(summary = "获取意图类型", description = "获取系统支持的搜索意图类型")
    public Result<List<String>> getIntentTypes() {
        List<String> types = intentRecognitionService.getSupportedIntentTypes();
        return Result.success(types);
    }
}
