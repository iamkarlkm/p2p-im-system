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
 * 小程序项目管理控制器
 */
@RestController
@RequestMapping("/api/v1/miniprogram/project")
@RequiredArgsConstructor
@Tag(name = "小程序项目管理", description = "小程序项目的创建、编辑、发布等管理接口")
public class MiniProgramProjectController {

    private final MiniProgramProjectService projectService;

    @PostMapping("/create")
    @Operation(summary = "创建项目")
    public Result<ProjectResponse> createProject(@RequestBody @Validated CreateProjectRequest request) {
        Long developerId = getCurrentDeveloperId();
        ProjectResponse response = projectService.createProject(request, developerId);
        return Result.success(response);
    }

    @PostMapping("/create-from-template/{templateId}")
    @Operation(summary = "基于模板创建项目")
    public Result<ProjectResponse> createFromTemplate(
            @PathVariable Long templateId,
            @RequestBody @Validated CreateProjectRequest request) {
        Long developerId = getCurrentDeveloperId();
        ProjectResponse response = projectService.createProjectFromTemplate(templateId, request, developerId);
        return Result.success(response);
    }

    @PutMapping("/{projectId}")
    @Operation(summary = "更新项目")
    public Result<ProjectResponse> updateProject(
            @PathVariable Long projectId,
            @RequestBody @Validated CreateProjectRequest request) {
        ProjectResponse response = projectService.updateProject(projectId, request);
        return Result.success(response);
    }

    @GetMapping("/{projectId}")
    @Operation(summary = "获取项目详情")
    public Result<ProjectResponse> getProjectDetail(@PathVariable Long projectId) {
        ProjectResponse response = projectService.getProjectDetail(projectId);
        return Result.success(response);
    }

    @GetMapping("/key/{projectKey}")
    @Operation(summary = "根据Key获取项目")
    public Result<ProjectResponse> getProjectByKey(@PathVariable String projectKey) {
        ProjectResponse response = projectService.getProjectByKey(projectKey);
        return Result.success(response);
    }

    @GetMapping("/list")
    @Operation(summary = "获取开发者项目列表")
    public Result<List<ProjectResponse>> getDeveloperProjects(
            @RequestParam(required = false) Integer status) {
        Long developerId = getCurrentDeveloperId();
        List<ProjectResponse> list = projectService.getDeveloperProjects(developerId, status);
        return Result.success(list);
    }

    @PostMapping("/{projectId}/publish")
    @Operation(summary = "发布项目")
    public Result<Boolean> publishProject(@PathVariable Long projectId) {
        boolean success = projectService.publishProject(projectId);
        return Result.success(success);
    }

    @PostMapping("/{projectId}/offline")
    @Operation(summary = "下架项目")
    public Result<Boolean> offlineProject(@PathVariable Long projectId) {
        boolean success = projectService.offlineProject(projectId);
        return Result.success(success);
    }

    @DeleteMapping("/{projectId}")
    @Operation(summary = "删除项目")
    public Result<Boolean> deleteProject(@PathVariable Long projectId) {
        boolean success = projectService.deleteProject(projectId);
        return Result.success(success);
    }

    @PostMapping("/{projectId}/preview-qr")
    @Operation(summary = "生成预览二维码")
    public Result<String> generatePreviewQrCode(@PathVariable Long projectId) {
        String qrCode = projectService.generatePreviewQrCode(projectId);
        return Result.success(qrCode);
    }

    @GetMapping("/{projectId}/statistics")
    @Operation(summary = "获取项目统计")
    public Result<ProjectStatistics> getProjectStatistics(@PathVariable Long projectId) {
        ProjectStatistics statistics = projectService.getProjectStatistics(projectId);
        return Result.success(statistics);
    }

    private Long getCurrentDeveloperId() {
        // TODO: 从SecurityContext获取当前开发者ID
        return 1L;
    }
}
