package com.im.backend.modules.miniprogram.controller;

import com.im.backend.common.Result;
import com.im.backend.modules.miniprogram.dto.*;
import com.im.backend.modules.miniprogram.service.IMiniProgramAppService;
import com.im.backend.modules.miniprogram.service.IMiniProgramDeveloperService;
import com.im.backend.modules.miniprogram.service.IMiniProgramSandboxService;
import com.im.backend.modules.miniprogram.service.IMiniProgramVersionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 小程序开放平台控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/miniprogram")
public class MiniProgramController {

    @Autowired
    private IMiniProgramDeveloperService developerService;

    @Autowired
    private IMiniProgramAppService appService;

    @Autowired
    private IMiniProgramVersionService versionService;

    @Autowired
    private IMiniProgramSandboxService sandboxService;

    // ========== 开发者管理 ==========

    @PostMapping("/developer/register")
    public Result<DeveloperResponse> registerDeveloper(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody RegisterDeveloperRequest request) {
        return Result.success(developerService.registerDeveloper(userId, request));
    }

    @GetMapping("/developer/info")
    public Result<DeveloperResponse> getDeveloperInfo(@RequestAttribute("userId") Long userId) {
        return Result.success(developerService.getDeveloperInfo(userId));
    }

    @PutMapping("/developer/info")
    public Result<DeveloperResponse> updateDeveloper(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody RegisterDeveloperRequest request) {
        return Result.success(developerService.updateDeveloper(userId, request));
    }

    // ========== 应用管理 ==========

    @PostMapping("/app/create")
    public Result<AppResponse> createApp(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody CreateAppRequest request) {
        DeveloperResponse dev = developerService.getDeveloperInfo(userId);
        return Result.success(appService.createApp(dev.getId(), request));
    }

    @GetMapping("/app/{appId}")
    public Result<AppResponse> getApp(@PathVariable String appId) {
        return Result.success(appService.getApp(appId));
    }

    @GetMapping("/developer/apps")
    public Result<List<AppResponse>> getDeveloperApps(@RequestAttribute("userId") Long userId) {
        DeveloperResponse dev = developerService.getDeveloperInfo(userId);
        return Result.success(appService.getDeveloperApps(dev.getId()));
    }

    @PutMapping("/app/{appId}")
    public Result<AppResponse> updateApp(
            @RequestAttribute("userId") Long userId,
            @PathVariable String appId,
            @Valid @RequestBody CreateAppRequest request) {
        DeveloperResponse dev = developerService.getDeveloperInfo(userId);
        return Result.success(appService.updateApp(dev.getId(), appId, request));
    }

    @DeleteMapping("/app/{appId}")
    public Result<Void> deleteApp(
            @RequestAttribute("userId") Long userId,
            @PathVariable String appId) {
        DeveloperResponse dev = developerService.getDeveloperInfo(userId);
        appService.deleteApp(dev.getId(), appId);
        return Result.success();
    }

    @PostMapping("/app/{appId}/secret/reset")
    public Result<String> resetAppSecret(
            @RequestAttribute("userId") Long userId,
            @PathVariable String appId) {
        DeveloperResponse dev = developerService.getDeveloperInfo(userId);
        return Result.success(appService.resetAppSecret(dev.getId(), appId));
    }

    // ========== 版本管理 ==========

    @PostMapping("/version/submit")
    public Result<VersionResponse> submitVersion(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody SubmitVersionRequest request) {
        DeveloperResponse dev = developerService.getDeveloperInfo(userId);
        return Result.success(versionService.submitVersion(dev.getId(), request));
    }

    @GetMapping("/app/{appId}/versions")
    public Result<List<VersionResponse>> getVersions(@PathVariable Long appId) {
        return Result.success(versionService.getVersions(appId));
    }

    @PostMapping("/version/{versionId}/audit/submit")
    public Result<Void> submitAudit(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long versionId) {
        DeveloperResponse dev = developerService.getDeveloperInfo(userId);
        versionService.submitAudit(dev.getId(), versionId);
        return Result.success();
    }

    @PostMapping("/version/{versionId}/release")
    public Result<Void> releaseVersion(
            @RequestAttribute("userId") Long userId,
            @PathVariable Long versionId) {
        DeveloperResponse dev = developerService.getDeveloperInfo(userId);
        versionService.releaseVersion(dev.getId(), versionId);
        return Result.success();
    }

    // ========== 沙箱环境 ==========

    @PostMapping("/sandbox/create")
    public Result<SandboxResponse> createSandbox(
            @RequestAttribute("userId") Long userId,
            @Valid @RequestBody CreateSandboxRequest request) {
        DeveloperResponse dev = developerService.getDeveloperInfo(userId);
        return Result.success(sandboxService.createSandbox(dev.getId(), request));
    }

    @GetMapping("/app/{appId}/sandboxes")
    public Result<List<SandboxResponse>> getSandboxes(@PathVariable Long appId) {
        return Result.success(sandboxService.getSandboxes(appId));
    }

    @PostMapping("/sandbox/{sandboxId}/stop")
    public Result<Void> stopSandbox(
            @RequestAttribute("userId") Long userId,
            @PathVariable String sandboxId) {
        DeveloperResponse dev = developerService.getDeveloperInfo(userId);
        sandboxService.stopSandbox(dev.getId(), sandboxId);
        return Result.success();
    }
}
