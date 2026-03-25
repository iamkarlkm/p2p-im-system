package com.im.backend.security.differentialprivacy.repository;

import com.im.backend.security.differentialprivacy.entity.PrivacyBudgetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 隐私预算仓储接口
 */
@Repository
public interface PrivacyBudgetRepository extends JpaRepository<PrivacyBudgetEntity, Long> {
    
    Optional<PrivacyBudgetEntity> findByUserIdAndBudgetType(String userId, String budgetType);
    
    Optional<PrivacyBudgetEntity> findBySessionIdAndBudgetType(String sessionId, String budgetType);
    
    List<PrivacyBudgetEntity> findByUserId(String userId);
    
    List<PrivacyBudgetEntity> findByBudgetType(String budgetType);
    
    List<PrivacyBudgetEntity> findByIsBlockedTrue();
    
    Page<PrivacyBudgetEntity> findByUserId(String userId, Pageable pageable);
    
    @Query("SELECT pb FROM PrivacyBudgetEntity pb WHERE pb.remainingBudget < (pb.totalBudget * :threshold)")
    List<PrivacyBudgetEntity> findByRemainingBudgetBelowThreshold(@Param("threshold") Double threshold);
    
    @Query("SELECT pb FROM PrivacyBudgetEntity pb WHERE pb.periodEnd < :currentTime AND pb.budgetPeriod != 'LIFETIME'")
    List<PrivacyBudgetEntity> findExpiredBudgets(@Param("currentTime") LocalDateTime currentTime);
    
    @Query("SELECT pb FROM PrivacyBudgetEntity pb WHERE pb.lastConsumedAt > :since")
    List<PrivacyBudgetEntity> findRecentlyActive(@Param("since") LocalDateTime since);
    
    @Modifying
    @Transactional
    @Query("UPDATE PrivacyBudgetEntity pb SET pb.remainingBudget = pb.totalBudget, pb.consumedBudget = 0.0, pb.consumptionCount = 0 WHERE pb.userId = :userId AND pb.budgetPeriod = :period")
    void resetUserBudgetForPeriod(@Param("userId") String userId, @Param("period") String period);
    
    @Modifying
    @Transactional
    @Query("UPDATE PrivacyBudgetEntity pb SET pb.isBlocked = false, pb.blockReason = NULL WHERE pb.id = :id")
    void unblockBudget(@Param("id") Long id);
    
    @Query("SELECT AVG(pb.consumedBudget) FROM PrivacyBudgetEntity pb WHERE pb.budgetType = :budgetType")
    Double getAverageConsumedBudgetByType(@Param("budgetType") String budgetType);
    
    @Query("SELECT COUNT(pb) FROM PrivacyBudgetEntity pb WHERE pb.isBlocked = true")
    Long countBlockedBudgets();
    
    @Query("SELECT pb FROM PrivacyBudgetEntity pb WHERE pb.violationCount > 0 ORDER BY pb.violationCount DESC")
    List<PrivacyBudgetEntity> findBudgetsWithViolations(Pageable pageable);
    
    @Query("SELECT pb FROM PrivacyBudgetEntity pb WHERE pb.consumptionCount = 0 AND pb.createdAt < :threshold")
    List<PrivacyBudgetEntity> findUnusedBudgets(@Param("threshold") LocalDateTime threshold);
    
    @Query("SELECT pb.budgetType, COUNT(pb) FROM PrivacyBudgetEntity pb GROUP BY pb.budgetType")
    List<Object[]> countByBudgetType();
    
    @Query("SELECT SUM(pb.consumedBudget) FROM PrivacyBudgetEntity pb WHERE pb.userId = :userId")
    Double getTotalConsumedBudgetByUser(@Param("userId") String userId);
}