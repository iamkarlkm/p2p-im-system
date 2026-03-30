package com.im.backend.modules.merchant.miniprogram.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.im.backend.common.Result;
import com.im.backend.modules.merchant.miniprogram.dto.MiniProgramCreateRequest;
import com.im.backend.modules.merchant.miniprogram.entity.MiniProgramApp;
import com.im.backend.modules.merchant.miniprogram.service.IMiniProgramService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 小程序应用控制器 - 功能#313: 小程序开发者生态
 */
@Tag(name = "小程序开发", description = "小程序开发者生态相关接口")
@RestController
@RequestMapping("/api/miniprogram")
@RequiredArgsConstructor
public class MiniProgramController {

    private final IMiniProgramService miniProgramService;

    @Operation(summary = "创建小程序")
    @PostMapping("/create")
    public Result<MiniProgramApp> createApp(
            @RequestAttribute("merchantId") Long merchantId,
            @RequestBody @Validated MiniProgramCreateRequest request) {
        return miniProgramService.createApp(merchantId, request);
    }

    @Operation(summary = "获取商户小程序列表")
    @GetMapping("/list/{merchantId}")
    public Result<IPage<MiniProgramApp>> getMerchantApps(
            @PathVariable Long merchantId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(miniProgramService.getMerchantApps(merchantId, new Page<>(page, size)));
    }

    @Operation(summary = "获取小程序详情")
    @GetMapping("/detail/{appId}")
    public Result<MiniProgramApp> getAppDetail(@PathVariable String appId) {
        return Result.success(miniProgramService.getAppDetail(appId));
    }

    @Operation(summary = "发布小程序")
    @PostMapping("/publish/{appId}")
    public Result<Void> publishApp(
            @RequestAttribute("merchantId") Long merchantId,
            @PathVariable Long appId) {
        return miniProgramService.publishApp(merchantId, appId);
    }

    @Operation(summary = "获取热门小程序")
    @GetMapping("/hot")
    public Result<List<MiniProgramApp>> getHotApps(
            @RequestParam(defaultValue = "10") Integer limit) {
        return Result.success(miniProgramService.getHotApps(limit));
    }
}
