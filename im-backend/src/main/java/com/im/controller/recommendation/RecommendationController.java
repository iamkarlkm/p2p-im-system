package com.im.controller.recommendation;

import com.im.dto.recommendation.RecommendationFeedRequestDTO;
import com.im.dto.recommendation.RecommendationFeedResponseDTO;
import com.im.entity.recommendation.RecommendationItem;
import com.im.service.recommendation.RecommendationFeedService;
import com.im.common.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 推荐信息流控制器
 * 提供个性化推荐相关的REST API接口
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/recommendation")
@Api(tags = "推荐信息流", description = "个性化推荐信息流相关接口")
@Validated
public class RecommendationController {

    @Autowired
    private RecommendationFeedService recommendationFeedService;

    /**
     * 获取个性化推荐信息流
     */
    @PostMapping("/feed")
    @ApiOperation(value = "获取推荐信息流", notes = "根据用户位置、场景获取个性化推荐内容")
    public Result<RecommendationFeedResponseDTO> getRecommendationFeed(
            @Valid @RequestBody RecommendationFeedRequestDTO request) {
        
        log.info("[推荐API] 获取推荐信息流, userId={}, scene={}", 
                request.getUserId(), request.getScene());
        
        RecommendationFeedResponseDTO response = recommendationFeedService.getRecommendationFeed(request);
        
        if ("SUCCESS".equals(response.getStatus())) {
            return Result.success(response);
        } else {
            return Result.error(response.getCode(), response.getMessage());
        }
    }

    /**
     * 获取附近推荐
     */
    @GetMapping("/nearby")
    @ApiOperation(value = "获取附近推荐", notes = "根据地理位置获取附近的热门推荐")
    public Result<List<RecommendationItem>> getNearbyRecommendations(
            @ApiParam("经度") @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @ApiParam("纬度") @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @ApiParam("搜索半径(米),默认5000") @RequestParam(defaultValue = "5000") Integer radius,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        
        log.info("[推荐API] 获取附近推荐, longitude={}, latitude={}, radius={}", 
                longitude, latitude, radius);
        
        List<RecommendationItem> items = recommendationFeedService.getNearbyRecommendations(
            longitude, latitude, radius, pageNum, pageSize);
        
        return Result.success(items);
    }

    /**
     * 获取首页信息流
     */
    @GetMapping("/home-feed")
    @ApiOperation(value = "获取首页信息流", notes = "获取首页个性化推荐内容")
    public Result<List<RecommendationItem>> getHomeFeed(
            @ApiParam("用户ID") @RequestParam(required = false) String userId,
            @ApiParam("经度") @RequestParam @NotNull Double longitude,
            @ApiParam("纬度") @RequestParam @NotNull Double latitude,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        
        log.info("[推荐API] 获取首页信息流, userId={}, longitude={}, latitude={}", 
                userId, longitude, latitude);
        
        List<RecommendationItem> items = recommendationFeedService.getHomeFeed(
            userId, longitude, latitude, pageNum, pageSize);
        
        return Result.success(items);
    }

    /**
     * 获取发现页推荐
     */
    @GetMapping("/discover")
    @ApiOperation(value = "获取发现页推荐", notes = "获取发现页的探索性推荐内容")
    public Result<List<RecommendationItem>> getDiscoverFeed(
            @ApiParam("用户ID") @RequestParam(required = false) String userId,
            @ApiParam("经度") @RequestParam @NotNull Double longitude,
            @ApiParam("纬度") @RequestParam @NotNull Double latitude,
            @ApiParam("分类筛选") @RequestParam(required = false) String category,
            @ApiParam("页码") @RequestParam(defaultValue = "1") Integer pageNum,
            @ApiParam("每页大小") @RequestParam(defaultValue = "20") Integer pageSize) {
        
        log.info("[推荐API] 获取发现页推荐, userId={}, category={}", userId, category);
        
        List<RecommendationItem> items = recommendationFeedService.getDiscoverFeed(
            userId, longitude, latitude, category, pageNum, pageSize);
        
        return Result.success(items);
    }

    /**
     * 获取猜你喜欢
     */
    @GetMapping("/guess-you-like")
    @ApiOperation(value = "获取猜你喜欢", notes = "根据用户偏好推荐可能感兴趣的内容")
    public Result<List<RecommendationItem>> getGuessYouLike(
            @ApiParam("用户ID") @RequestParam(required = false) String userId,
            @ApiParam("数量限制") @RequestParam(defaultValue = "20") Integer limit) {
        
        log.info("[推荐API] 获取猜你喜欢, userId={}, limit={}", userId, limit);
        
        List<RecommendationItem> items = recommendationFeedService.getGuessYouLike(userId, limit);
        
        return Result.success(items);
    }

