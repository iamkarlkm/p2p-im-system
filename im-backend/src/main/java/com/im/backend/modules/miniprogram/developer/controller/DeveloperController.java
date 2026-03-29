package com.im.backend.modules.miniprogram.developer.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.miniprogram.developer.dto.*;
import com.im.backend.modules.miniprogram.developer.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序开发者平台控制器
 */
@RestController
@RequestMapping("/api/v1/developer")
@RequiredArgsConstructor
public class DeveloperController {
    
    private final IProgramProjectService projectService;
    private final IVisualDesignService visualDesignService;
    private final IComponentMarketService componentService;
    private final IDeveloperAuthService developerAuthService;
    
    // ========== 项目管理 ==========
    
    @PostMapping("/projects")
    public Result<ProjectResponse> createProject(@RequestBody @Validated CreateProjectRequest request,
                                                  @RequestAttribute("userId") Long developerId) {
        return Result.success(projectService.createProject(request, developerId));
    }
    
    @GetMapping("/projects/{projectId}")
    public Result<ProjectResponse> getProject(@PathVariable Long projectId) {
        return Result.success(projectService.getProject(projectId));
    }
    
    @GetMapping("/projects")
    public Result<List<ProjectResponse>> getDeveloperProjects(@RequestAttribute("userId") Long developerId) {
        return Result.success(projectService.getDeveloperProjects(developerId));
    }
    
    @PostMapping("/projects/{projectId}/publish")
    public Result<Boolean> publishProject(@PathVariable Long projectId) {
        return Result.success(projectService.publishProject(projectId));
    }
    
    @PostMapping("/projects/from-template/{templateId}")
    public Result<ProjectResponse> createFromTemplate(@PathVariable Long templateId,
                                                       @RequestAttribute("userId") Long developerId,
                                                       @RequestParam Long merchantId) {
        return Result.success(projectService.createProjectFromTemplate(templateId, developerId, merchantId));
    }
    
    // ========== 页面设计 ==========
    
    @PostMapping("/pages")
    public Result<PageResponse> createPage(@RequestBody @Validated CreatePageRequest request) {
        return Result.success(visualDesignService.createPage(request));
    }
    
    @GetMapping("/pages/{pageId}")
    public Result<PageResponse> getPage(@PathVariable Long pageId) {
        return Result.success(visualDesignService.getPage(pageId));
    }
    
    @GetMapping("/projects/{projectId}/pages")
    public Result<List<PageResponse>> getProjectPages(@PathVariable Long projectId) {
        return Result.success(visualDesignService.getProjectPages(projectId));
    }
    
    @PutMapping("/pages/{pageId}")
    public Result<PageResponse> updatePage(@PathVariable Long pageId,
                                            @RequestParam String layoutConfig,
                                            @RequestParam String components) {
        return Result.success(visualDesignService.updatePage(pageId, layoutConfig, components));
    }
    
    // ========== 组件市场 ==========
    
    @PostMapping("/components")
    public Result<ComponentResponse> publishComponent(@RequestBody @Validated PublishComponentRequest request,
                                                       @RequestAttribute("userId") Long developerId) {
        return Result.success(componentService.publishComponent(request, developerId));
    }
    
    @GetMapping("/components/{componentId}")
    public Result<ComponentResponse> getComponent(@PathVariable Long componentId) {
        return Result.success(componentService.getComponent(componentId));
    }
    
    @GetMapping("/components")
    public Result<List<ComponentResponse>> getComponentsByCategory(@RequestParam String category) {
        return Result.success(componentService.getComponentsByCategory(category));
    }
    
    @GetMapping("/components/hot")
    public Result<List<ComponentResponse>> getHotComponents(@RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(componentService.getHotComponents(limit));
    }
    
    @GetMapping("/components/search")
    public Result<List<ComponentResponse>> searchComponents(@RequestParam String keyword) {
        return Result.success(componentService.searchComponents(keyword));
    }
    
    @PostMapping("/components/{componentId}/download")
    public Result<Boolean> downloadComponent(@PathVariable Long componentId) {
        return Result.success(componentService.downloadComponent(componentId));
    }
    
    // ========== 开发者认证 ==========
    
    @PostMapping("/register")
    public Result<DeveloperResponse> registerDeveloper(@RequestAttribute("userId") Long userId,
                                                        @RequestParam String developerType,
                                                        @RequestParam String developerName) {
        return Result.success(developerAuthService.registerDeveloper(userId, developerType, developerName));
    }
    
    @GetMapping("/profile")
    public Result<DeveloperResponse> getDeveloperProfile(@RequestAttribute("userId") Long userId) {
        return Result.success(developerAuthService.getDeveloperByUserId(userId));
    }
    
    @PostMapping("/verify")
    public Result<Boolean> submitVerification(@RequestAttribute("userId") Long userId,
                                               @RequestBody String verifyInfo) {
        DeveloperResponse developer = developerAuthService.getDeveloperByUserId(userId);
        if (developer == null) return Result.error("开发者不存在");
        return Result.success(developerAuthService.submitVerification(developer.getId(), verifyInfo));
    }
    
    @GetMapping("/earnings")
    public Result<DeveloperEarningsResponse> getEarnings(@RequestAttribute("userId") Long userId) {
        DeveloperResponse developer = developerAuthService.getDeveloperByUserId(userId);
        if (developer == null) return Result.error("开发者不存在");
        return Result.success(developerAuthService.getEarningsStats(developer.getId()));
    }
}
