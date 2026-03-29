package com.im.backend.modules.local_life.search.controller;

import com.im.backend.common.api.Result;
import com.im.backend.modules.local_life.search.dto.*;
import com.im.backend.modules.local_life.search.service.IntelligentSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 智能搜索控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@Tag(name = "智能搜索", description = "本地生活智能搜索与语义理解相关接口")
public class IntelligentSearchController {

    private final IntelligentSearchService intelligentSearchService;

    /**
     * 智能搜索
     */
    @PostMapping("/intelligent")
    @Operation(summary = "智能搜索", description = "支持自然语言查询的本地生活智能搜索")
    public Result<IntelligentSearchResultDTO> intelligentSearch(
            @RequestAttribute("userId") Long userId,
            @RequestBody @Validated IntelligentSearchRequestDTO request) {
        log.info("智能搜索请求 - userId: {}, query: {}", userId, request.getQuery());
        IntelligentSearchResultDTO result = intelligentSearchService.search(userId, request);
        return Result.success(result);
    }

    /**
     * 语义理解
     */
    @PostMapping("/understand")
    @Operation(summary = "语义理解", description = "分析搜索查询的意图和提取实体")
    public Result<SemanticUnderstandingResultDTO> semanticUnderstand(
            @RequestBody @Validated SemanticUnderstandingRequestDTO request) {
        log.info("语义理解请求 - query: {}", request.getQuery());
        SemanticUnderstandingResultDTO result = intelligentSearchService.understand(request);
        return Result.success(result);
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    @Operation(summary = "搜索建议", description = "根据输入获取搜索建议")
    public Result<SearchSuggestionResultDTO> getSuggestions(
            @RequestAttribute("userId") Long userId,
            @RequestParam String keyword,
            @RequestParam(required = false) String cityCode,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        SearchSuggestionRequestDTO request = new SearchSuggestionRequestDTO();
        request.setKeyword(keyword);
        request.setCityCode(cityCode);
        request.setLongitude(longitude);
        request.setLatitude(latitude);
        request.setLimit(limit);
        SearchSuggestionResultDTO result = intelligentSearchService.getSuggestions(userId, request);
        return Result.success(result);
    }

    /**
     * 获取热门搜索
     */
    @GetMapping("/hot")
    @Operation(summary = "热门搜索", description = "获取当前热门搜索词")
    public Result<List<SearchTrendDTO>> getHotSearches(
            @RequestParam(required = false) String cityCode,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {
        List<SearchTrendDTO> result = intelligentSearchService.getHotSearches(cityCode, limit);
        return Result.success(result);
    }

    /**
     * 获取搜索历史
     */
    @GetMapping("/history")
    @Operation(summary = "搜索历史", description = "获取用户搜索历史")
    public Result<List<String>> getSearchHistory(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {
        List<String> history = intelligentSearchService.getSearchHistory(userId, limit);
        return Result.success(history);
    }

    /**
     * 清除搜索历史
     */
    @DeleteMapping("/history")
    @Operation(summary = "清除搜索历史", description = "清除用户所有搜索历史")
    public Result<Void> clearSearchHistory(@RequestAttribute("userId") Long userId) {
        intelligentSearchService.clearSearchHistory(userId);
        return Result.success();
    }

    /**
     * 删除单条搜索历史
     */
    @DeleteMapping("/history/{keyword}")
    @Operation(summary = "删除搜索历史", description = "删除指定搜索历史记录")
    public Result<Void> deleteSearchHistory(
            @RequestAttribute("userId") Long userId,
            @PathVariable String keyword) {
        intelligentSearchService.deleteSearchHistory(userId, keyword);
        return Result.success();
    }

    /**
     * 获取搜索发现
     */
    @GetMapping("/discovery")
    @Operation(summary = "搜索发现", description = "获取个性化搜索发现")
    public Result<List<String>> getSearchDiscovery(
            @RequestAttribute("userId") Long userId,
            @RequestParam(required = false) String cityCode) {
        List<String> discovery = intelligentSearchService.getSearchDiscovery(userId, cityCode);
        return Result.success(discovery);
    }
}
