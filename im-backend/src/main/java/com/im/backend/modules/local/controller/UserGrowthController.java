package com.im.backend.modules.local.controller;

import com.im.backend.common.api.ApiResponse;
import com.im.backend.modules.local.dto.UserGrowthRequest;
import com.im.backend.modules.local.dto.UserGrowthResponse;
import com.im.backend.modules.local.service.UserGrowthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 用户成长体系控制器
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/local/user-growth")
@RequiredArgsConstructor
@Validated
@Tag(name = "用户成长体系", description = "本地生活用户成长与会员权益相关接口")
public class UserGrowthController {

    private final UserGrowthService userGrowthService;

    /**
     * 记录用户行为并计算成长
     */
    @PostMapping("/action")
    @Operation(summary = "记录行为", description = "记录用户行为并计算成长值")
    public ApiResponse<UserGrowthResponse> recordAction(
            @Valid @RequestBody UserGrowthRequest request) {
        log.info("Recording user action: {}", request.getActionType());
        
        UserGrowthResponse response = userGrowthService.recordAction(request);
        
        return ApiResponse.success(response);
    }

    /**
     * 获取用户等级
     */
    @GetMapping("/level/{userId}")
    @Operation(summary = "获取用户等级", description = "获取用户当前等级和成长信息")
    public ApiResponse<UserGrowthResponse> getUserLevel(
            @Parameter(description = "用户ID") @PathVariable String userId) {
        log.info("Getting user level: {}", userId);
        
        UserGrowthResponse response = userGrowthService.getUserLevel(userId);
        
        return ApiResponse.success(response);
    }

    /**
     * 获取等级列表
     */
    @GetMapping("/levels")
    @Operation(summary = "获取等级列表", description = "获取所有等级配置")
    public ApiResponse<List<UserGrowthService.LevelConfig>> getLevelList() {
        log.info("Getting level list");
        
        List<UserGrowthService.LevelConfig> levels = userGrowthService.getLevelList();
        
        return ApiResponse.success(levels);
    }

    /**
     * 获取用户权益
     */
    @GetMapping("/benefits/{userId}")
    @Operation(summary = "获取用户权益", description = "获取用户当前等级权益")
    public ApiResponse<List<UserGrowthResponse.LevelBenefit>> getUserBenefits(
            @Parameter(description = "用户ID") @PathVariable String userId) {
        log.info("Getting user benefits: {}", userId);
        
        List<UserGrowthResponse.LevelBenefit> benefits = userGrowthService.getUserBenefits(userId);
        
        return ApiResponse.success(benefits);
    }

    /**
     * 领取权益
     */
    @PostMapping("/claim-benefit")
    @Operation(summary = "领取权益", description = "领取等级权益")
    public ApiResponse<Boolean> claimBenefit(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "权益类型") @RequestParam String benefitType) {
        log.info("User {} claiming benefit: {}", userId, benefitType);
        
        boolean result = userGrowthService.claimBenefit(userId, benefitType);
        
        return ApiResponse.success(result);
    }
}
