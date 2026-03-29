package com.im.backend.controller;

import com.im.backend.entity.DarkModeConfigEntity;
import com.im.backend.service.DarkModeConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 暗黑模式配置 REST API 控制器
 */
@RestController
@RequestMapping("/api/v1/dark-mode")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "暗黑模式配置", description = "应用内暗黑模式配置管理 API")
public class DarkModeConfigController {

    private final DarkModeConfigService configService;
    
    /**
     * 获取当前用户的活跃配置
     */
    @GetMapping("/active")
    @Operation(summary = "获取活跃配置", description = "获取当前用户的活跃暗黑模式配置")
    public ResponseEntity<?> getActiveConfig(
            @Parameter(description = "用户ID") @RequestParam String userId) {
        try {
            var config = configService.getActiveConfig(userId);
            if (config.isPresent()) {
                return ResponseEntity.ok(buildSuccessResponse("获取配置成功", config.get()));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("用户没有配置", "USER_CONFIG_NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("获取活跃配置失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取配置失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 获取用户的所有配置
     */
    @GetMapping("/list")
    @Operation(summary = "获取配置列表", description = "获取当前用户的所有暗黑模式配置")
    public ResponseEntity<?> getUserConfigs(
            @Parameter(description = "用户ID") @RequestParam String userId) {
        try {
            List<DarkModeConfigEntity> configs = configService.getUserConfigs(userId);
            return ResponseEntity.ok(buildSuccessResponse("获取配置列表成功", configs));
        } catch (Exception e) {
            log.error("获取配置列表失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取配置列表失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 创建默认配置
     */
    @PostMapping("/create-default")
    @Operation(summary = "创建默认配置", description = "为用户创建默认的暗黑模式配置")
    public ResponseEntity<?> createDefaultConfig(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "平台类型") @RequestParam(defaultValue = "DESKTOP") DarkModeConfigEntity.Platform platform,
            @Parameter(description = "设备ID") @RequestParam(required = false) String deviceId) {
        try {
            DarkModeConfigEntity config = configService.createDefaultConfig(userId, platform, deviceId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(buildSuccessResponse("创建默认配置成功", config));
        } catch (Exception e) {
            log.error("创建默认配置失败: userId={}, platform={}", userId, platform, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("创建配置失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 创建自定义配置
     */
    @PostMapping("/create-custom")
    @Operation(summary = "创建自定义配置", description = "创建自定义的暗黑模式配置")
    public ResponseEntity<?> createCustomConfig(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "主题模式") @RequestParam DarkModeConfigEntity.ThemeMode themeMode,
            @Parameter(description = "平台类型") @RequestParam DarkModeConfigEntity.Platform platform,
            @Parameter(description = "设备ID") @RequestParam(required = false) String deviceId,
            @RequestBody(required = false) Map<String, Object> customColors) {
        try {
            DarkModeConfigEntity config = configService.createCustomConfig(userId, themeMode, platform, deviceId, customColors);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(buildSuccessResponse("创建自定义配置成功", config));
        } catch (Exception e) {
            log.error("创建自定义配置失败: userId={}, themeMode={}", userId, themeMode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("创建自定义配置失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 切换主题模式
     */
    @PutMapping("/switch-theme")
    @Operation(summary = "切换主题模式", description = "切换用户的主题模式")
    public ResponseEntity<?> switchThemeMode(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "新主题模式") @RequestParam DarkModeConfigEntity.ThemeMode newThemeMode) {
        try {
            DarkModeConfigEntity config = configService.switchThemeMode(userId, newThemeMode);
            return ResponseEntity.ok(buildSuccessResponse("切换主题模式成功", config));
        } catch (Exception e) {
            log.error("切换主题模式失败: userId={}, themeMode={}", userId, newThemeMode, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("切换主题模式失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 激活指定配置
     */
    @PutMapping("/activate")
    @Operation(summary = "激活配置", description = "激活用户的指定配置")
    public ResponseEntity<?> activateConfig(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "配置ID") @RequestParam Long configId) {
        try {
            boolean success = configService.activateConfig(userId, configId);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse("激活配置成功", Map.of("configId", configId)));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("配置不存在或用户不匹配", "CONFIG_NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("激活配置失败: userId={}, configId={}", userId, configId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("激活配置失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 更新自定义颜色
     */
    @PutMapping("/update-colors")
    @Operation(summary = "更新颜色", description = "更新自定义主题的颜色配置")
    public ResponseEntity<?> updateCustomColors(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @RequestBody Map<String, String> colorUpdates) {
        try {
            DarkModeConfigEntity config = configService.updateCustomColors(userId, colorUpdates);
            return ResponseEntity.ok(buildSuccessResponse("更新颜色成功", config));
        } catch (IllegalArgumentException e) {
            log.warn("更新颜色失败: userId={}, error={}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse(e.getMessage(), "INVALID_REQUEST"));
        } catch (Exception e) {
            log.error("更新颜色失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("更新颜色失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 切换高对比度模式
     */
    @PutMapping("/toggle-high-contrast")
    @Operation(summary = "切换高对比度", description = "启用或禁用高对比度模式")
    public ResponseEntity<?> toggleHighContrast(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "是否启用") @RequestParam boolean enabled) {
        try {
            boolean success = configService.toggleHighContrast(userId, enabled);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse(enabled ? "启用高对比度成功" : "禁用高对比度成功",
                        Map.of("enabled", enabled)));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("用户配置不存在", "CONFIG_NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("切换高对比度失败: userId={}, enabled={}", userId, enabled, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("切换高对比度失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 切换减少动画效果
     */
    @PutMapping("/toggle-reduce-motion")
    @Operation(summary = "切换减少动画", description = "启用或禁用减少动画效果")
    public ResponseEntity<?> toggleReduceMotion(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "是否启用") @RequestParam boolean enabled) {
        try {
            boolean success = configService.toggleReduceMotion(userId, enabled);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse(enabled ? "启用减少动画成功" : "禁用减少动画成功",
                        Map.of("enabled", enabled)));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("用户配置不存在", "CONFIG_NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("切换减少动画失败: userId={}, enabled={}", userId, enabled, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("切换减少动画失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 切换夜间保护模式
     */
    @PutMapping("/toggle-night-protection")
    @Operation(summary = "切换夜间保护", description = "启用或禁用夜间保护模式")
    public ResponseEntity<?> toggleNightProtection(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "是否启用") @RequestParam boolean enabled) {
        try {
            boolean success = configService.toggleNightProtection(userId, enabled);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse(enabled ? "启用夜间保护成功" : "禁用夜间保护成功",
                        Map.of("enabled", enabled)));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("用户配置不存在", "CONFIG_NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("切换夜间保护失败: userId={}, enabled={}", userId, enabled, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("切换夜间保护失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 配置自动切换
     */
    @PutMapping("/configure-auto-switch")
    @Operation(summary = "配置自动切换", description = "配置暗黑模式自动切换时间")
    public ResponseEntity<?> configureAutoSwitch(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "是否启用") @RequestParam boolean enabled,
            @Parameter(description = "开始时间 (HH:mm)") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间 (HH:mm)") @RequestParam(required = false) String endTime) {
        try {
            boolean success = configService.configureAutoSwitch(userId, enabled, startTime, endTime);
            if (success) {
                Map<String, Object> result = new HashMap<>();
                result.put("enabled", enabled);
                result.put("startTime", startTime);
                result.put("endTime", endTime);
                return ResponseEntity.ok(buildSuccessResponse("配置自动切换成功", result));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("用户配置不存在", "CONFIG_NOT_FOUND"));
            }
        } catch (IllegalArgumentException e) {
            log.warn("配置自动切换失败: userId={}, error={}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse(e.getMessage(), "INVALID_TIME_FORMAT"));
        } catch (Exception e) {
            log.error("配置自动切换失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("配置自动切换失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 检查是否应该自动切换到暗黑模式
     */
    @GetMapping("/check-auto-switch")
    @Operation(summary = "检查自动切换", description = "检查当前是否应该自动切换到暗黑模式")
    public ResponseEntity<?> checkAutoSwitch(
            @Parameter(description = "用户ID") @RequestParam String userId) {
        try {
            boolean shouldSwitch = configService.shouldAutoSwitchToDark(userId);
            return ResponseEntity.ok(buildSuccessResponse("检查自动切换成功",
                    Map.of("shouldSwitchToDark", shouldSwitch)));
        } catch (Exception e) {
            log.error("检查自动切换失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("检查自动切换失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 更新透明度
     */
    @PutMapping("/update-opacity")
    @Operation(summary = "更新透明度", description = "更新主题的透明度级别")
    public ResponseEntity<?> updateOpacity(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "透明度级别 (0.0-1.0)") @RequestParam Double opacityLevel) {
        try {
            boolean success = configService.updateOpacity(userId, opacityLevel);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse("更新透明度成功",
                        Map.of("opacityLevel", opacityLevel)));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("用户配置不存在", "CONFIG_NOT_FOUND"));
            }
        } catch (IllegalArgumentException e) {
            log.warn("更新透明度失败: userId={}, error={}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse(e.getMessage(), "INVALID_OPACITY"));
        } catch (Exception e) {
            log.error("更新透明度失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("更新透明度失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 更新字体缩放
     */
    @PutMapping("/update-font-scale")
    @Operation(summary = "更新字体缩放", description = "更新字体缩放因子")
    public ResponseEntity<?> updateFontScale(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "字体缩放因子 (0.8-1.5)") @RequestParam Double fontScaleFactor) {
        try {
            boolean success = configService.updateFontScale(userId, fontScaleFactor);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse("更新字体缩放成功",
                        Map.of("fontScaleFactor", fontScaleFactor)));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("用户配置不存在", "CONFIG_NOT_FOUND"));
            }
        } catch (IllegalArgumentException e) {
            log.warn("更新字体缩放失败: userId={}, error={}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(buildErrorResponse(e.getMessage(), "INVALID_FONT_SCALE"));
        } catch (Exception e) {
            log.error("更新字体缩放失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("更新字体缩放失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 同步配置到设备
     */
    @PostMapping("/sync-to-device")
    @Operation(summary = "同步到设备", description = "将配置同步到指定设备")
    public ResponseEntity<?> syncConfigToDevice(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "配置ID") @RequestParam Long configId,
            @Parameter(description = "设备ID") @RequestParam String deviceId) {
        try {
            boolean success = configService.syncConfigToDevice(userId, configId, deviceId);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse("同步配置到设备成功",
                        Map.of("configId", configId, "deviceId", deviceId)));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("配置不存在或用户不匹配", "CONFIG_NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("同步配置到设备失败: userId={}, configId={}, deviceId={}", userId, configId, deviceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("同步配置到设备失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 获取当前主题的颜色配置
     */
    @GetMapping("/current-colors")
    @Operation(summary = "获取当前颜色", description = "获取当前主题的颜色配置")
    public ResponseEntity<?> getCurrentThemeColors(
            @Parameter(description = "用户ID") @RequestParam String userId) {
        try {
            Map<String, String> colors = configService.getCurrentThemeColors(userId);
            return ResponseEntity.ok(buildSuccessResponse("获取颜色配置成功", colors));
        } catch (Exception e) {
            log.error("获取颜色配置失败: userId={}", userId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取颜色配置失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 导出配置
     */
    @GetMapping("/export")
    @Operation(summary = "导出配置", description = "导出配置为JSON格式")
    public ResponseEntity<?> exportConfig(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "配置ID") @RequestParam Long configId) {
        try {
            String json = configService.exportConfig(userId, configId);
            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .header("Content-Disposition", "attachment; filename=\"dark-mode-config-" + configId + ".json\"")
                    .body(json);
        } catch (IllegalArgumentException e) {
            log.warn("导出配置失败: userId={}, configId={}, error={}", userId, configId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(buildErrorResponse(e.getMessage(), "CONFIG_NOT_FOUND"));
        } catch (Exception e) {
            log.error("导出配置失败: userId={}, configId={}", userId, configId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("导出配置失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 删除配置
     */
    @DeleteMapping("/delete")
    @Operation(summary = "删除配置", description = "删除用户的指定配置")
    public ResponseEntity<?> deleteConfig(
            @Parameter(description = "用户ID") @RequestParam String userId,
            @Parameter(description = "配置ID") @RequestParam Long configId) {
        try {
            boolean success = configService.deleteConfig(userId, configId);
            if (success) {
                return ResponseEntity.ok(buildSuccessResponse("删除配置成功",
                        Map.of("deleted", true, "configId", configId)));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(buildErrorResponse("配置不存在或用户不匹配", "CONFIG_NOT_FOUND"));
            }
        } catch (Exception e) {
            log.error("删除配置失败: userId={}, configId={}", userId, configId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("删除配置失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    @Operation(summary = "获取统计信息", description = "获取暗黑模式配置的统计信息")
    public ResponseEntity<?> getStatistics() {
        try {
            Map<String, Object> stats = configService.getStatistics();
            return ResponseEntity.ok(buildSuccessResponse("获取统计信息成功", stats));
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("获取统计信息失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 清理过时配置
     */
    @PostMapping("/cleanup")
    @Operation(summary = "清理配置", description = "清理过时的临时配置")
    public ResponseEntity<?> cleanupExpiredConfigs() {
        try {
            int cleaned = configService.cleanupExpiredConfigs();
            return ResponseEntity.ok(buildSuccessResponse("清理配置成功",
                    Map.of("cleanedCount", cleaned, "timestamp", LocalDateTime.now())));
        } catch (Exception e) {
            log.error("清理配置失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(buildErrorResponse("清理配置失败: " + e.getMessage(), "INTERNAL_ERROR"));
        }
    }
    
    /**
     * 健康检查端点
     */
    @GetMapping("/health")
    @Operation(summary = "健康检查", description = "检查暗黑模式服务是否正常")
    public ResponseEntity<?> healthCheck() {
        try {
            Map<String, Object> health = new HashMap<>();
            health.put("status", "UP");
            health.put("service", "dark-mode-config");
            health.put("timestamp", LocalDateTime.now());
            health.put("activeConfigs", configService.getStatistics().get("totalConfigs"));
            return ResponseEntity.ok(health);
        } catch (Exception e) {
            log.error("健康检查失败", e);
            Map<String, Object> health = new HashMap<>();
            health.put("status", "DOWN");
            health.put("service", "dark-mode-config");
            health.put("timestamp", LocalDateTime.now());
            health.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(health);
        }
    }
    
    // 响应构建辅助方法
    private Map<String, Object> buildSuccessResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("data", data);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
    
    private Map<String, Object> buildErrorResponse(String message, String errorCode) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        response.put("errorCode", errorCode);
        response.put("timestamp", LocalDateTime.now());
        return response;
    }
}