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
 * 开发者中心控制器
 */
@RestController
@RequestMapping("/api/v1/miniprogram/developer")
@RequiredArgsConstructor
@Tag(name = "开发者中心", description = "开发者注册、认证、收益等接口")
public class MiniProgramDeveloperController {

    private final MiniProgramDeveloperService developerService;

    @PostMapping("/register")
    @Operation(summary = "注册开发者")
    public Result<DeveloperResponse> registerDeveloper(@RequestBody @Validated RegisterDeveloperRequest request) {
        Long userId = getCurrentUserId();
        DeveloperResponse response = developerService.registerDeveloper(userId, request);
        return Result.success(response);
    }

    @GetMapping("/info/{developerId}")
    @Operation(summary = "获取开发者信息")
    public Result<DeveloperResponse> getDeveloperInfo(@PathVariable Long developerId) {
        DeveloperResponse response = developerService.getDeveloperInfo(developerId);
        return Result.success(response);
    }

    @PutMapping("/info")
    @Operation(summary = "更新开发者信息")
    public Result<DeveloperResponse> updateDeveloperInfo(@RequestBody @Validated UpdateDeveloperRequest request) {
        Long developerId = getCurrentDeveloperId();
        DeveloperResponse response = developerService.updateDeveloperInfo(developerId, request);
        return Result.success(response);
    }

    @PostMapping("/authenticate")
    @Operation(summary = "提交认证")
    public Result<Boolean> submitAuthentication(@RequestBody @Validated AuthRequest request) {
        Long developerId = getCurrentDeveloperId();
        boolean success = developerService.submitAuthentication(developerId, request);
        return Result.success(success);
    }

    @GetMapping("/statistics")
    @Operation(summary = "获取开发者统计")
    public Result<DeveloperStatistics> getDeveloperStatistics() {
        Long developerId = getCurrentDeveloperId();
        DeveloperStatistics statistics = developerService.getDeveloperStatistics(developerId);
        return Result.success(statistics);
    }

    @GetMapping("/income-records")
    @Operation(summary = "获取收益明细")
    public Result<List<IncomeRecord>> getIncomeRecords(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long developerId = getCurrentDeveloperId();
        List<IncomeRecord> list = developerService.getIncomeRecords(developerId, page, size);
        return Result.success(list);
    }

    @PostMapping("/withdraw")
    @Operation(summary = "提现申请")
    public Result<Boolean> withdrawRequest(@RequestBody @Validated WithdrawRequest request) {
        Long developerId = getCurrentDeveloperId();
        boolean success = developerService.withdrawRequest(developerId, request);
        return Result.success(success);
    }

    @GetMapping("/levels")
    @Operation(summary = "获取开发者等级列表")
    public Result<List<DeveloperLevel>> getDeveloperLevels() {
        List<DeveloperLevel> list = developerService.getDeveloperLevels();
        return Result.success(list);
    }

    @GetMapping("/search")
    @Operation(summary = "搜索开发者")
    public Result<List<DeveloperResponse>> searchDevelopers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<DeveloperResponse> list = developerService.searchDevelopers(keyword, page, size);
        return Result.success(list);
    }

    private Long getCurrentUserId() {
        // TODO: 从SecurityContext获取当前用户ID
        return 1L;
    }

    private Long getCurrentDeveloperId() {
        // TODO: 从SecurityContext获取当前开发者ID
        return 1L;
    }
}
