package com.im.backend.security.differentialprivacy.service;

import com.im.backend.security.differentialprivacy.entity.PrivacyBudgetEntity;
import com.im.backend.security.differentialprivacy.entity.PrivacyBudgetEntity.BudgetPeriod;
import com.im.backend.security.differentialprivacy.entity.PrivacyBudgetEntity.BudgetType;
import com.im.backend.security.differentialprivacy.repository.PrivacyBudgetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 隐私预算服务
 * 管理隐私预算的分配、消耗和监控
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PrivacyBudgetService {
    
    private final PrivacyBudgetRepository budgetRepository;
    
    @Transactional(readOnly = true)
    public Optional<PrivacyBudgetEntity> getUserBudget(String userId, BudgetType budgetType) {
        return budgetRepository.findByUserIdAndBudgetType(userId, budgetType.name());
    }
    
    @Transactional(readOnly = true)
    public Optional<PrivacyBudgetEntity> getSessionBudget(String sessionId, BudgetType budgetType) {
        return budgetRepository.findBySessionIdAndBudgetType(sessionId, budgetType.name());
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyBudgetEntity> getAllUserBudgets(String userId) {
        return budgetRepository.findByUserId(userId);
    }
    
    @Transactional
    public PrivacyBudgetEntity createBudget(PrivacyBudgetEntity budget) {
        log.info("Creating privacy budget for user: {}, type: {}", budget.getUserId(), budget.getBudgetType());
        return budgetRepository.save(budget);
    }
    
    @Transactional
    public PrivacyBudgetEntity consumeBudget(String userId, BudgetType budgetType, Double epsilon) {
        log.info("Consuming privacy budget for user: {}, epsilon: {}", userId, epsilon);
        
        return getUserBudget(userId, budgetType)
            .map(budget -> {
                if (!budget.canConsume(epsilon)) {
                    throw new RuntimeException("Insufficient privacy budget. Remaining: " + budget.getRemainingBudget());
                }
                
                budget.consumeBudget(epsilon);
                return budgetRepository.save(budget);
            })
            .orElseThrow(() -> new RuntimeException("Budget not found for user: " + userId));
    }
    
    @Transactional
    public void resetBudget(String userId, BudgetPeriod period) {
        log.info("Resetting budget for user: {}, period: {}", userId, period);
        budgetRepository.resetUserBudgetForPeriod(userId, period.name());
    }
    
    @Transactional
    public void unblockBudget(Long budgetId) {
        log.info("Unblocking budget: {}", budgetId);
        budgetRepository.unblockBudget(budgetId);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyBudgetEntity> getBudgetsBelowThreshold(Double threshold) {
        return budgetRepository.findByRemainingBudgetBelowThreshold(threshold);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyBudgetEntity> getExpiredBudgets(LocalDateTime currentTime) {
        return budgetRepository.findExpiredBudgets(currentTime);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyBudgetEntity> getRecentlyActive(LocalDateTime since) {
        return budgetRepository.findRecentlyActive(since);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyBudgetEntity> getBlockedBudgets() {
        return budgetRepository.findByIsBlockedTrue();
    }
    
    @Transactional(readOnly = true)
    public Long getBlockedBudgetCount() {
        return budgetRepository.countBlockedBudgets();
    }
    
    @Transactional(readOnly = true)
    public Page<PrivacyBudgetEntity> getUserBudgetsPage(String userId, Pageable pageable) {
        return budgetRepository.findByUserId(userId, pageable);
    }
    
    @Transactional(readOnly = true)
    public List<PrivacyBudgetEntity> getBudgetsWithViolations(Pageable pageable) {
        return budgetRepository.findBudgetsWithViolations(pageable);
    }
    
    @Transactional(readOnly = true)
    public Double getAverageConsumedBudgetByType(BudgetType budgetType) {
        return budgetRepository.getAverageConsumedBudgetByType(budgetType.name());
    }
    
    @Transactional(readOnly = true)
    public Double getTotalConsumedBudgetByUser(String userId) {
        return budgetRepository.getTotalConsumedBudgetByUser(userId);
    }
    
    @Transactional(readOnly = true)
    public boolean hasSufficientBudget(String userId, BudgetType budgetType, Double epsilon) {
        return getUserBudget(userId, budgetType)
            .map(budget -> budget.canConsume(epsilon))
            .orElse(false);
    }
    
    @Transactional(readOnly = true)
    public double getRemainingBudgetPercentage(String userId, BudgetType budgetType) {
        return getUserBudget(userId, budgetType)
            .map(budget -> (budget.getRemainingBudget() / budget.getTotalBudget()) * 100.0)
            .orElse(0.0);
    }
}