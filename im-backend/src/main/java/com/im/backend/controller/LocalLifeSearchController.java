package com.im.backend.controller;

import com.im.backend.common.Result;
import com.im.backend.dto.SearchRequest;
import com.im.backend.dto.SearchResponse;
import com.im.backend.service.ILocalSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 本地生活搜索控制器
 * 提供POI搜索、商户搜索、搜索建议等功能
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/local-life/search")
@RequiredArgsConstructor
@Tag(name = "本地生活搜索", description = "本地生活POI搜索、商户搜索、搜索建议相关接口")
public class LocalLifeSearchController {

    private final ILocalSearchService localSearchService;

    /**
     * 本地生活综合搜索
     */
    @PostMapping
    @Operation(summary = "本地生活搜索", description = "搜索附近POI、商户、优惠券等")
    public Result<SearchResponse> search(
            @RequestBody @Validated SearchRequest request,
            @RequestAttribute(value = "userId", required = false) Long userId) {
        log.info("Local life search: keyword={}, city={}, userId={}", 
                request.getKeyword(), request.getCityCode(), userId);
        
        SearchResponse response = localSearchService.search(request, userId);
        return Result.success(response);
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    @Operation(summary = "获取搜索建议", description = "根据输入关键词返回搜索建议")
    public Result<List<String>> getSearchSuggestions(
            @RequestParam @Parameter(description = "搜索关键词") String keyword,
            @RequestParam(required = false) @Parameter(description = "城市编码") String cityCode) {
        List<String> suggestions = localSearchService.getSearchSuggestions(keyword, cityCode);
        return Result.success(suggestions);
    }

    /**
     * 获取热门搜索词
     */
    @GetMapping("/hot-keywords")
    @Operation(summary = "获取热门搜索词", description = "获取当前城市热门搜索关键词")
    public Result<List<String>> getHotKeywords(
            @RequestParam(required = false) @Parameter(description = "城市编码") String cityCode,
            @RequestParam(defaultValue = "10") @Parameter(description = "返回数量") Integer limit) {
        List<String> hotWords = localSearchService.getHotKeywords(cityCode, limit);
        return Result.success(hotWords);
    }

    /**
     * 获取用户搜索历史
     */
    @GetMapping("/history")
    @Operation(summary = "获取用户搜索历史", description = "获取当前用户的搜索历史记录")
    public Result<List<String>> getUserSearchHistory(
            @RequestAttribute("userId") Long userId,
            @RequestParam(defaultValue = "10") @Parameter(description = "返回数量") Integer limit) {
        List<String> history = localSearchService.getUserSearchHistory(userId, limit);
        return Result.success(history);
    }

    /**
     * 清除用户搜索历史
     */
    @DeleteMapping("/history")
    @Operation(summary = "清除搜索历史", description = "清除当前用户的所有搜索历史")
    public Result<Void> clearUserSearchHistory(@RequestAttribute("userId") Long userId) {
        localSearchService.clearUserSearchHistory(userId);
        return Result.success();
    }

    /**
     * 获取POI详情
     */
    @GetMapping("/poi/{poiId}")
    @Operation(summary = "获取POI详情", description = "根据POI ID获取详细信息")
    public Result<SearchResponse.SearchResultItem> getPoiDetail(
            @PathVariable @Parameter(description = "POI ID") Long poiId) {
        SearchResponse.SearchResultItem detail = localSearchService.getPoiDetail(poiId);
        return Result.success(detail);
    }

    /**
     * 记录搜索点击
     */
    @PostMapping("/click")
    @Operation(summary = "记录搜索点击", description = "记录用户点击搜索结果的行为")
    public Result<Void> recordSearchClick(
            @RequestParam @Parameter(description = "搜索记录ID") Long searchId,
            @RequestParam @Parameter(description = "POI ID") Long poiId) {
        localSearchService.recordSearchClick(searchId, poiId);
        return Result.success();
    }

    /**
     * 获取附近推荐
     */
    @GetMapping("/nearby-recommendations")
    @Operation(summary = "获取附近推荐", description = "根据位置获取附近推荐商户")
    public Result<List<SearchResponse.SearchResultItem>> getNearbyRecommendations(
            @RequestParam @Parameter(description = "经度") Double longitude,
            @RequestParam @Parameter(description = "纬度") Double latitude,
            @RequestParam(defaultValue = "10") @Parameter(description = "返回数量") Integer limit) {
        List<SearchResponse.SearchResultItem> recommendations = 
                localSearchService.getNearbyRecommendations(longitude, latitude, limit);
        return Result.success(recommendations);
    }

    /**
     * 附近商户搜索(简化接口)
     */
    @GetMapping("/nearby")
    @Operation(summary = "附近商户搜索", description = "搜索指定位置附近的商户")
    public Result<SearchResponse> searchNearby(
            @RequestParam @Parameter(description = "经度") Double longitude,
            @RequestParam @Parameter(description = "纬度") Double latitude,
            @RequestParam(defaultValue = "3000") @Parameter(description = "搜索半径(米)") Integer radius,
            @RequestParam(required = false) @Parameter(description = "关键词") String keyword,
            @RequestParam(defaultValue = "SMART") @Parameter(description = "排序方式") String sortBy,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        
        SearchRequest request = SearchRequest.builder()
                .longitude(longitude)
                .latitude(latitude)
                .radius(radius)
                .keyword(keyword)
                .sortBy(sortBy)
                .pageNum(pageNum)
                .pageSize(pageSize)
                .build();
        
        SearchResponse response = localSearchService.search(request, null);
        return Result.success(response);
    }
}
