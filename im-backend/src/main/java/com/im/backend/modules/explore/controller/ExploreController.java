package com.im.backend.modules.explore.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.im.backend.common.result.Result;
import com.im.backend.modules.explore.entity.*;
import com.im.backend.modules.explore.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 探店发现引擎控制器
 * 提供探店笔记、路线、打卡、达人等功能的RESTful API
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Tag(name = "探店发现引擎", description = "智能探店与发现引擎相关接口")
@RestController
@RequestMapping("/api/v1/explore")
@RequiredArgsConstructor
public class ExploreController {

    private final ExploreNoteService noteService;
    private final ExploreRouteService routeService;
    private final ExploreCheckinService checkinService;
    private final ExploreInfluencerService influencerService;

    // ==================== 探店笔记接口 ====================

    @Operation(summary = "发布探店笔记")
    @PostMapping("/notes")
    public Result<ExploreNote> publishNote(@RequestBody ExploreNote note) {
        return Result.success(noteService.publishNote(note));
    }

    @Operation(summary = "获取笔记详情")
    @GetMapping("/notes/{noteId}")
    public Result<ExploreNote> getNoteDetail(@PathVariable Long noteId) {
        return Result.success(noteService.getNoteDetail(noteId));
    }

    @Operation(summary = "分页查询笔记列表")
    @GetMapping("/notes")
    public Result<IPage<ExploreNote>> pageNotes(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer status) {
        return Result.success(noteService.pageNotes(pageNum, pageSize, status));
    }

    @Operation(summary = "获取推荐笔记列表")
    @GetMapping("/notes/recommended")
    public Result<IPage<ExploreNote>> getRecommendedNotes(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(noteService.getRecommendedNotes(userId, longitude, latitude, pageNum, pageSize));
    }

    @Operation(summary = "获取附近热门笔记")
    @GetMapping("/notes/nearby")
    public Result<IPage<ExploreNote>> getNearbyHotNotes(
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude,
            @RequestParam(defaultValue = "5000") Double radius,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(noteService.getNearbyHotNotes(longitude, latitude, radius, pageNum, pageSize));
    }

    @Operation(summary = "获取精选笔记")
    @GetMapping("/notes/featured")
    public Result<IPage<ExploreNote>> getFeaturedNotes(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(noteService.getFeaturedNotes(pageNum, pageSize));
    }

    @Operation(summary = "点赞笔记")
    @PostMapping("/notes/{noteId}/like")
    public Result<Boolean> likeNote(@PathVariable Long noteId, @RequestParam Long userId) {
        return Result.success(noteService.likeNote(noteId, userId));
    }

    @Operation(summary = "取消点赞")
    @DeleteMapping("/notes/{noteId}/like")
    public Result<Boolean> unlikeNote(@PathVariable Long noteId, @RequestParam Long userId) {
        return Result.success(noteService.unlikeNote(noteId, userId));
    }

    @Operation(summary = "收藏笔记")
    @PostMapping("/notes/{noteId}/favorite")
    public Result<Boolean> favoriteNote(@PathVariable Long noteId, @RequestParam Long userId) {
        return Result.success(noteService.favoriteNote(noteId, userId));
    }

    @Operation(summary = "取消收藏")
    @DeleteMapping("/notes/{noteId}/favorite")
    public Result<Boolean> unfavoriteNote(@PathVariable Long noteId, @RequestParam Long userId) {
        return Result.success(noteService.unfavoriteNote(noteId, userId));
    }

    @Operation(summary = "搜索笔记")
    @GetMapping("/notes/search")
    public Result<IPage<ExploreNote>> searchNotes(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(noteService.searchNotes(keyword, pageNum, pageSize));
    }

    // ==================== 探店路线接口 ====================

    @Operation(summary = "创建探店路线")
    @PostMapping("/routes")
    public Result<ExploreRoute> createRoute(@RequestBody ExploreRoute route) {
        return Result.success(routeService.createRoute(route));
    }

    @Operation(summary = "智能生成探店路线")
    @PostMapping("/routes/generate")
    public Result<ExploreRoute> generateSmartRoute(
            @RequestBody List<Long> poiIds,
            @RequestParam Integer transportMode,
            @RequestParam Long userId) {
        return Result.success(routeService.generateSmartRoute(poiIds, transportMode, userId));
    }

    @Operation(summary = "获取路线详情")
    @GetMapping("/routes/{routeId}")
    public Result<ExploreRoute> getRouteDetail(@PathVariable Long routeId) {
        return Result.success(routeService.getRouteDetail(routeId));
    }