    /**
     * 获取场景化推荐
     */
    @GetMapping("/scene-based")
    @ApiOperation(value = "获取场景化推荐", notes = "根据场景标签获取场景化推荐内容")
    public Result<List<RecommendationItem>> getSceneBasedRecommendations(
            @ApiParam("用户ID") @RequestParam(required = false) String userId,
            @ApiParam("场景标签") @RequestParam @NotBlank String sceneTag,
            @ApiParam("经度") @RequestParam(required = false) Double longitude,
            @ApiParam("纬度") @RequestParam(required = false) Double latitude,
            @ApiParam("数量限制") @RequestParam(defaultValue = "20") Integer limit) {
        
        log.info("[推荐API] 获取场景化推荐, userId={}, sceneTag={}", userId, sceneTag);
        
        List<RecommendationItem> items = recommendationFeedService.getSceneBasedRecommendations(
            userId, sceneTag, longitude, latitude, limit);
        
        return Result.success(items);
    }

    /**
     * 获取相似推荐
     */
    @GetMapping("/similar")
    @ApiOperation(value = "获取相似推荐", notes = "获取与指定物品相似的内容")
    public Result<List<RecommendationItem>> getSimilarRecommendations(
            @ApiParam("物品ID") @RequestParam @NotBlank String itemId,
            @ApiParam("物品类型") @RequestParam @NotBlank String itemType,
            @ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("[推荐API] 获取相似推荐, itemId={}, itemType={}", itemId, itemType);
        
        List<RecommendationItem> items = recommendationFeedService.getSimilarRecommendations(
            itemId, itemType, limit);
        
        return Result.success(items);
    }

    /**
     * 获取相关推荐
     */
    @GetMapping("/related")
    @ApiOperation(value = "获取相关推荐", notes = "获取与指定物品相关的内容")
    public Result<List<RecommendationItem>> getRelatedRecommendations(
            @ApiParam("物品ID") @RequestParam @NotBlank String itemId,
            @ApiParam("物品类型") @RequestParam @NotBlank String itemType,
            @ApiParam("用户ID") @RequestParam(required = false) String userId,
            @ApiParam("数量限制") @RequestParam(defaultValue = "10") Integer limit) {
        
        log.info("[推荐API] 获取相关推荐, itemId={}, itemType={}", itemId, itemType);
        
        List<RecommendationItem> items = recommendationFeedService.getRelatedRecommendations(
            itemId, itemType, userId, limit);
        
        return Result.success(items);
    }

    /**
     * 上报曝光
     */
    @PostMapping("/report/impression")
    @ApiOperation(value = "上报曝光", notes = "上报推荐项的曝光事件，用于模型优化")
    public Result<Void> reportImpression(
            @ApiParam("用户ID") @RequestParam(required = false) String userId,
            @ApiParam("物品ID") @RequestParam @NotBlank String itemId,
            @ApiParam("场景") @RequestParam @NotBlank String scene,
            @ApiParam("位置") @RequestParam @Min(0) Integer position) {
        
        recommendationFeedService.reportImpression(userId, itemId, scene, position, null);
        
        return Result.success();
    }

    /**
     * 上报点击
     */
    @PostMapping("/report/click")
    @ApiOperation(value = "上报点击", notes = "上报推荐项的点击事件，用于模型优化")
    public Result<Void> reportClick(
            @ApiParam("用户ID") @RequestParam(required = false) String userId,
            @ApiParam("物品ID") @RequestParam @NotBlank String itemId,
            @ApiParam("场景") @RequestParam @NotBlank String scene,
            @ApiParam("位置") @RequestParam @Min(0) Integer position) {
        
        recommendationFeedService.reportClick(userId, itemId, scene, position, null);
        
        return Result.success();
    }

    /**
     * 上报转化
     */
    @PostMapping("/report/conversion")
    @ApiOperation(value = "上报转化", notes = "上报推荐项的转化事件（如下单、预约等）")
    public Result<Void> reportConversion(
            @ApiParam("用户ID") @RequestParam(required = false) String userId,
            @ApiParam("物品ID") @RequestParam @NotBlank String itemId,
            @ApiParam("场景") @RequestParam @NotBlank String scene,
            @ApiParam("转化类型") @RequestParam @NotBlank String conversionType) {
        
        recommendationFeedService.reportConversion(userId, itemId, scene, conversionType, null);
        
        return Result.success();
    }

    /**
     * 刷新推荐缓存
     */
    @PostMapping("/refresh-cache")
    @ApiOperation(value = "刷新推荐缓存", notes = "刷新用户的推荐缓存，获取最新推荐内容")
    public Result<Void> refreshRecommendationCache(
            @ApiParam("用户ID") @RequestParam @NotBlank String userId,
            @ApiParam("场景") @RequestParam(required = false) String scene) {
        
        recommendationFeedService.refreshRecommendationCache(userId, scene);
        
        return Result.success();
    }
}
