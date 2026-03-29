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
import java.util.Map;

/**
 * 小程序页面管理控制器
 */
@RestController
@RequestMapping("/api/v1/miniprogram/page")
@RequiredArgsConstructor
@Tag(name = "小程序页面管理", description = "小程序页面的创建、编辑、管理等接口")
public class MiniProgramPageController {

    private final MiniProgramPageService pageService;

    @PostMapping("/create")
    @Operation(summary = "创建页面")
    public Result<PageResponse> createPage(@RequestBody @Validated CreatePageRequest request) {
        PageResponse response = pageService.createPage(request);
        return Result.success(response);
    }

    @PutMapping("/{pageId}")
    @Operation(summary = "更新页面")
    public Result<PageResponse> updatePage(
            @PathVariable Long pageId,
            @RequestBody @Validated CreatePageRequest request) {
        PageResponse response = pageService.updatePage(pageId, request);
        return Result.success(response);
    }

    @GetMapping("/{pageId}")
    @Operation(summary = "获取页面详情")
    public Result<PageResponse> getPageDetail(@PathVariable Long pageId) {
        PageResponse response = pageService.getPageDetail(pageId);
        return Result.success(response);
    }

    @GetMapping("/project/{projectId}")
    @Operation(summary = "获取项目页面列表")
    public Result<List<PageResponse>> getProjectPages(@PathVariable Long projectId) {
        List<PageResponse> list = pageService.getProjectPages(projectId);
        return Result.success(list);
    }

    @PostMapping("/{pageId}/set-home")
    @Operation(summary = "设置首页")
    public Result<Boolean> setHomePage(@PathVariable Long pageId) {
        boolean success = pageService.setHomePage(pageId);
        return Result.success(success);
    }

    @PostMapping("/{pageId}/component-tree")
    @Operation(summary = "更新组件树")
    public Result<Boolean> updateComponentTree(
            @PathVariable Long pageId,
            @RequestBody Map<String, Object> componentTree) {
        boolean success = pageService.updateComponentTree(pageId, componentTree);
        return Result.success(success);
    }

    @PostMapping("/{pageId}/style")
    @Operation(summary = "更新页面样式")
    public Result<Boolean> updatePageStyle(
            @PathVariable Long pageId,
            @RequestBody Map<String, Object> pageStyle) {
        boolean success = pageService.updatePageStyle(pageId, pageStyle);
        return Result.success(success);
    }

    @PostMapping("/sort")
    @Operation(summary = "排序页面")
    public Result<Boolean> sortPages(@RequestBody List<Long> pageIds) {
        boolean success = pageService.sortPages(pageIds);
        return Result.success(success);
    }

    @DeleteMapping("/{pageId}")
    @Operation(summary = "删除页面")
    public Result<Boolean> deletePage(@PathVariable Long pageId) {
        boolean success = pageService.deletePage(pageId);
        return Result.success(success);
    }

    @PostMapping("/{pageId}/copy")
    @Operation(summary = "复制页面")
    public Result<PageResponse> copyPage(
            @PathVariable Long pageId,
            @RequestParam String newPageName) {
        PageResponse response = pageService.copyPage(pageId, newPageName);
        return Result.success(response);
    }
}