    @Operation(summary = "获取推荐路线")
    @GetMapping("/routes/recommended")
    public Result<IPage<ExploreRoute>> getRecommendedRoutes(
            @RequestParam(required = false) BigDecimal longitude,
            @RequestParam(required = false) BigDecimal latitude,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(routeService.getRecommendedRoutes(longitude, latitude, pageNum, pageSize));
    }

    @Operation(summary = "获取精选路线")
    @GetMapping("/routes/featured")
    public Result<IPage<ExploreRoute>> getFeaturedRoutes(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(routeService.getFeaturedRoutes(pageNum, pageSize));
    }

    @Operation(summary = "使用路线")
    @PostMapping("/routes/{routeId}/use")
    public Result<Boolean> useRoute(@PathVariable Long routeId, @RequestParam Long userId) {
        return Result.success(routeService.useRoute(routeId, userId));
    }

    @Operation(summary = "收藏路线")
    @PostMapping("/routes/{routeId}/favorite")
    public Result<Boolean> favoriteRoute(@PathVariable Long routeId, @RequestParam Long userId) {
        return Result.success(routeService.favoriteRoute(routeId, userId));
    }

    // ==================== 打卡接口 ====================

    @Operation(summary = "用户打卡")
    @PostMapping("/checkins")
    public Result<ExploreCheckin> checkin(@RequestBody ExploreCheckin checkin) {
        return Result.success(checkinService.checkin(checkin));
    }

    @Operation(summary = "围栏自动打卡")
    @PostMapping("/checkins/auto")
    public Result<ExploreCheckin> autoCheckin(
            @RequestParam Long userId,
            @RequestParam Long poiId,
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude) {
        return Result.success(checkinService.autoCheckinByFence(userId, poiId, longitude, latitude));
    }

    @Operation(summary = "获取用户打卡记录")
    @GetMapping("/users/{userId}/checkins")
    public Result<IPage<ExploreCheckin>> getUserCheckins(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(checkinService.getUserCheckins(userId, pageNum, pageSize));
    }

    @Operation(summary = "获取用户探店统计")
    @GetMapping("/users/{userId}/checkin-stats")
    public Result<ExploreCheckinService.CheckinStatistics> getUserCheckinStats(@PathVariable Long userId) {
        return Result.success(checkinService.getUserStatistics(userId));
    }

    @Operation(summary = "获取用户足迹地图")
    @GetMapping("/users/{userId}/footprint")
    public Result<List<ExploreCheckin>> getUserFootprintMap(@PathVariable Long userId) {
        return Result.success(checkinService.getUserFootprintMap(userId));
    }

    // ==================== 达人接口 ====================

    @Operation(summary = "申请成为探店达人")
    @PostMapping("/influencers/apply")
    public Result<ExploreInfluencer> applyForInfluencer(
            @RequestParam Long userId,
            @RequestBody ExploreInfluencer influencer) {
        return Result.success(influencerService.applyForInfluencer(userId, influencer));
    }

    @Operation(summary = "获取达人详情")
    @GetMapping("/influencers/{influencerId}")
    public Result<ExploreInfluencer> getInfluencerDetail(@PathVariable Long influencerId) {
        return Result.success(influencerService.getInfluencerDetail(influencerId));
    }

    @Operation(summary = "获取达人列表")
    @GetMapping("/influencers")
    public Result<IPage<ExploreInfluencer>> getInfluencerList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) Integer level) {
        return Result.success(influencerService.getInfluencerList(pageNum, pageSize, level));
    }

    @Operation(summary = "获取热门达人榜单")
    @GetMapping("/influencers/hot")
    public Result<List<ExploreInfluencer>> getHotInfluencers(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(influencerService.getHotInfluencers(limit));
    }

    @Operation(summary = "关注达人")
    @PostMapping("/influencers/{influencerId}/follow")
    public Result<Boolean> followInfluencer(@PathVariable Long influencerId, @RequestParam Long userId) {
        return Result.success(influencerService.followInfluencer(influencerId, userId));
    }

    @Operation(summary = "取消关注达人")
    @DeleteMapping("/influencers/{influencerId}/follow")
    public Result<Boolean> unfollowInfluencer(@PathVariable Long influencerId, @RequestParam Long userId) {
        return Result.success(influencerService.unfollowInfluencer(influencerId, userId));
    }

    @Operation(summary = "获取达人统计数据")
    @GetMapping("/influencers/{influencerId}/stats")
    public Result<ExploreInfluencerService.InfluencerStats> getInfluencerStats(@PathVariable Long influencerId) {
        return Result.success(influencerService.getInfluencerStats(influencerId));
    }
}
