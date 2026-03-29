package com.im.backend.modules.miniprogram.market.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.common.api.PageResult;
import com.im.backend.modules.miniprogram.market.dto.*;
import com.im.backend.modules.miniprogram.market.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序应用市场控制器
 */
@Tag(name = "小程序应用市场", description = "本地生活场景化小程序发现、搜索、推荐")
@RestController
@RequestMapping("/api/v1/miniprogram/market")
@RequiredArgsConstructor
@Validated
public class MiniProgramMarketController {

    private final MiniProgramAppService appService;
    private final MiniProgramReviewService reviewService;
    private final MiniProgramFavoriteService favoriteService;
    private final MiniProgramCategoryService categoryService;

    // ==================== 分类接口 ====================
    
    @Operation(summary = "获取分类树")
    @GetMapping("/categories/tree")
    public ApiResponse<List<CategoryResponse>> getCategoryTree() {
        return ApiResponse.success(categoryService.getCategoryTree());
    }

    @Operation(summary = "获取顶级分类")
    @GetMapping("/categories/top")
    public ApiResponse<List<CategoryResponse>> getTopCategories() {
        return ApiResponse.success(categoryService.getTopCategories());
    }

    @Operation(summary = "获取子分类")
    @GetMapping("/categories/{parentCode}/sub")
    public ApiResponse<List<CategoryResponse>> getSubCategories(@PathVariable String parentCode) {
        return ApiResponse.success(categoryService.getSubCategories(parentCode));
    }

    @Operation(summary = "根据场景获取分类")
    @GetMapping("/categories/scene/{sceneType}")
    public ApiResponse<List<CategoryResponse>> getCategoriesByScene(@PathVariable Integer sceneType) {
        return ApiResponse.success(categoryService.getCategoriesByScene(sceneType));
    }

    // ==================== 小程序发现接口 ====================
    
    @Operation(summary = "搜索小程序")
    @PostMapping("/apps/search")
    public ApiResponse<PageResult<MiniProgramListItem>> searchApps(@RequestBody MiniProgramSearchRequest request) {
        return ApiResponse.success(appService.searchApps(request));
    }

    @Operation(summary = "获取小程序详情")
    @GetMapping("/apps/{appId}")
    public ApiResponse<MiniProgramResponse> getAppDetail(@PathVariable Long appId, 
                                                          @RequestParam(required = false) Long userId) {
        return ApiResponse.success(appService.getAppDetail(appId, userId));
    }

    @Operation(summary = "获取推荐小程序")
    @PostMapping("/apps/recommend")
    public ApiResponse<List<MiniProgramListItem>> getRecommendApps(@RequestBody MiniProgramRecommendRequest request) {
        return ApiResponse.success(appService.getRecommendApps(request));
    }

    @Operation(summary = "获取分类下的小程序")
    @GetMapping("/apps/category/{categoryCode}")
    public ApiResponse<PageResult<MiniProgramListItem>> getAppsByCategory(
            @PathVariable String categoryCode,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return ApiResponse.success(appService.getAppsByCategory(categoryCode, pageNum, pageSize));
    }

    @Operation(summary = "获取热门小程序榜单")
    @GetMapping("/apps/hot")
    public ApiResponse<List<MiniProgramListItem>> getHotApps(@RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(appService.getHotApps(limit));
    }

    @Operation(summary = "获取新品小程序")
    @GetMapping("/apps/new")
    public ApiResponse<List<MiniProgramListItem>> getNewApps(@RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(appService.getNewApps(limit));
    }

    @Operation(summary = "获取小程序统计信息")
    @GetMapping("/apps/{appId}/statistics")
    public ApiResponse<MiniProgramStatistics> getAppStatistics(@PathVariable Long appId) {
        return ApiResponse.success(appService.getAppStatistics(appId));
    }

    @Operation(summary = "上报小程序使用")
    @PostMapping("/apps/{appId}/usage")
    public ApiResponse<Void> reportAppUsage(@PathVariable Long appId,
                                             @RequestParam Long userId,
                                             @RequestParam Integer duration) {
        appService.reportAppUsage(appId, userId, duration);
        return ApiResponse.success();
    }

    // ==================== 评论接口 ====================
    
    @Operation(summary = "提交评分评论")
    @PostMapping("/reviews")
    public ApiResponse<ReviewResponse> submitReview(@RequestParam Long userId,
                                                     @RequestBody @Validated SubmitReviewRequest request) {
        return ApiResponse.success(reviewService.submitReview(userId, request));
    }

    @Operation(summary = "获取小程序评论列表")
    @GetMapping("/apps/{appId}/reviews")
    public ApiResponse<PageResult<ReviewResponse>> getAppReviews(
            @PathVariable Long appId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ApiResponse.success(reviewService.getAppReviews(appId, pageNum, pageSize));
    }

    @Operation(summary = "获取用户评论列表")
    @GetMapping("/reviews/user/{userId}")
    public ApiResponse<List<ReviewResponse>> getUserReviews(@PathVariable Long userId) {
        return ApiResponse.success(reviewService.getUserReviews(userId));
    }

    @Operation(summary = "开发者回复评论")
    @PostMapping("/reviews/{reviewId}/reply")
    public ApiResponse<Boolean> replyReview(@PathVariable Long reviewId,
                                             @RequestParam Long developerId,
                                             @RequestParam String reply) {
        return ApiResponse.success(reviewService.replyReview(reviewId, developerId, reply));
    }

    @Operation(summary = "点赞评论")
    @PostMapping("/reviews/{reviewId}/like")
    public ApiResponse<Boolean> likeReview(@PathVariable Long reviewId) {
        return ApiResponse.success(reviewService.likeReview(reviewId));
    }

    // ==================== 收藏接口 ====================
    
    @Operation(summary = "收藏小程序")
    @PostMapping("/favorites")
    public ApiResponse<Boolean> favoriteApp(@RequestParam Long userId,
                                            @RequestBody @Validated FavoriteRequest request) {
        return ApiResponse.success(favoriteService.favoriteApp(userId, request));
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/favorites/{appId}")
    public ApiResponse<Boolean> unfavoriteApp(@RequestParam Long userId, @PathVariable Long appId) {
        return ApiResponse.success(favoriteService.unfavoriteApp(userId, appId));
    }

    @Operation(summary = "获取用户收藏列表")
    @GetMapping("/favorites/user/{userId}")
    public ApiResponse<PageResult<MiniProgramListItem>> getUserFavorites(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        return ApiResponse.success(favoriteService.getUserFavorites(userId, pageNum, pageSize));
    }

    @Operation(summary = "检查是否已收藏")
    @GetMapping("/favorites/check")
    public ApiResponse<Boolean> isFavorited(@RequestParam Long userId, @RequestParam Long appId) {
        return ApiResponse.success(favoriteService.isFavorited(userId, appId));
    }

    @Operation(summary = "获取常用小程序")
    @GetMapping("/favorites/frequent/{userId}")
    public ApiResponse<List<MiniProgramListItem>> getFrequentlyUsedApps(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") Integer limit) {
        return ApiResponse.success(favoriteService.getFrequentlyUsedApps(userId, limit));
    }
}
