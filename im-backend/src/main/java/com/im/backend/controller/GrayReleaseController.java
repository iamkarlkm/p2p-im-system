package com.im.backend.controller;

import com.im.backend.config.GrayReleaseRuleManager;
import com.im.backend.config.GrayReleaseRuleManager.GrayReleaseRule;
import com.im.backend.config.GrayReleaseRuleManager.RuleType;
import com.im.backend.config.GrayReleaseRuleManager.GrayReleaseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/**
 * 灰度发布管理 Controller
 * 提供灰度发布规则管理、状态查询、动态配置等 API
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/gray-release")
@RequiredArgsConstructor
public class GrayReleaseController {

    private final GrayReleaseRuleManager ruleManager;

    /**
     * 获取灰度发布状态
     */
    @GetMapping("/status")
    public ResponseEntity<GrayReleaseStatus> getStatus() {
        return ResponseEntity.ok(ruleManager.getStatus());
    }

    /**
     * 获取所有灰度规则
     */
    @GetMapping("/rules")
    public ResponseEntity<Map<String, GrayReleaseRule>> getAllRules() {
        return ResponseEntity.ok(ruleManager.getAllRules());
    }

    /**
     * 获取单个规则
     */
    @GetMapping("/rules/{ruleName}")
    public ResponseEntity<GrayReleaseRule> getRule(@PathVariable String ruleName) {
        GrayReleaseRule rule = ruleManager.getAllRules().get(ruleName);
        if (rule == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(rule);
    }

    /**
     * 创建新规则
     */
    @PostMapping("/rules")
    public ResponseEntity<?> createRule(@RequestBody CreateRuleRequest request) {
        try {
            RuleType type = RuleType.valueOf(request.getType().toUpperCase());
            GrayReleaseRule rule = new GrayReleaseRule(
                    request.getName(),
                    request.getDescription(),
                    type,
                    request.isEnabled()
            );

            // 设置条件
            if (request.getConditions() != null) {
                request.getConditions().forEach(rule::addCondition);
            }

            rule.setPriority(request.getPriority());
            ruleManager.addRule(rule);

            log.info("Created gray release rule: {}", request.getName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to create rule", e);
            return ResponseEntity.badRequest().body("Failed to create rule: " + e.getMessage());
        }
    }

    /**
     * 更新规则
     */
    @PutMapping("/rules/{ruleName}")
    public ResponseEntity<?> updateRule(
            @PathVariable String ruleName,
            @RequestBody UpdateRuleRequest request) {
        try {
            ruleManager.toggleRule(ruleName, request.isEnabled());
            log.info("Updated rule {}: enabled={}", ruleName, request.isEnabled());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to update rule", e);
            return ResponseEntity.badRequest().body("Failed to update rule: " + e.getMessage());
        }
    }

    /**
     * 删除规则
     */
    @DeleteMapping("/rules/{ruleName}")
    public ResponseEntity<?> deleteRule(@PathVariable String ruleName) {
        try {
            ruleManager.removeRule(ruleName);
            log.info("Deleted rule: {}", ruleName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to delete rule", e);
            return ResponseEntity.badRequest().body("Failed to delete rule: " + e.getMessage());
        }
    }

    /**
     * 更新动态配置
     */
    @PutMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody Map<String, Object> config) {
        try {
            config.forEach((key, value) -> {
                ruleManager.updateConfig(key, value);
                log.debug("Updated config: {} = {}", key, value);
            });
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Failed to update config", e);
            return ResponseEntity.badRequest().body("Failed to update config: " + e.getMessage());
        }
    }

    /**
     * 获取动态配置
     */
    @GetMapping("/config/{key}")
    public ResponseEntity<?> getConfig(@PathVariable String key) {
        Object value = ruleManager.getConfig(key);
        if (value == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(value);
    }

    /**
     * 测试灰度规则匹配
     */
    @PostMapping("/test-match")
    public ResponseEntity<Map<String, Boolean>> testMatch(@RequestBody TestMatchRequest request) {
        Map<String, Boolean> result = new HashMap<>();
        
        boolean isGray = ruleManager.isGrayRelease(
                request.getUserId(),
                request.getRegion(),
                request.getClientVersion(),
                request.getIpAddress()
        );
        
        result.put("isGrayRelease", isGray);
        return ResponseEntity.ok(result);
    }

    /**
     * 启用灰度发布
     */
    @PostMapping("/enable")
    public ResponseEntity<?> enable() {
        ruleManager.updateConfig("enabled", true);
        log.info("Gray release enabled");
        return ResponseEntity.ok().build();
    }

    /**
     * 禁用灰度发布
     */
    @PostMapping("/disable")
    public ResponseEntity<?> disable() {
        ruleManager.updateConfig("enabled", false);
        log.info("Gray release disabled");
        return ResponseEntity.ok().build();
    }

    // Request DTOs

    @lombok.Data
    public static class CreateRuleRequest {
        private String name;
        private String description;
        private String type;
        private boolean enabled = true;
        private Map<String, Object> conditions;
        private int priority = 0;
    }

    @lombok.Data
    public static class UpdateRuleRequest {
        private boolean enabled;
    }

    @lombok.Data
    public static class TestMatchRequest {
        private String userId;
        private String region;
        private String clientVersion;
        private String ipAddress;
    }
}
