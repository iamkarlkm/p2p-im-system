package com.im.backend.security.differentialprivacy.controller;

import com.im.backend.security.differentialprivacy.entity.PrivacyBudgetEntity;
import com.im.backend.security.differentialprivacy.entity.PrivacyBudgetEntity.BudgetType;
import com.im.backend.security.differentialprivacy.service.PrivacyBudgetService;
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
 * 隐私预算控制器
 * 提供预算管理的 REST API
 */
@RestController
@RequestMapping("/api/v1/differential-privacy/budget")
@RequiredArgsConstructor
@Slf4j
public class PrivacyBudgetController {
    
    private final PrivacyBudgetService budgetService;
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PrivacyBudgetEntity>> getUserBudgets(@PathVariable String userId) {
        log.info("Getting budgets for user: {}", userId);
        return ResponseEntity.ok(budgetService.getAllUserBudgets(userId));
    }
    
    @GetMapping("/user/{userId}/type/{budgetType}")
    public ResponseEntity<?> getUserBudget(
            @PathVariable String userId,
            @PathVariable String budgetType) {
        log.info("Getting budget for user: {}, type: {}", userId, budgetType);
        try {
            BudgetType type = BudgetType.valueOf(budgetType);
            return budgetService.getUserBudget(userId, type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping
    public ResponseEntity<PrivacyBudgetEntity> createBudget(@RequestBody PrivacyBudgetEntity budget) {
        log.info("Creating budget for user: {}", budget.getUserId());
        try {
            PrivacyBudgetEntity created = budgetService.createBudget(budget);
            return ResponseEntity.ok(created);
        } catch (Exception e) {
            log.error("Failed to create budget", e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/consume")
    public ResponseEntity<?> consumeBudget(
            @RequestParam String userId,
            @RequestParam String budgetType,
            @RequestParam Double epsilon) {
        log.info("Consuming budget for user: {}, epsilon: {}", userId, epsilon);
        try {
            BudgetType type = BudgetType.valueOf(budgetType);
            PrivacyBudgetEntity updated = budgetService.consumeBudget(userId, type, epsilon);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/reset/{userId}")
    public ResponseEntity<Void> resetBudget(
            @PathVariable String userId,
            @RequestParam String period) {
        log.info("Resetting budget for user: {}, period: {}", userId, period);
        try {
            budgetService.resetBudget(userId, com.im.backend.security.differentialprivacy.entity.PrivacyBudgetEntity.BudgetPeriod.valueOf(period));
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/unblock/{budgetId}")
    public ResponseEntity<Void> unblockBudget(@PathVariable Long budgetId) {
        log.info("Unblocking budget: {}", budgetId);
        budgetService.unblockBudget(budgetId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/warning/threshold")
    public ResponseEntity<List<PrivacyBudgetEntity>> getBudgetsBelowThreshold(@RequestParam Double threshold) {
        log.info("Getting budgets below threshold: {}", threshold);
        return ResponseEntity.ok(budgetService.getBudgetsBelowThreshold(threshold));
    }
    
    @GetMapping("/blocked")
    public ResponseEntity<List<PrivacyBudgetEntity>> getBlockedBudgets() {
        log.info("Getting blocked budgets");
        return ResponseEntity.ok(budgetService.getBlockedBudgets());
    }
    
    @GetMapping("/blocked/count")
    public ResponseEntity<Map<String, Long>> getBlockedBudgetCount() {
        log.info("Getting blocked budget count");
        Map<String, Long> result = new HashMap<>();
        result.put("count", budgetService.getBlockedBudgetCount());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/user/{userId}/page")
    public ResponseEntity<Page<PrivacyBudgetEntity>> getUserBudgetsPage(
            @PathVariable String userId,
            Pageable pageable) {
        log.info("Getting budgets page for user: {}", userId);
        return ResponseEntity.ok(budgetService.getUserBudgetsPage(userId, pageable));
    }
    
    @GetMapping("/violations")
    public ResponseEntity<List<PrivacyBudgetEntity>> getBudgetsWithViolations(Pageable pageable) {
        log.info("Getting budgets with violations");
        return ResponseEntity.ok(budgetService.getBudgetsWithViolations(pageable));
    }
    
    @GetMapping("/stats/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String userId) {
        log.info("Getting stats for user: {}", userId);
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalConsumed", budgetService.getTotalConsumedBudgetByUser(userId));
        stats.put("remainingPercentage", budgetService.getRemainingBudgetPercentage(userId, BudgetType.USER_LIFETIME));
        return ResponseEntity.ok(stats);
    }
    
    @PostMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkBudget(
            @RequestParam String userId,
            @RequestParam String budgetType,
            @RequestParam Double epsilon) {
        log.info("Checking budget sufficiency for user: {}, epsilon: {}", userId, epsilon);
        try {
            BudgetType type = BudgetType.valueOf(budgetType);
            Map<String, Boolean> result = new HashMap<>();
            result.put("sufficient", budgetService.hasSufficientBudget(userId, type, epsilon));
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}