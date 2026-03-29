package com.im.backend.modules.miniprogram.controller;

import com.im.backend.common.api.Result;
import com.im.backend.modules.miniprogram.dto.*;
import com.im.backend.modules.miniprogram.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 模板市场控制器
 */
@RestController
@RequestMapping("/api/v1/miniprogram/template")
@RequiredArgsConstructor
@Tag(name = "模板市场", description = "模板市场的浏览、使用等接口")
public class MiniProgramTemplateController {

    private final MiniProgramTemplateService templateService;

    @GetMapping("/list")
    @Operation(summary = "获取模板列表")
    public Result<List<TemplateResponse>> getTemplateList(
            @RequestParam(required = false) Integer category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<TemplateResponse> list = templateService.getTemplateList(category, page, size);
        return Result.success(list);
    }

    @GetMapping("/{templateId}")
    @Operation(summary = "获取模板详情")
    public Result<TemplateResponse> getTemplateDetail(@PathVariable Long templateId) {
        TemplateResponse response = templateService.getTemplateDetail(templateId);
        return Result.success(response);
    }

    @GetMapping("/hot")
    @Operation(summary = "获取热门模板")
    public Result<List<TemplateResponse>> getHotTemplates(
            @RequestParam(defaultValue = "10") int limit) {
        List<TemplateResponse> list = templateService.getHotTemplates(limit);
        return Result.success(list);
    }

    @GetMapping("/official")
    @Operation(summary = "获取官方模板")
    public Result<List<TemplateResponse>> getOfficialTemplates() {
        List<TemplateResponse> list = templateService.getOfficialTemplates();
        return Result.success(list);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索模板")
    public Result<List<TemplateResponse>> searchTemplates(
            @RequestParam String keyword,
            @RequestParam(required = false) Integer category,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<TemplateResponse> list = templateService.searchTemplates(keyword, category, page, size);
        return Result.success(list);
    }

    @PostMapping("/{templateId}/use")
    @Operation(summary = "使用模板")
    public Result<Boolean> useTemplate(@PathVariable Long templateId) {
        Long userId = getCurrentUserId();
        boolean success = templateService.useTemplate(templateId, userId);
        return Result.success(success);
    }

    @GetMapping("/recommend")
    @Operation(summary = "获取推荐模板")
    public Result<List<TemplateResponse>> getRecommendTemplates(
            @RequestParam(defaultValue = "10") int limit) {
        Long userId = getCurrentUserId();
        List<TemplateResponse> list = templateService.getRecommendTemplates(userId, limit);
        return Result.success(list);
    }

    private Long getCurrentUserId() {
        // TODO: 从SecurityContext获取当前用户ID
        return 1L;
    }
}
