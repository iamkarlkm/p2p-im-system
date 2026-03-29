package com.im.backend.modules.local.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.modules.local.dto.PersonalizedRecommendationRequest;
import com.im.backend.modules.local.dto.PersonalizedRecommendationResponse;
import com.im.backend.modules.local.service.PersonalizedRecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 个性化推荐控制器
 * 提供多路召回推荐和智能排序API
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/local/recommendation")
@RequiredArgsConstructor
@Validated
@Tag(name = "个性化推荐", description = "本地生活个性化推荐信息流相关接口")
public class PersonalizedRecommendationController {

    private final PersonalizedRecommendationService recommendationService;

    /**
     * 获取个性化推荐
     */
    @PostMapping("/feed")
    @Operation(summary = "获取个性化推荐", description = "基于多路召回的智能推荐")
    public ApiResponse<PersonalizedRecommendationResponse> getRecommendations(
            @Valid @RequestBody PersonalizedRecommendationRequest request) {
        log.info("Getting recommendations for user: {}", request.getUserId());
        
        PersonalizedRecommendationResponse response = recommendationService.getRecommendations(request);
        
        return ApiResponse.success(response);
    }

    /**
     * 异步获取个性化推荐
     */
    @PostMapping("/feed/async")
    @Operation(summary = "获取个性化推荐（异步）", description = "异步获取推荐结果")
    public CompletableFuture<ApiResponse<PersonalizedRecommendationResponse>> getRecommendationsAsync(
            @Valid @RequestBody PersonalizedRecommendationRequest request) {
        log.info("Getting async recommendations for user: {}", request.getUserId());
        
        return recommendationService.getRecommendationsAsync(request)
                .thenApply(ApiResponse::success);
    }

    /**
     * 快速推荐 - GET方式
     */
    @GetMapping("/feed/quick")
    @Operation(summary = "快速推荐", description = "通过URL参数快速获取推荐")
    public ApiResponse<PersonalizedRecommendationResponse> quickRecommend(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "纬度") @RequestParam(required = false) Double lat,
            @Parameter(description = "经度") @RequestParam(required = false) Double lng,
            @Parameter(description = "场景") @RequestParam(required = false, defaultValue = "feed") String scene,
            @Parameter(description = "页码") @RequestParam(required = false, defaultValue = "1") Integer page,
            @Parameter(description = "每页数量") @RequestParam(required = false, defaultValue = "20") Integer pageSize) {
        
        log.info("Quick recommendation for user: {} at [{}, {}]", userId, lat, lng);
        
        PersonalizedRecommendationRequest request = PersonalizedRecommendationRequest.builder()
                .userId(userId)
                .latitude(lat)
                .longitude(lng)
                .scene(scene)
                .page(page)
                .pageSize(pageSize)
                .build();
        
        PersonalizedRecommendationResponse response = recommendationService.getRecommendations(request);
        
        return ApiResponse.success(response);
    }

    /**
     * 附近推荐
     */
    @GetMapping("/nearby")
    @Operation(summary = "附近推荐", description = "基于地理位置的附近推荐")
    public ApiResponse<PersonalizedRecommendationResponse> nearbyRecommend(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "纬度") @RequestParam Double lat,
            @Parameter(description = "经度") @RequestParam Double lng,
            @Parameter(description = "半径（米）") @RequestParam(required = false, defaultValue = "5000") Integer radius,
            @Parameter(description = "数量") @RequestParam(required = false, defaultValue = "20") Integer limit) {
        
        log.info("Nearby recommendation for user: {} at [{}, {}] with radius {}", userId, lat, lng, radius);
        
        PersonalizedRecommendationRequest request = PersonalizedRecommendationRequest.builder()
                .userId(userId)
                .latitude(lat)
                .longitude(lng)
                .scene("nearby")
                .recallStrategies(Arrays.asList("geo", "hot"))
                .page(1)
                .pageSize(limit)
                .build();
        
        PersonalizedRecommendationResponse response = recommendationService.getRecommendations(request);
        
        return ApiResponse.success(response);
    }

    /**
     * 热门推荐
     */
    @GetMapping("/popular")
    @Operation(summary = "热门推荐", description = "当前区域热门推荐")
    public ApiResponse<PersonalizedRecommendationResponse> popularRecommend(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "纬度") @RequestParam(required = false) Double lat,
            @Parameter(description = "经度") @RequestParam(required = false) Double lng,
            @Parameter(description = "数量") @RequestParam(required = false, defaultValue = "20") Integer limit) {
        
        log.info("Popular recommendation for user: {}", userId);
        
        PersonalizedRecommendationRequest request = PersonalizedRecommendationRequest.builder()
                .userId(userId)
                .latitude(lat)
                .longitude(lng)
                .scene("popular")
                .recallStrategies(Arrays.asList("hot"))
                .page(1)
                .pageSize(limit)
                .build();
        
        PersonalizedRecommendationResponse response = recommendationService.getRecommendations(request);
        
        return ApiResponse.success(response);
    }

    /**
     * 刷新推荐
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新推荐", description = "刷新推荐结果，排除已看内容")
    public ApiResponse<PersonalizedRecommendationResponse> refreshRecommendations(
            @Valid @RequestBody PersonalizedRecommendationRequest request,
            @Parameter(description = "排除的内容ID列表") @RequestParam List<String> excludeIds) {
        
        log.info("Refreshing recommendations for user: {}, excluding {} items", 
                request.getUserId(), excludeIds.size());
        
        PersonalizedRecommendationResponse response = recommendationService.refreshRecommendations(request, excludeIds);
        
        return ApiResponse.success(response);
    }

    /**
     * 记录用户反馈
     */
    @PostMapping("/feedback")
    @Operation(summary = "记录反馈", description = "记录用户对推荐的反馈")
    public ApiResponse<Void> recordFeedback(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "内容ID") @RequestParam String itemId,
            @Parameter(description = "行为类型：click/favorite/skip") @RequestParam String action,
            @Parameter(description = "上下文") @RequestParam(required = false) String context) {
        
        log.info("Recording feedback: user={}, item={}, action={}", userId, itemId, action);
        
        recommendationService.recordFeedback(userId, itemId, action, context);
        
        return ApiResponse.success();
    }
}
