package com.im.backend.security.differentialprivacy.controller;

import com.im.backend.security.differentialprivacy.entity.DifferentialPrivacyConfigEntity;
import com.im.backend.security.differentialprivacy.service.DifferentialPrivacyConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 差分隐私配置控制器
 * 提供配置管理的 REST API
 */
@RestController
@RequestMapping("/api/v1/differential-privacy/config")
@RequiredArgsConstructor
@Slf4j
public class DifferentialPrivacyConfigController {
    
    private final DifferentialPrivacyConfigService configService;
    
    @GetMapping("/{configKey}")
    public ResponseEntity<?> getConfig(@PathVariable String configKey) {
        log.info("Getting config: {}", configKey);
        return configService.getConfigByKey(configKey)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping
    public ResponseEntity<List<DifferentialPrivacyConfigEntity>> getAllActiveConfigs() {
        log.info("Getting all active configs");
        return ResponseEntity.ok(configService.getAllActiveConfigs());
    }
    
    @GetMapping("/page")
    public ResponseEntity<Page<DifferentialPrivacyConfigEntity>> getConfigsPage(Pageable pageable) {
        log.info("Getting configs page");
        return ResponseEntity.ok(configService.getConfigsPage(pageable));
    }
    
    @PostMapping
    public ResponseEntity<DifferentialPrivacyConfigEntity> createConfig(@RequestBody DifferentialPrivacyConfigEntity config) {
        log.info("Creating config: {}", config.getConfigKey());
        try {
            DifferentialPrivacyConfigEntity created = configService.createConfig(config);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Failed to create config", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/{configKey}")
    public ResponseEntity<?> updateConfig(
            @PathVariable String configKey,
            @RequestBody DifferentialPrivacyConfigEntity updates) {
        log.info("Updating config: {}", configKey);
        try {
            DifferentialPrivacyConfigEntity updated = configService.updateConfig(configKey, updates);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{configKey}")
    public ResponseEntity<Void> deleteConfig(@PathVariable String configKey) {
        log.info("Deleting config: {}", configKey);
        configService.deleteConfig(configKey);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{configKey}/approval")
    public ResponseEntity<?> updateApprovalStatus(
            @PathVariable String configKey,
            @RequestParam String status) {
        log.info("Updating approval status for config: {} to {}", configKey, status);
        try {
            DifferentialPrivacyConfigEntity updated = configService.updateApprovalStatus(configKey, status);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/approval/pending")
    public ResponseEntity<List<DifferentialPrivacyConfigEntity>> getPendingApprovals() {
        log.info("Getting pending approvals");
        return ResponseEntity.ok(configService.getPendingApprovals());
    }
    
    @GetMapping("/sensitive")
    public ResponseEntity<List<DifferentialPrivacyConfigEntity>> getSensitiveConfigs() {
        log.info("Getting sensitive configs");
        return ResponseEntity.ok(configService.getSensitiveConfigs());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<DifferentialPrivacyConfigEntity>> searchConfigs(@RequestParam String keyword) {
        log.info("Searching configs with keyword: {}", keyword);
        return ResponseEntity.ok(configService.searchConfigs(keyword));
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        log.info("Getting config stats");
        Map<String, Object> stats = new HashMap<>();
        stats.put("activeCount", configService.getActiveConfigCount());
        stats.put("sensitiveCount", configService.getSensitiveConfigCount());
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/validate/epsilon")
    public ResponseEntity<Map<String, Boolean>> validateEpsilon(
            @RequestParam Double epsilon,
            @RequestParam String configKey) {
        log.info("Validating epsilon: {} for config: {}", epsilon, configKey);
        Map<String, Boolean> result = new HashMap<>();
        result.put("valid", configService.validateEpsilon(epsilon, configKey));
        return ResponseEntity.ok(result);
    }
}