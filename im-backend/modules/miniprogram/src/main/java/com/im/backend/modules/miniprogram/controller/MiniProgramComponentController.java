package com.im.backend.modules.miniprogram.controller;

import com.im.backend.common.api.Result;
import com.im.backend.modules.miniprogram.dto.*;
import com.im.backend.modules.miniprogram.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 组件市场控制器
 */
@RestController
@RequestMapping("/api/v1/miniprogram/component")
@RequiredArgsConstructor
@Tag(name = "组件市场", description = "组件市场的浏览、下载、发布等接口")
public class MiniProgramComponentController {

    private final MiniProgramComponentService componentService;

    @PostMapping("/publish")
    @Operation(summary = "发布组件")
    public Result<ComponentResponse> publishComponent(@RequestBody @Validated CreateComponentRequest request) {
        Long authorId = getCurrentUserId();
        ComponentResponse response = componentService.publishComponent(request, authorId);
        return Result.success(response);
    }

    @PutMapping("/{componentId}")
    @Operation(summary = "更新组件")
    public Result<ComponentResponse> updateComponent(
            @PathVariable Long componentId,
            @RequestBody @Validated CreateComponentRequest request) {
        ComponentResponse response = componentService.updateComponent(componentId, request);
        return Result.success(response);
    }

    @GetMapping("/{componentId}")
    @Operation(summary = "获取组件详情")
    public Result<ComponentResponse> getComponentDetail(@PathVariable Long componentId) {
        ComponentResponse response = componentService.getComponentDetail(componentId);
        return Result.success(response);
    }

    @GetMapping("/list")
    @Operation(summary = "获取组件列表")
    public Result<List<ComponentResponse>> getComponentList(
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ComponentResponse> list = componentService.getComponentList(category, keyword, page, size);
        return Result.success(list);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门组件")
    public Result<List<ComponentResponse>> getHotComponents(
            @RequestParam(defaultValue = "10") int limit) {
        List<ComponentResponse> list = componentService.getHotComponents(limit);
        return Result.success(list);
    }

    @GetMapping("/developer/{developerId}")
    @Operation(summary = "获取开发者组件")
    public Result<List<ComponentResponse>> getDeveloperComponents(@PathVariable Long developerId) {
        List<ComponentResponse> list = componentService.getDeveloperComponents(developerId);
        return Result.success(list);
    }

    @PostMapping("/{componentId}/download")
    @Operation(summary = "下载组件")
    public Result<Boolean> downloadComponent(@PathVariable Long componentId) {
        Long userId = getCurrentUserId();
        boolean success = componentService.downloadComponent(componentId, userId);
        return Result.success(success);
    }

    @PostMapping("/{componentId}/rate")
    @Operation(summary = "评分组件")
    public Result<Boolean> rateComponent(
            @PathVariable Long componentId,
            @RequestParam Integer rating) {
        Long userId = getCurrentUserId();
        boolean success = componentService.rateComponent(componentId, userId, rating);
        return Result.success(success);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索组件")
    public Result<List<ComponentResponse>> searchComponents(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<ComponentResponse> list = componentService.searchComponents(keyword, category, page, size);
        return Result.success(list);
    }

    @PostMapping("/{componentId}/offline")
    @Operation(summary = "下架组件")
    public Result<Boolean> offlineComponent(@PathVariable Long componentId) {
        boolean success = componentService.offlineComponent(componentId);
        return Result.success(success);
    }

    private Long getCurrentUserId() {
        // TODO: 从SecurityContext获取当前用户ID
        return 1L;
    }
}
